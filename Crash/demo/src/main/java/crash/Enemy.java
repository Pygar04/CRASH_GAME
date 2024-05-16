package crash;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Enemy implements Runnable {
    // Costanti per le direzioni di movimento
    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_DOWN = 3;

    // Variabili di istanza
    private int x, y, width, height, speed, direction;
    private BufferedImage imageMap;
    private int mapWidth, mapHeight;
    private Rectangle hitbox; // hitbox dell'enemy
    private Rectangle testa; // hitbox posizionato nella testa dell'enemy
    private Image enemyImage;
    private boolean active = true; // Stato attivo
    private CollisionManager collisionManager;

    // Percorsi delle immagini dell'enemy
    private static final String ENEMY_SX = "/enemySx.png";
    private static final String ENEMY_DX = "/enemyDx.png";
    private static final String ENEMY_UP = "/enemyUp.png";
    private static final String ENEMY_DOWN = "/enemyDown.png";

    public Enemy(CollisionManager collisionManager, Map map) {
        this.collisionManager = collisionManager;
        this.direction = DIRECTION_LEFT; // iniziamo con il nemico che si muove verso destra
        this.imageMap = map.getMapImage();
        updateEnemyImage();

        this.width = enemyImage.getWidth(null);
        this.height = enemyImage.getHeight(null);
        this.mapWidth = imageMap.getWidth();
        this.mapHeight = imageMap.getHeight();
        this.x = (mapWidth / 2) - 50 - width; // Centrato e spostato di 50 pixel a sinistra dal centro
        this.y = mapHeight - height - 50; // pixel sopra il bordo inferiore
        this.speed = 5; // Velocità di movimento predefinita
        this.hitbox = new Rectangle(x, y, width, height);
        this.testa = new Rectangle(x - width, y + height / 2, width, 1); // Inizializza la hitbox della testa
    }

    @Override
    public void run() {
        while (active) {
            move();
            try {
                Thread.sleep(16); // Approx. 60fps
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                active = false;
            }
        }
    }

    // Gestisce il movimento dell'enemy
    public void move() {
        if (!active) return;

        Rectangle newPosition = new Rectangle(hitbox);
        boolean canMoveHead = true;

        // Calcola la nuova posizione della testa
        switch (direction) {
            case DIRECTION_RIGHT:
                newPosition.x += speed;
                testa = new Rectangle((newPosition.x + newPosition.width), (newPosition.y + newPosition.height / 2), (newPosition.width), 1);
                canMoveHead = collisionManager.canMove(testa);
                break;
            case DIRECTION_DOWN:
                newPosition.y += speed;
                testa = new Rectangle(newPosition.x + (newPosition.width / 2), newPosition.y + newPosition.height, 1, newPosition.height);
                canMoveHead = collisionManager.canMove(testa);
                break;
            case DIRECTION_LEFT:
                newPosition.x -= speed;
                testa = new Rectangle(newPosition.x - newPosition.width/2, newPosition.y + newPosition.height / 2, (newPosition.width/2), 1);
                canMoveHead = collisionManager.canMove(testa);
                break;
            case DIRECTION_UP:
                newPosition.y -= speed;
                testa = new Rectangle(newPosition.x + (newPosition.width / 2), newPosition.y - newPosition.height, 1, newPosition.height/2);
                canMoveHead = collisionManager.canMove(testa);
                break;
        }

        // Verifica se la testa può muoversi
        canMoveHead = collisionManager.canMove(testa);

        // Se la testa non può muoversi, cambia direzione in senso orario
        if (!canMoveHead) {
            direction = (direction + 1) % 4;
            // Debug
            System.out.println("Nuova direzione Enemy: " + direction);
        } else {
            // Calcola la nuova posizione del corpo
            switch (direction) {
                case DIRECTION_RIGHT:
                    newPosition.x += speed;
                    break;
                case DIRECTION_DOWN:
                    newPosition.y += speed;
                    break;
                case DIRECTION_LEFT:
                    newPosition.x -= speed;
                    break;
                case DIRECTION_UP:
                    newPosition.y -= speed;
                    break;
            }

            // Verifica se il corpo può muoversi
            if (collisionManager.canMove(newPosition)) {
                hitbox = newPosition;
            }
        }

        updateEnemyImage(); // Aggiorna l'immagine del nemico per riflettere la possibile nuova direzione
    }

    // Aggiorna l'immagine del nemico in base alla direzione
    private void updateEnemyImage() {
        try {
            switch (direction) {
                case DIRECTION_RIGHT:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_DX));
                    break;
                case DIRECTION_DOWN:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_DOWN));
                    break;
                case DIRECTION_LEFT:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_SX));
                    break;
                case DIRECTION_UP:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_UP));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dell'immagine", "Errore Immagine", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Disegna l'enemy e le hitbox per il debug
    public void draw(Graphics g) {
        if (active) {
            //g.drawImage(enemyImage, hitbox.x, hitbox.y, null);

            // Disegna la hitbox dell'enemy in giallo
            g.setColor(Color.YELLOW);
            g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);

            // Disegna la hitbox della testa in rosso
            g.setColor(Color.RED);
            g.drawRect(testa.x, testa.y, testa.width, testa.height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(hitbox.x, hitbox.y, enemyImage.getWidth(null), enemyImage.getHeight(null));
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
