package crash;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardManager extends KeyAdapter {
    private Player player;

    public KeyboardManager(Player player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                player.moveHorizontal(true, false);
                break;
            case KeyEvent.VK_RIGHT:
                player.moveHorizontal(false, true);
                break;
            case KeyEvent.VK_UP:
                player.moveVertical(true, false);
                break;
            case KeyEvent.VK_DOWN:
                player.moveVertical(false, true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                player.moveHorizontal(false, false);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                player.moveVertical(false, false);
                break;
        }
    }
}
