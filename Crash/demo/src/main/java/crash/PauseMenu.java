package crash;

import javax.swing.*;
import java.awt.*;

public class PauseMenu extends JPanel {
    public PauseMenu(GameBoard gameBoard) {
        setLayout(new GridLayout(3, 1));
        setBackground(new Color(0, 0, 0, 123)); // Semi-transparent background
        JButton resumeButton = new JButton("Resume");
        JButton optionsButton = new JButton("Options");
        JButton quitButton = new JButton("Quit Game");
    }
}
