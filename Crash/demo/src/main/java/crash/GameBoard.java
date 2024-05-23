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
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Point;

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
    private int topScore;
    private boolean isPaused = false;
    private SoundManager explosionSound;
    private SoundManager gameOverSound;

    private SoundManager countdownSound;

    private int explosionX, explosionY;
    private boolean showExplosion = false;

    private static final String EXPLOSION = "/Image/explosion.png";
    private static final String LIVE = "/Image/live.png";
    private static final int IMAGE_MARGIN = 10; // Distanza tra le immagini delle vite

    private static final String EXPLOSION_SOUND = "/Sound/explosion.wav";
    private static final String GAME_OVER_SOUND = "/Sound/gameover.wav";
    private static final String COUNTDOWN_SOUND = "/Sound/countdown.wav";

    public GameBoard() { // Costruttore
        setBackground(Color.BLACK);
        this.gameMap = new Map();
        this.collisionManager = new CollisionManager(gameMap);
        this.player = new Player(collisionManager, gameMap);
        this.enemy = new Enemy(collisionManager, gameMap);
        this.punti = new Punti(gameMap.getWeightMap(), gameMap.getHeightMap());
        this.keyboardManager = new KeyboardManager(player);
        this.topScore = 0;
    
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
        // Disegna il centro del background
        drawCenteredBackground(g);
        // Disegna l'esplosione
        drawExplosion(g);
        // Disegna il menu di pausa
        drawPauseMenu(g);
    }



    private void drawExplosion(Graphics g) {
        if (showExplosion) {
            System.out.println("Disegno l'esplosione alle coordinate: (" + explosionX + ", " + explosionY + ")");
            g.drawImage(explosionImage, explosionX - explosionImage.getWidth(null) / 2, explosionY - explosionImage.getHeight(null) / 2, null);
        }
    }
    
    private void drawPauseMenu(Graphics g) {
        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 127)); // Colore nero semitrasparente
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int boxWidth = 262;
            int boxHeight = 289;
            int boxX = (panelWidth - boxWidth) / 2;
            int boxY = (panelHeight - boxHeight) / 2;
            g.fillRect(0, 0, panelWidth, boxY); // Parte superiore
            g.fillRect(0, boxY, boxX, boxHeight); // Parte sinistra
            g.fillRect(boxX + boxWidth, boxY, panelWidth - boxX - boxWidth, boxHeight); // Parte destra
            g.fillRect(0, boxY + boxHeight, panelWidth, panelHeight - boxY - boxHeight); // Parte inferiore
        }
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

        // Disegna il testo "GAME OVER" solo se isGameOverVisible è true
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
        
        // Disegna il punteggio massimo
        currentY += 50;
        String bestScoreText = "TOP SCORE";
        int bestScoreWidth = metrics.stringWidth(bestScoreText);
        g.drawString(bestScoreText, centerX - 60 - bestScoreWidth / 2, currentY);
        if(player.getScore() > topScore){
            topScore = player.getScore();
        }
        String TopscoreText = String.valueOf(topScore);
        g.drawString(TopscoreText, centerX + 40, currentY);

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
        restartButton.addActionListener(e -> {
            player.restart();
            player.initScore();
            enemy.initSpeed();
            player.initLives();
            player.initSpeed();
            enemy.restart();
            punti.restart();
            repaint();
            executorService = Executors.newFixedThreadPool(2);
            executorService.execute(player);
            executorService.execute(enemy);
        });
    
        // Crea e configura il pulsante PAUSE
        pauseButton = new JButton("PAUSE");
        pauseButton.setFont(font);
        pauseButton.setForeground(Color.YELLOW);
        pauseButton.setBackground(Color.BLACK);
        pauseButton.setFocusPainted(false);
        pauseButton.setBorder(yellowBorder);
        add(pauseButton);
        pauseButton.addActionListener(e -> {
            if (player.getActive()) {
                player.setActive(false);
                enemy.setActive(false);
                isPaused = true;
                pauseButton.setText("RESUME");
            } else {
                player.setActive(true);
                enemy.setActive(true);
                isPaused = false;
                executorService.execute(player);
                executorService.execute(enemy);
                pauseButton.setText("PAUSE");
            }
            repaint(); // Aggiunge questa chiamata per assicurarsi che il pannello venga ridisegnato quando il gioco viene messo in pausa o ripreso
        });
    
        // Posiziona i pulsanti
        setLayout(null);
    }

    // 
    private void handleCollision() {
        explosionSound = new SoundManager(EXPLOSION_SOUND);
        if (collisionManager.handleCollisions(player, enemy)) {
            executorService.shutdownNow();
            player.loseLife();
            if(player.getLives() >= 0){
                Point collisionPoint = collisionManager.getCollisionPoint(player, enemy);
                explosionX = collisionPoint.x;
                explosionY = collisionPoint.y;
                showExplosion = true;
                repaint();
                explosionSound.play();
            }
            try {
                Thread.sleep(3000); // Pause the thread for 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showExplosion = false;
            if (stopGame()) {
                repaint();
                executorService.shutdownNow();
                return;
            }
            restart();
        }
    }

    public void updateGame() {
        handleCollision(); //commento 
        punti.checkCollisions(player); // Verifica se il player raccoglie un punto
        if (player.getLives() > 0)
            repaint();
    }
    

    public boolean stopGame() {
        gameOverSound = new SoundManager(GAME_OVER_SOUND);
        if (player.getLives() == 0){
            showExplosion = false;
            gameOverSound.play();
            return true;
            }
        return false;
    }


    private void loadExplosion() {
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
        if (player.getLives() > 0) {
            player.restart();
            enemy.restart();
            punti.restart();
            repaint();
            executorService = Executors.newFixedThreadPool(2);
            executorService.execute(player);
            executorService.execute(enemy);
        }
    }
}
