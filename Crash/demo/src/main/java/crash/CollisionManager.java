package crash;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.awt.Point;

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
        return result;
    }
    
    private boolean isFree(Rectangle area) {
        int startX = Math.max(0, area.x);
        int endX = Math.min(mapImage.getWidth() - 1, area.x + area.width - 1);
        int startY = Math.max(0, area.y);
        int endY = Math.min(mapImage.getHeight() - 1, area.y + area.height - 1);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (wallPixels.contains(new Point(x, y))) {
        
                    return false; // Se un pixel del muro è nell'area, l'area non è libera
                }
            }
        }
        return true; // Se nessun pixel del muro è nell'area, l'area è libera
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

