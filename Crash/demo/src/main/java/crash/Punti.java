package crash;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.awt.image.BufferedImage;

public class Punti {

    private List<Rectangle> punti; // Lista dei punti presenti nel gioco
    private BufferedImage mapImage; // Immagine della mappa di gioco
    private Map map; // Map object
    private int pointSize;  // Dimensione di ogni punto
    private int mapWidth; // Dimensioni della mappa di gioco
    private int mapHeight;
    private Rectangle centralBox; // Riquadro centrale della mappa
    private SoundManager pointSound;
    private static final String POINT_SOUND = "/Sound/point.wav";
    private static final int PUNTI = 160; // Numero costante di punti

    // Costruttore della classe Punti
    public Punti(int mapWidth, int mapHeight, BufferedImage mapImage, Map map) {
        this.punti = new ArrayList<>();
        this.mapImage = mapImage;
        this.map = map; // Initialize the map object
        this.pointSize = 3; // Dimensione ridotta del punto
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Definisci il riquadro centrale
        int boxWidth = 264;
        int boxHeight = 264;
        int boxX = (mapWidth - boxWidth) / 2;
        int boxY = (mapHeight - boxHeight) / 2;
        this.centralBox = new Rectangle(boxX, boxY, boxWidth, boxHeight);

        // Genera i punti
        generatePunti();

        // Inizializza il suono dei punti
        pointSound = new SoundManager(POINT_SOUND);
    }

    // Metodo per generare i punti
    private void generatePunti() {
        int corsiaSize = 50; // Larghezza di una corsia tra due muri
        int puntiGenerati = 0; // Contatore dei punti generati

        // Crea i punti fino a quando non ne hai generati il numero desiderato o finché non hai esaurito gli spazi
        while (puntiGenerati < PUNTI) {
            for (int y = 0; y < mapHeight && puntiGenerati < PUNTI; y += corsiaSize) {
                for (int x = 0; x < mapWidth && puntiGenerati < PUNTI; x += corsiaSize) {
                    int puntoX = x + (corsiaSize - pointSize) / 2;
                    int puntoY = y + (corsiaSize - pointSize) / 2;
                    Rectangle punto = new Rectangle(puntoX, puntoY, pointSize, pointSize);

                    // Controlla se il punto non interseca il riquadro centrale e non si trova su un muro o un pixel rosso
                    if (!centralBox.intersects(punto) && !isOnWall(puntoX, puntoY, corsiaSize) && !map.isRedPixel(puntoX, puntoY)) {
                        punti.add(punto);
                        puntiGenerati++; // Incrementa il contatore dei punti generati
                    }
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
    public void restart() {
        punti.clear();
        generatePunti();
    }
}
