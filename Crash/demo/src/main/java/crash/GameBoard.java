package crash;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;

public class GameBoard extends JPanel {
    private Player player;
    private Image liveImage, explosionImage;
    private Enemy enemy;
    private Punti punti;
    private Map gameMap;
    private CollisionManager collisionManager;
    private KeyboardManager keyboardManager;
    private ExecutorService executorService;
    private JButton restartButton;
    private JButton pauseButton;

    private static final String EXPLOSION = "/explosion.png";
    private static final String LIVE = "/live.png";
    private static final int IMAGE_MARGIN = 10; // Distanza tra le immagini delle vite

    public GameBoard() { // Costruttore 
        setBackground(Color.BLACK);
        this.gameMap = new Map();
        this.collisionManager = new CollisionManager(gameMap);
        this.player = new Player(collisionManager, gameMap);
        this.enemy = new Enemy(collisionManager, gameMap);
        this.punti = new Punti(gameMap.getWeightMap(), gameMap.getHeightMap());
        this.keyboardManager = new KeyboardManager(player);

        addKeyListener(keyboardManager);
        setFocusable(true);
        requestFocusInWindow();
        initButtons();

        executorService = Executors.newFixedThreadPool(2); // Crea un servizio executor con un pool di thread fisso di dimensione 2
        executorService.execute(player); // Avvia l'esecuzione del task del giocatore in un thread separato
        executorService.execute(enemy); // Avvia l'esecuzione del task del nemico in un thread separato

        // Carica le immagini
        loadExplosion();
        loadLive();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameMap.draw(g, this);  // Disegna la mappa come sfondo
        punti.draw(g);  // Disegna i punti sulla mappa
        player.draw(g);  // Disegna il giocatore
        enemy.draw(g);  // Disegna il nemico

        // Disegna il centro del background come nell'immagine fornita
        drawCenteredBackground(g);
    }

    private void drawCenteredBackground(Graphics g) {
        g.setColor(Color.YELLOW);
        Font font = new Font("Monospaced", Font.BOLD, 16); // Font size regolato
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Dimensioni del riquadro
        int boxWidth = 262;
        int boxHeight = 289;

        // Calcolo delle posizioni del riquadro
        int boxX = (panelWidth - boxWidth) / 2;
        int boxY = (panelHeight - boxHeight) / 2;

        // Calcolo delle posizioni del testo
        int centerX = boxX + boxWidth / 2;
        int currentY = boxY + 40;

        // Disegna il testo "GAME OVER"
        if(player.getLives() == 0){
            String gameOverText = "GAME OVER";
            int gameOverWidth = metrics.stringWidth(gameOverText);
            g.drawString(gameOverText, centerX - gameOverWidth / 2, currentY);
        }
        // Disegna il testo "TOP SCORE"
        currentY += 30;
        String ScoreText = "SCORE";
        int ScoreWidth = metrics.stringWidth(ScoreText);
        g.drawString(ScoreText, centerX - ScoreWidth / 2, currentY);

        // Disegna il punteggio
        currentY += 30;
        String scoreText = String.valueOf(player.getScore());
        int scoreWidth = metrics.stringWidth(scoreText);
        g.drawString(scoreText, centerX - scoreWidth / 2, currentY);

        // Disegna i numeri dei giocatori e i loro punteggi
        currentY += 50;
        String livesText = "LIVES";
        int livesWidth = metrics.stringWidth(livesText);
        g.drawString(livesText, centerX - 60 - livesWidth / 2, currentY);
        
        // Disegna le immagini delle vite
        for (int i = 0; i < player.getLives(); i++) {
            g.drawImage(liveImage, centerX + 10 + (i * (liveImage.getWidth(null) + IMAGE_MARGIN)), currentY - liveImage.getHeight(null) + 15, null);
        }
        

        currentY += 50;
        String bestScoreText = "TOP SCORE";
        int bestScoreWidth = metrics.stringWidth(bestScoreText);
        g.drawString(bestScoreText, centerX - 60 - bestScoreWidth / 2, currentY);
        g.drawString("0", centerX + 40, currentY);

        // Posiziona i pulsanti
        restartButton.setBounds(centerX - 110, currentY + 50, 100, 30);
        pauseButton.setBounds(centerX + 10, currentY + 50, 100, 30);
    }

    private void initButtons() {
        // Configura il font per i pulsanti
        Font font = new Font("Monospaced", Font.BOLD, 16);
        LineBorder yellowBorder = new LineBorder(Color.YELLOW, 2); // Crea un bordo giallo

        // Crea e configura il pulsante RESTART
        restartButton = new JButton("RESTART");
        restartButton.setFont(font);
        restartButton.setForeground(Color.YELLOW);
        restartButton.setBackground(Color.BLACK);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(yellowBorder); // Imposta il bordo giallo
        add(restartButton);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartButton();
            }
            
        });
        

        // Crea e configura il pulsante PAUSE
        pauseButton = new JButton("PAUSE");
        pauseButton.setFont(font);
        pauseButton.setForeground(Color.YELLOW);
        pauseButton.setBackground(Color.BLACK);
        pauseButton.setFocusPainted(false);
        pauseButton.setBorder(yellowBorder); // Imposta il bordo giallo
        add(pauseButton);


        // Posiziona i pulsanti
        setLayout(null);
    }

    public void updateGame() {
        if(collisionManager.handleCollisions(player, enemy)) {
            executorService.shutdownNow();
            loadExplosion();
            player.loseLife();
            //restart();
        }
        // Gestisci le collisioni
        punti.checkCollisions(player); // Verifica se il player raccoglie un punto
        repaint(); // Ridisegna il pannello di gioco con le nuove posizioni
    }

    public boolean stopGame() {
        if (player.getLives() <= 0){
            executorService.shutdownNow(); // Stop all running tasks
            return true;
            }
        return false;
    }

    private void loadExplosion(){
        try {
            explosionImage = ImageIO.read(getClass().getResourceAsStream(EXPLOSION));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dell'immagine", "Errore Immagine", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLive(){
        try {
            liveImage = ImageIO.read(getClass().getResourceAsStream(LIVE));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dell'immagine", "Errore Immagine", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void restart(){
        player.restart();
        enemy.restart();
        punti.restart();
        repaint();
        executorService = Executors.newFixedThreadPool(2);
        executorService.execute(player);
        executorService.execute(enemy);
    }

    public void restartButton(){
        restartButton.addActionListener(e -> {
            player.restart();
            player.setLives();
            player.setSpeed();
            enemy.restart();
            enemy.setSpeed();
            punti.restart();
            repaint();
            executorService = Executors.newFixedThreadPool(2);
            executorService.execute(player);
            executorService.execute(enemy);
        });
    }
}
