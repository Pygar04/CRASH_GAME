package crash;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Punti {
    private List<Rectangle> punti;
    private int pointSize;
    private int mapWidth;
    private int mapHeight;
    private Rectangle centralBox;

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

        generatePunti();
    }

    private void generatePunti() {
        int corsiaSize = 50; // Larghezza di una corsia tra due muri
        
        for (int y = 0; y < mapHeight; y += corsiaSize) {
            for (int x = 0; x < mapWidth; x += corsiaSize) {
                int puntoX = x + (corsiaSize - pointSize) / 2;
                int puntoY = y + (corsiaSize - pointSize) / 2;
                Rectangle punto = new Rectangle(puntoX, puntoY, pointSize, pointSize);
                
                if (!centralBox.intersects(punto) && !isOnWall(puntoX, puntoY, corsiaSize)) {
                    punti.add(punto);
                }
            }
        }
    }

    private boolean isOnWall(int x, int y, int corsiaSize) {
        // Controlla se il punto si trova su un muro
        return x % corsiaSize < pointSize || y % corsiaSize < pointSize;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
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

    public void restart(){
        punti.clear();
        generatePunti();
    }
}
