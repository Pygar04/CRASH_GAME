package crash;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Map {
    private BufferedImage mapImage; // L'immagine della mappa
    private static final String MAP_IMAGE_PATH = "/Image/map2.png"; // Il percorso dell'immagine della mappa

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

    public int getWidthMap(){
        return mapImage.getWidth(); // Restituisce la larghezza dell'immagine della mappa
    }
    
    public int getHeighMap(){
        return mapImage.getHeight(); // Restituisce l'altezza dell'immagine della mappa
    }

    public void draw(Graphics g, Component c) {
        if (mapImage != null) {
            int width = mapImage.getWidth();
            int height = mapImage.getHeight();
            BufferedImage modifiedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    
            // Colora i pixel rossi di nero nel nuovo BufferedImage
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (isRedPixel(x, y)) {
                        modifiedImage.setRGB(x, y, Color.BLACK.getRGB()); // Sovrascrive il pixel con il colore nero
                    } else {
                        modifiedImage.setRGB(x, y, mapImage.getRGB(x, y)); // Copia il pixel originale nell'immagine modificata
                    }
                }
            }
    
            // Disegna l'immagine modificata sul componente specificato
            g.drawImage(modifiedImage, 0, 0, c.getWidth(), c.getHeight(), null);
        }
    }
    

    public boolean isRedPixel(int x, int y) {
        // Estrai il colore del pixel dalle coordinate specificate
        int rgb = mapImage.getRGB(x, y);
        int red = (rgb >> 16) & 0xFF; // Estrai il componente rosso
        int green = (rgb >> 8) & 0xFF; // Estrai il componente verde
        int blue = rgb & 0xFF; // Estrai il componente blu
        // Controlla se il pixel è rosso
        return red > green && red > blue;
    }
    
}