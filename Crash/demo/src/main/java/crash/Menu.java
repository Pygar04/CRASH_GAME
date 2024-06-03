package crash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private CrashGame game;
    private Image backgroundImage;
    private ScoreFile topScore;
    private JFrame TextArea;

    private static final String MENU = "/Image/menu.png";
    private static final String START_BUTTON = "/Image/startButton.png";
    private static final String SCORES_BUTTON = "/Image/scoreButton.png";

    public Menu(CrashGame game) {
        this.game = game;
        setupMenu();
    }

    private void setupMenu() {
        // Set layout manager for the main panel
        setLayout(new BorderLayout());

        // Load background image
        backgroundImage = new ImageIcon(getClass().getResource(MENU)).getImage();

        // Create a new panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Use GridBagLayout

        // Create a new panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(360, 350));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new GridBagLayout());

        // Create Start Game button with image
        JButton startButton = new JButton(new ImageIcon(getClass().getResource(START_BUTTON)));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.startGame();
            }
        });

        // Create View Best Scores button with image
        JButton bestScoresButton = new JButton(new ImageIcon(getClass().getResource(SCORES_BUTTON)));
        bestScoresButton.setPreferredSize(new Dimension(200, 50));
        bestScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.showBestScores();
            }
        });

        // Add buttons to the button panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        buttonPanel.add(startButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(bestScoresButton, gbc);

        // Add the button panel to the background panel with vertical offset
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(50, 0, 0, 0); // Adjust this value to move the panel vertically
        gbc.anchor = GridBagConstraints.NORTH; // Ensure panel is anchored to the top
        backgroundPanel.add(buttonPanel, gbc);

        // Add the background panel to the main panel
        add(backgroundPanel, BorderLayout.CENTER);
    }
}
