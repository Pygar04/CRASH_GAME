package crash;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player implements Runnable {
    // Costanti per le direzioni di movimento
    private static final int DIRECTION_RIGHT = 0;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_LEFT = 2;
    private static final int DIRECTION_DOWN = 3;
    private static final int INIT_DIRECTION = DIRECTION_RIGHT;

    // Variabili di istanza
    private int x, y, width, height, speed, score;
    private int direction;
    private BufferedImage mapImage;
    private int mapWidth, mapHeight;
    private Rectangle hitbox; // hitbox del player
    private Rectangle testa; // hitbox della testa
    private Image playerImage;
    private int lives;
    private boolean active = true;
    private CollisionManager collisionManager;

    // Percorsi delle immagini del player
    private static final String PLAYER_DX = "/playerDx.png";
    private static final String PLAYER_SX = "/playerSx.png";
    private static final String PLAYER_UP = "/playerUp.png";
    private static final String PLAYER_DOWN = "/playerDown.png";
    

    public Player(CollisionManager collisionManager, Map map) {
        this.collisionManager = collisionManager;
        this.direction = INIT_DIRECTION;
        this.lives = 3;
        updatePlayerImage();
        this.mapImage = map.getMapImage();
        this.width = playerImage.getWidth(null);
        this.height = playerImage.getHeight(null);
        this.mapWidth = mapImage.getWidth();
        this.mapHeight = mapImage.getHeight();
        this.x = (mapWidth / 2) + 50 - (width / 2); // Centrato e spostato di 50 pixel a destra dal centro
        this.y = mapHeight - height - 50; // 50 pixel sopra il bordo inferiore
        this.speed = 5;
        this.score = 0;
        this.hitbox = new Rectangle(x, y, width, height);
        this.testa = new Rectangle(x + width, y + height / 2, width, 1); // Inizializza la hitbox della testa
    }

    public void restart(){
        this.active = true;
        this.direction = INIT_DIRECTION;
        this.x = (mapWidth / 2) + 50 - (width / 2); // Centrato e spostato di 50 pixel a destra dal centro
        this.y = mapHeight - height - 50; // 50 pixel sopra il bordo inferiore
        this.hitbox = new Rectangle(x, y, width, height);
        this.testa = new Rectangle(x + width, y + height / 2, width / 2, 1); // Inizializza la hitbox della testa   
    }

    // Disegna il player e le hitbox per il debug
    public void draw(Graphics g) {
        //g.drawImage(playerImage, hitbox.x, hitbox.y, null);

        // DEBUG: Disegna le hitbox
        g.setColor(Color.YELLOW);
        g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);

        g.setColor(Color.RED);
        g.drawRect(testa.x, testa.y, testa.width, testa.height);
    }

    // Riduce le vite del player e ferma il gioco se le vite sono finite
    public void loseLife() {
        lives--;
    }

    public Rectangle getBounds() {
        return new Rectangle(hitbox);
    }

    // Metodo principale di esecuzione del thread
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

    // Gestisce il movimento del player
    public void move() {
        if (!active) return;

        Rectangle newPosition = new Rectangle(hitbox);
        boolean canMoveHead = false;  

        // Calcola la nuova posizione della testa in base alla direzione
        switch (direction) {
            case DIRECTION_RIGHT:
                newPosition.x += speed;
                testa = new Rectangle((newPosition.x + newPosition.width), (newPosition.y + newPosition.height / 2), (newPosition.width/2), 1);
                canMoveHead = collisionManager.canMove(testa);
                break;
            case DIRECTION_DOWN:
                newPosition.y += speed;
                testa = new Rectangle(newPosition.x + (newPosition.width / 2), newPosition.y + newPosition.height, 1, newPosition.height);
                canMoveHead = collisionManager.canMove(testa);
                break;
            case DIRECTION_LEFT:
                newPosition.x -= speed;
                testa = new Rectangle(newPosition.x - newPosition.width, newPosition.y + newPosition.height / 2, (newPosition.width), 1);
                canMoveHead = collisionManager.canMove(testa);
                break;
            case DIRECTION_UP:
                newPosition.y -= speed;
                testa = new Rectangle(newPosition.x + (newPosition.width / 2), newPosition.y - newPosition.height, 1, newPosition.height);
                canMoveHead = collisionManager.canMove(testa);
                break;
        }
        
        // Verifica collisione con il muro per la hitbox principale
        if (collisionManager.canMove(newPosition)) {
            hitbox = newPosition;
        }
        // Cambia direzione solo se la testa incontra un muro
        if (!canMoveHead) {
            direction = (direction + 1) % 4; // Cambia direzione in senso orario
            // Debug
            System.out.println("Changing direction Player to: " + direction);
            updatePlayerImage(); // Aggiorna l'immagine del player per riflettere la possibile nuova direzione
        }
    }

    // Aggiorna l'immagine del player in base alla direzione
    private void updatePlayerImage() {
        try {
            switch (direction) {
                case DIRECTION_RIGHT:
                    playerImage = ImageIO.read(getClass().getResourceAsStream(PLAYER_DX));
                    break;
                case DIRECTION_DOWN:
                    playerImage = ImageIO.read(getClass().getResourceAsStream(PLAYER_DOWN));
                    break;
                case DIRECTION_LEFT:
                    playerImage = ImageIO.read(getClass().getResourceAsStream(PLAYER_SX));
                    break;
                case DIRECTION_UP:
                    playerImage = ImageIO.read(getClass().getResourceAsStream(PLAYER_UP));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dell'immagine", "Errore Immagine", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo chiamato in caso di collisione con il nemico
    public void collide() {
        System.out.println("Collision detected with Enemy!");
        active = false; // Rendi il giocatore inattivo dopo una collisione
        restart();
    }

    // Gestisce il movimento orizzontale del player senza cambiare direzione
    public void moveHorizontal(boolean moveLeft, boolean moveRight) {
        if (moveLeft && (direction == DIRECTION_UP || direction == DIRECTION_DOWN)) {
            x -= speed;
            hitbox.x = x;
        } else if (moveRight && (direction == DIRECTION_UP || direction == DIRECTION_DOWN)) {
            x += speed;
            hitbox.x = x;
        }
    }

    // Gestisce il movimento verticale del player senza cambiare direzione
    public void moveVertical(boolean moveUp, boolean moveDown) {
        if (moveUp && (direction == DIRECTION_LEFT || direction == DIRECTION_RIGHT)) {
            y -= speed;
            hitbox.y = y;
        } else if (moveDown && (direction == DIRECTION_LEFT || direction == DIRECTION_RIGHT)) {
            y += speed;
            hitbox.y = y;
        }
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(){
        score++;
    }

    
}