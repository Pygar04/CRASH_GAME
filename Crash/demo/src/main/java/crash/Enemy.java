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
    private static final int INIT_DIRECTION = DIRECTION_LEFT;

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
        this.direction = INIT_DIRECTION; // iniziamo con il nemico che si muove verso destra
        this.imageMap = map.getMapImage();
        updateEnemyImage();
        this.width = enemyImage.getWidth(null);
        this.height = enemyImage.getHeight(null);
        this.mapWidth = imageMap.getWidth();
        this.mapHeight = imageMap.getHeight();
        this.x = (mapWidth / 2) - 50 - width; // Centrato e spostato di 50 pixel a sinistra dal centro
        this.y = mapHeight - height - 50; // pixel sopra il bordo inferiore
        this.speed = 3; // Velocità di movimento predefinita
        this.hitbox = new Rectangle(x, y, width, height);
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

        boolean canMoveHead = true;

        // Calcola la nuova posizione della testa
        switch (direction) {
            case DIRECTION_RIGHT: // Se la direzione è destra
                hitbox.x += speed;  // Sposta la nuova posizione verso destra in base alla velocità
                // Calcola la nuova posizione della testa come un piccolo rettangolo davanti alla direzione di movimento
                testa = new Rectangle(hitbox.x + hitbox.width, hitbox.y + hitbox.height / 2, 
                                      hitbox.width / 4, 1);
                canMoveHead = collisionManager.canMove(testa); // Verifica se la testa può muoversi senza collisioni
                break;
        
            case DIRECTION_DOWN: // Se la direzione è giù
                hitbox.y += speed; // Sposta la nuova posizione verso il basso in base alla velocità
                // Calcola la nuova posizione della testa come un piccolo rettangolo davanti alla direzione di movimento
                testa = new Rectangle(hitbox.x + hitbox.width / 2, hitbox.y + hitbox.height, 
                                      1, hitbox.height / 4);
                canMoveHead = collisionManager.canMove(testa); // Verifica se la testa può muoversi senza collisioni
                break;
        
            case DIRECTION_LEFT: // Se la direzione è sinistra
                hitbox.x -= speed; // Sposta la nuova posizione verso sinistra in base alla velocità
                // Calcola la nuova posizione della testa come un piccolo rettangolo davanti alla direzione di movimento
                testa = new Rectangle(hitbox.x - hitbox.width / 4, hitbox.y + hitbox.height / 2, 
                                      hitbox.width / 4, 1);
                canMoveHead = collisionManager.canMove(testa); // Verifica se la testa può muoversi senza collisioni
                break;
        
            case DIRECTION_UP: // Se la direzione è su
                hitbox.y -= speed; // Sposta la nuova posizione verso l'alto in base alla velocità
                // Calcola la nuova posizione della testa come un piccolo rettangolo davanti alla direzione di movimento
                testa = new Rectangle(hitbox.x + hitbox.width / 2, hitbox.y - hitbox.height / 4, 
                                      1, hitbox.height / 4);
                canMoveHead = collisionManager.canMove(testa); // Verifica se la testa può muoversi senza collisioni
                break;
        }

        // Se la testa non può muoversi, cambia direzione in senso orario
        if (!canMoveHead) {
            direction = (direction + 1) % 4;
            // Debug
            System.out.println("Nuova direzione Enemy: " + direction);
            updateEnemyImage(); // Aggiorna l'immagine del nemico per riflettere la possibile nuova direzione
        } 
    }

    // Aggiorna l'immagine del nemico in base alla direzione
    private void updateEnemyImage() {
        try {
            switch (direction) {
                case DIRECTION_RIGHT:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_DX));
                    hitbox = new Rectangle(x, y, width, height);
                    break;
                case DIRECTION_DOWN:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_DOWN));
                    hitbox = new Rectangle(x, y, height, width);
                    break;
                case DIRECTION_LEFT:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_SX));
                    hitbox = new Rectangle(x, y, width, height);
                    break;
                case DIRECTION_UP:
                    enemyImage = ImageIO.read(getClass().getResourceAsStream(ENEMY_UP));
                    hitbox = new Rectangle(x, y, height, width);
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
            //Debug
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

    public boolean getActive(){
        return active;
    }

    public void initSpeed(){
        this.speed = 3;
    }


    public void restart() {
        this.active = false; // Interrompe il thread corrente
        try {
            Thread.sleep(100); // Dà al thread il tempo di terminare
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.active = true;
        this.direction = INIT_DIRECTION;
        this.x = (mapWidth / 2) - 50 - width;
        this.y = mapHeight - height - 50;
        this.hitbox = new Rectangle(x, y, width, height);
    }
}
