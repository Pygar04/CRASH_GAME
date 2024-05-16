package crash;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Punti {
    private List<Rectangle> punti;
    private int pointSize;
    private int mapWidth;
    private int mapHeight;
    private Random random;
    private Rectangle centralBox;

    public Punti(int mapWidth, int mapHeight) {
        this.punti = new ArrayList<>();
        this.pointSize = 5; // Dimensione ridotta del punto
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.random = new Random();

        // Definisci il riquadro centrale
        int boxWidth = 262;
        int boxHeight = 289;
        int boxX = (mapWidth - boxWidth) / 2;
        int boxY = (mapHeight - boxHeight) / 2;
        this.centralBox = new Rectangle(boxX, boxY, boxWidth, boxHeight);

        generatePunti();
    }

    private void generatePunti() {
        int corsiaSize = 51; // Larghezza di una corsia tra due muri
        int numCorsieOrizzontali = mapWidth / corsiaSize;
        int numCorsieVerticali = mapHeight / corsiaSize;
        
        for (int i = 0; i < 100; i++) { // Genera 100 punti casuali sulla mappa
            int x, y;
            Rectangle punto;
            do {
                int corsiaX = random.nextInt(numCorsieOrizzontali);
                int corsiaY = random.nextInt(numCorsieVerticali);

                x = corsiaX * corsiaSize + (corsiaSize - pointSize) / 2;
                y = corsiaY * corsiaSize + (corsiaSize - pointSize) / 2;
                punto = new Rectangle(x, y, pointSize, pointSize);
            } while (centralBox.intersects(punto) || isOnWall(x, y, corsiaSize));

            punti.add(punto);
        }
    }

    private boolean isOnWall(int x, int y, int corsiaSize) {
        // Controlla se il punto si trova su un muro
        // Puoi personalizzare questa logica in base alla tua implementazione dei muri nella mappa
        return x % corsiaSize == 0 || y % corsiaSize == 0;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        for (Rectangle punto : punti) {
            g.fillRect(punto.x, punto.y, punto.width, punto.height);
        }
    }

    public void checkCollisions(Player player) {
        Rectangle playerBounds = player.getBounds();
        punti.removeIf(punto -> {
            boolean intersects = punto.intersects(playerBounds);
            if (intersects) {
                player.incrementScore();
            }
            return intersects;
        });
    }
}
