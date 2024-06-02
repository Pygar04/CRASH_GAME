package crash;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.awt.Point;
import java.io.File;
import javax.imageio.ImageIO;

public class CollisionManager {
    private BufferedImage mapImage; // L'immagine della mappa
    private Map map;
    private Set<Point> wallPixels = new HashSet<>(); // I pixel che rappresentano i muri
    private static final Color TARGET_COLOR = new Color(0, 0, 255); // Il colore blu che rappresenta i muri

    public CollisionManager(Map map) {
        this.map = map;
        this.mapImage = map.getMapImage();  // Usa l'immagine della mappa dalla classe Map
        analyzeImageForWalls(); // Analizza l'immagine per trovare i muri
    }

    private void analyzeImageForWalls() {
        int width = map.getWidthMap(); // Larghezza dell'immagine
        int height = map.getHeighMap(); // Altezza dell'immagine
        for (int y = 0; y < height; y++) { // Itera sull'altezza
            for (int x = 0; x < width; x++) { // Itera sulla larghezza
                if (isWallColor(mapImage.getRGB(x, y))) {
                    wallPixels.add(new Point(x, y)); // Aggiunge il pixel al set dei pixel dei muri
                }
            }
        }
    }

    private boolean isWallColor(int color) {
        Color pixelColor = new Color(color, true);
        return pixelColor.equals(TARGET_COLOR); // Controlla se il colore del pixel è il colore dei muri
    }

    public boolean canMove(Rectangle intendedPosition) {
        boolean result = isFree(intendedPosition);
        if (!result) {
            saveDebugImageWithCollisions(intendedPosition); // Salva l'immagine di debug se non può muoversi
        }
        return result;
    }
    
    private boolean isFree(Rectangle area) {
        int startX = Math.max(0, area.x);
        int endX = Math.min(mapImage.getWidth() - 1, area.x + area.width - 1);
        int startY = Math.max(0, area.y);
        int endY = Math.min(mapImage.getHeight() - 1, area.y + area.height - 1);

        System.out.println("Checking area: " + area);
        System.out.println("Checking coordinates from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (wallPixels.contains(new Point(x, y))) {
                    System.out.println("Collision found at: (" + x + "," + y + ")");
                    return false; // Se un pixel del muro è nell'area, l'area non è libera
                }
            }
        }
        return true; // Se nessun pixel del muro è nell'area, l'area è libera
    }

    private void saveDebugImageWithCollisions(Rectangle area) {
        try {
            BufferedImage debugImage = new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Disegna i muri
            for (Point p : wallPixels) {
                debugImage.setRGB(p.x, p.y, Color.BLUE.getRGB()); // Colore dei muri
            }

            // Disegna l'area di controllo
            for (int x = area.x; x < area.x + area.width; x++) {
                for (int y = area.y; y < area.y + area.height; y++) {
                    if (x >= 0 && x < debugImage.getWidth() && y >= 0 && y < debugImage.getHeight()) {
                        debugImage.setRGB(x, y, Color.GREEN.getRGB()); // Colore dell'area di controllo
                    }
                }
            }

            // Disegna le collisioni trovate
            for (int x = area.x; x < area.x + area.width; x++) {
                for (int y = area.y; y < area.y + area.height; y++) {
                    if (x >= 0 && x < debugImage.getWidth() && y >= 0 && y < debugImage.getHeight() && wallPixels.contains(new Point(x, y))) {
                        debugImage.setRGB(x, y, Color.RED.getRGB()); // Colore delle collisioni
                    }
                }
            }

            File outputfile = new File("collisionDebug.png");
            ImageIO.write(debugImage, "png", outputfile);
            System.out.println("Immagine di debug delle collisioni salvata come collisionDebug.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean handleCollisions(Player player, Enemy enemy) {
        if (player.getBounds().intersects(enemy.getBounds())) // Se il giocatore e il nemico si intersecano
             return true; 
        return false;
    }

    public Point getCollisionPoint(Player player, Enemy enemy) {
        if (player.getBounds().intersects(enemy.getBounds())) {
            // Restituisce il punto medio della collisione
            int collisionX = (player.getBounds().x + enemy.getBounds().x) / 2;
            int collisionY = (player.getBounds().y + enemy.getBounds().y) / 2;
            return new Point(collisionX, collisionY);
        }
        return null;
    }
}

