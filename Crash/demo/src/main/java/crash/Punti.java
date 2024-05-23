package crash;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Punti {
    
    private List<Rectangle> punti; // Lista dei punti presenti nel gioco
   
    private int pointSize;  // Dimensione di ogni punto
    // Dimensioni della mappa di gioco
    private int mapWidth;
    private int mapHeight;

    private Rectangle centralBox; // Riquadro centrale della mappa

    private SoundManager pointSound;

    private static final String POINT_SOUND = "/Sound/point.wav";

    // Costruttore della classe Punti
    public Punti(int mapWidth, int mapHeight) {
        this.punti = new ArrayList<>();
        this.pointSize = 3; // Dimensione ridotta del punto
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Definisci il riquadro centrale
        int boxWidth = 262;
        int boxHeight = 289;
        int boxX = (mapWidth - boxWidth) / 2;
        int boxY = (mapHeight - boxHeight) / 2;
        this.centralBox = new Rectangle(boxX, boxY, boxWidth, boxHeight);

        // Genera i punti
        generatePunti();
    }

    // Metodo per generare i punti
    private void generatePunti() {
        int corsiaSize = 50; // Larghezza di una corsia tra due muri
        
        // Crea i punti in ogni corsia
        for (int y = 0; y < mapHeight; y += corsiaSize) {
            for (int x = 0; x < mapWidth; x += corsiaSize) {
                int puntoX = x + (corsiaSize - pointSize) / 2;
                int puntoY = y + (corsiaSize - pointSize) / 2; 
                Rectangle punto = new Rectangle(puntoX, puntoY, pointSize, pointSize);
                
                // Aggiungi il punto alla lista solo se non interseca il riquadro centrale e non si trova su un muro
                if (!centralBox.intersects(punto) && !isOnWall(puntoX, puntoY, corsiaSize)) {
                    punti.add(punto);
                }
            }
        }
    }

    // Metodo per verificare se un punto si trova su un muro
    private boolean isOnWall(int x, int y, int corsiaSize) {
        return x % corsiaSize < pointSize || y % corsiaSize < pointSize;
    }

    // Metodo per disegnare i punti
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        for (Rectangle punto : punti) {
            g.fillRect(punto.x, punto.y, punto.width, punto.height);
        }
    }

    // Metodo per verificare le collisioni tra il giocatore e i punti
    public void checkCollisions(Player player) {
        pointSound = new SoundManager(POINT_SOUND);
        Rectangle playerBounds = player.getBounds();
        Iterator<Rectangle> iterator = punti.iterator();
        while (iterator.hasNext()) {
            Rectangle punto = iterator.next();
            // Se il giocatore interseca un punto, incrementa il punteggio del giocatore e rimuovi il punto
            if (punto.intersects(playerBounds)) {
                pointSound.play();
                player.incrementScore();
                iterator.remove();
            }
        }
    }

    // Metodo per riavviare i punti
    public void restart(){
        punti.clear();
        generatePunti();
    }
}