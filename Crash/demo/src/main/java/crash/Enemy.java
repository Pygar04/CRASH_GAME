package crash;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

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
    private Map map;
    private Random random;
    private int moveSpeed;
    private boolean onRedPixel;
    private boolean hasMovedInRed;

    

    // Percorsi delle immagini dell'enemy
    private static final String ENEMY_SX = "/Image/enemySx.png";
    private static final String ENEMY_DX = "/Image/enemyDx.png";
    private static final String ENEMY_UP = "/Image/enemyUp.png";
    private static final String ENEMY_DOWN = "/Image/enemyDown.png";

    public Enemy(CollisionManager collisionManager, Map map) {
        this.collisionManager = collisionManager;
        this.map = map;
        this.imageMap = map.getMapImage();
        updateEnemyImage();
        this.width = enemyImage.getWidth(null);
        this.height = enemyImage.getHeight(null);
        this.mapWidth = imageMap.getWidth();
        this.mapHeight = imageMap.getHeight();
        this.x = (mapWidth / 2) - 50 - width; // Centrato e spostato di 50 pixel a sinistra dal centro
        this.y = mapHeight - height - 53; // pixel sopra il bordo inferiore
        this.speed = 5; // Velocità di movimento predefinita
        this.moveSpeed = 50;
        this.hitbox = new Rectangle(x, y, width, height);
        this.testa = new Rectangle((hitbox.x + hitbox.width), (hitbox.y + hitbox.height / 2), (hitbox.width / 4), 1);
        this.onRedPixel = false;
        this.hasMovedInRed = false;
        this.random = new Random();

    }

    @Override
    public void run() {
        while (active) {
            move();
            moveRandomly();
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
                x = hitbox.x;
                // Calcola la nuova posizione della testa come un piccolo rettangolo davanti alla direzione di movimento
                testa = new Rectangle((hitbox.x + hitbox.width), (hitbox.y + hitbox.height / 2), 
                                      (hitbox.width / 4), 1);
                canMoveHead = collisionManager.canMove(testa); // Verifica se la testa può muoversi senza collisioni
                break;
        
            case DIRECTION_DOWN: 
                hitbox.y += speed;
                y = hitbox.y;
                testa = new Rectangle(hitbox.x + hitbox.width / 2, hitbox.y + hitbox.height, 
                                      1, hitbox.height / 4);
                canMoveHead = collisionManager.canMove(testa);
                break;
        
            case DIRECTION_LEFT:
                hitbox.x -= speed;
                x = hitbox.x;
                testa = new Rectangle(hitbox.x - hitbox.width / 4, hitbox.y + hitbox.height / 2, 
                                      hitbox.width / 4, 1);
                canMoveHead = collisionManager.canMove(testa);
                break;
        
            case DIRECTION_UP: 
                hitbox.y -= speed;
                y = hitbox.y;
                testa = new Rectangle(hitbox.x + hitbox.width / 2, hitbox.y - hitbox.height / 4, 
                                      1, hitbox.height / 4);
                canMoveHead = collisionManager.canMove(testa);
                break;
        }

        // Se la testa non può muoversi, cambia direzione in senso orario
        if (!canMoveHead) {
            direction = (direction + 1) % 4;
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
            g.drawImage(enemyImage, hitbox.x, hitbox.y, null);
            
            // DEBUG: Disegna la hitbox dell'enemy in giallo
            /* 
            g.setColor(Color.YELLOW);
            g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);

            // Disegna la hitbox della testa in rosso
            g.setColor(Color.RED);
            g.drawRect(testa.x, testa.y, testa.width, testa.height);
            */
        }
    }

    // Riavvia l'enemy
    public void restart() {
        this.active = false; // Interrompe il thread corrente
        try {
            Thread.sleep(100); // Dà al thread il tempo di terminare
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.direction = DIRECTION_LEFT;
        this.x = (mapWidth / 2) - 50 - width;
        this.y = mapHeight - height - 50;
        this.hitbox = new Rectangle(x, y, width, height);
        this.testa = new Rectangle((hitbox.x + hitbox.width), (hitbox.y + hitbox.height / 2), (hitbox.width / 4), 1);
        this.active = true;
    }


    // Metodo per spostare l'enemy di 50 pixel in modo casuale
    public void moveRandomly() {
        if (!active) return;
    
        boolean isRedPixel = map.isRedPixel(x, y);
    
        if (isRedPixel && !onRedPixel) {
            // Il nemico è appena entrato in un'area rossa
            onRedPixel = true;
            hasMovedInRed = false;
        } else if (!isRedPixel && onRedPixel) {
            // Il nemico è appena uscito dall'area rossa
            onRedPixel = false;
        }
    
        if (onRedPixel && !hasMovedInRed) {
            // Esegui il movimento una sola volta quando entra nell'area rossa
            if (direction == DIRECTION_LEFT || direction == DIRECTION_RIGHT) {
                // Movimento verticale (su o giù)
                if (random.nextBoolean()) {
                    moveVertical(true, false); // Sposta verso l'alto
                } else {
                    moveVertical(false, true); // Sposta verso il basso
                }
            } else if (direction == DIRECTION_UP || direction == DIRECTION_DOWN) {
                // Movimento orizzontale (sinistra o destra)
                if (random.nextBoolean()) {
                    moveHorizontal(true, false); // Sposta verso sinistra
                } else {
                    moveHorizontal(false, true); // Sposta verso destra
                }
            }
            hasMovedInRed = true; // Segna che il nemico si è mosso nell'area rossa
        }
    }
    

    public void moveVertical(boolean moveUp, boolean moveDown) {
        if (moveUp && (direction == DIRECTION_LEFT || direction == DIRECTION_RIGHT)) {
            for (int i = 0; i < moveSpeed; i++) {
                int newY = y - 1;
                Rectangle newHitbox = new Rectangle(x, newY, width, height);
                if (collisionManager.canMove(newHitbox)) {
                    y = newY;
                    hitbox.y = y;
                } else {
                    break; // Interrompe il ciclo se c'è una collisione
                }
            }
        } else if (moveDown && (direction == DIRECTION_LEFT || direction == DIRECTION_RIGHT)) {
            for (int i = 0; i < moveSpeed; i++) {
                int newY = y + 1;
                Rectangle newHitbox = new Rectangle(x, newY, width, height);
                if (collisionManager.canMove(newHitbox)) {
                    y = newY;
                    hitbox.y = y;
                } else {
                    break; // Interrompe il ciclo se c'è una collisione
                }
            }
        }
    }

    public void moveHorizontal(boolean moveLeft, boolean moveRight) {
        if (moveLeft && (direction == DIRECTION_UP || direction == DIRECTION_DOWN)) {
            for (int i = 0; i < moveSpeed; i++) {
                int newX = x - 1;
                Rectangle newHitbox = new Rectangle(newX, y, width, height);
                if (collisionManager.canMove(newHitbox)) {
                    x = newX;
                    hitbox.x = x;
                } else {
                    break; // Interrompe il ciclo se c'è una collisione
                }
            }
        } else if (moveRight && (direction == DIRECTION_UP || direction == DIRECTION_DOWN)) {
            for (int i = 0; i < moveSpeed; i++) {
                int newX = x + 1;
                Rectangle newHitbox = new Rectangle(newX, y, width, height);
                if (collisionManager.canMove(newHitbox)) {
                    x = newX;
                    hitbox.x = x;
                } else {
                    break; // Interrompe il ciclo se c'è una collisione
                }
            }
        }
    }

    public void stopGame() {
        active = false; // Imposta il flag active a false per fermare l'esecuzione del thread
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
        this.speed = 5;
    }

}
