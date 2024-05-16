package crash;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {

    public static void playSound(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.err.println("Error with playing sound: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
