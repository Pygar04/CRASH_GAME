package crash;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CrashGame game = new CrashGame();
            Thread gameThread = new Thread(game);
            gameThread.start();
        });
    }
}