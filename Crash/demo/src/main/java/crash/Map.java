package crash;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Map {
    private BufferedImage mapImage; // L'immagine della mappa
    private static final String MAP_IMAGE_PATH = "/map.png"; // Il percorso dell'immagine della mappa

    public Map() {
        loadImage(); // Carica l'immagine della mappa quando viene creato un oggetto Map
    }

    private void loadImage() {
        try {
            // Legge l'immagine della mappa dal percorso specificato
            mapImage = ImageIO.read(getClass().getResourceAsStream(MAP_IMAGE_PATH));
        } catch (IOException e) {
            // Stampa l'errore se non riesce a caricare l'immagine
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dell'immagine: " + MAP_IMAGE_PATH, "Errore Immagine", JOptionPane.ERROR_MESSAGE);
        }
    }

    public BufferedImage getMapImage() {
        return mapImage; // Restituisce l'immagine della mappa
    }

    public int getWeightMap(){
        return mapImage.getWidth(); // Restituisce la larghezza dell'immagine della mappa
    }
    
    public int getHeightMap(){
        return mapImage.getHeight(); // Restituisce l'altezza dell'immagine della mappa
    }

    public void draw(Graphics g, Component c) {
        if (mapImage != null) {
            // Disegna l'immagine della mappa sul componente specificato
            g.drawImage(mapImage, 0, 0, c.getWidth(), c.getHeight(), null);
        }
    }
}