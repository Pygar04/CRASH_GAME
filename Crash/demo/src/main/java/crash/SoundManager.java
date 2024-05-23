package crash;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundManager {
    private Clip clip;
    private float volume;
    private FloatControl volumeControl;

    public SoundManager(String path) {
        this.volume = -20;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(path));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume); // imposta il volume
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento del suono: " + e.getMessage());
        }
    }

    public void play() {
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0); // riparte dall'inizio
        clip.start();
    }

    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
        }
    }
}