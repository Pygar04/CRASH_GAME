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

public class GameBoard extends JPanel {
    private Player player;
    private Image image;
    private Enemy enemy;
    private Punti punti;
    private Map gameMap;
    private CollisionManager collisionManager;
    private KeyboardManager keyboardManager;
    private ExecutorService executorService;

    private static final String EXPLOSION = "/explosion.png";

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

        executorService = Executors.newFixedThreadPool(2); // Crea un servizio executor con un pool di thread fisso di dimensione 2
        executorService.execute(player); // Avvia l'esecuzione del task del giocatore in un thread separato
        executorService.execute(enemy); // Avvia l'esecuzione del task del nemico in un thread separato
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
        String topScoreText = "TOP SCORE";
        int topScoreWidth = metrics.stringWidth(topScoreText);
        g.drawString(topScoreText, centerX - topScoreWidth / 2, currentY);

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
        String lives = String.valueOf(player.getLives());
        g.drawString(lives, centerX + 40, currentY);

        currentY += 30;
        String bestScoreText = "BEST SCORE";
        int bestScoreWidth = metrics.stringWidth(bestScoreText);
        g.drawString(bestScoreText, centerX - 60 - bestScoreWidth / 2, currentY);
        g.drawString("0", centerX + 40, currentY);

        // Disegna le righe "RESTART" e "PAUSE"
        currentY += 50;
        String restartText = "RESTART";
        int restartWidth = metrics.stringWidth(restartText);
        g.drawString(restartText, centerX - 60 - restartWidth / 2, currentY);

        String pauseText = "PAUSE";
        int pauseWidth = metrics.stringWidth(pauseText);
        g.drawString(pauseText, centerX + 60 - pauseWidth / 2, currentY);

    }

    public void updateGame() {
        if(collisionManager.handleCollisions(player, enemy));{
            loadImage();
        }
              // Gestisci le collisioni
        punti.checkCollisions(player); // Verifica se il player raccoglie un punto
        repaint(); // Ridisegna il pannello di gioco con le nuove posizioni
    }

    public void stopGame() {
        if (player.getLives() == 0)
            executorService.shutdownNow(); // Stop all running tasks
    }

    public void loadImage(){
        try {
            image = ImageIO.read(getClass().getResourceAsStream(EXPLOSION));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dell'immagine", "Errore Immagine", JOptionPane.ERROR_MESSAGE);
        }
    }
}
