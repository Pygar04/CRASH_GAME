package crash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private CrashGame game;
    private Image backgroundImage;
    private ScoreFile topScore;

    private static final String MENU = "/Image/menu.png";
    private static final String START_BUTTON = "/Image/startButton.png";
    private static final String SCORES_BUTTON = "/Image/scoreButton.png";

    public Menu(CrashGame game) {
        this.game = game;
        this.topScore = new ScoreFile("topScore.data");
        setupMenu();
    }

    private void setupMenu() {
        // Set layout manager for the main panel
        setLayout(new BorderLayout());

        // Load background image
        backgroundImage = new ImageIcon(getClass().getResource(MENU)).getImage();

        // Create a new panel for the background with an image
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
                showBestScores();
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

    private void showBestScores() {
        removeAll();
        setLayout(new BorderLayout());

        // Create a new panel to display the score
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(360, 350));
        scorePanel.setBackground(Color.BLACK);
        scorePanel.setLayout(new GridBagLayout());

        // Load top score
        int topScoreValue = topScore.loadTopScore();

        // Create label to display the score
        JLabel scoreLabel = new JLabel("Top Score: " + topScoreValue);
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create Back button
        JButton backButton = new JButton("BACK");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        backButton.setForeground(Color.YELLOW);
        backButton.setBackground(Color.BLACK);
        backButton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(100, 25));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                setupMenu();
                revalidate();
                repaint();
            }
        });

        // Add components to the score panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        scorePanel.add(scoreLabel, gbc);

        gbc.gridy = 1;
        scorePanel.add(backButton, gbc);

        // Create a new panel for alignment
        JPanel alignmentPanel = new JPanel();
        alignmentPanel.setLayout(new GridBagLayout());
        alignmentPanel.setPreferredSize(new Dimension(360, 350));
        alignmentPanel.setOpaque(false);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(50, 0, 0, 0); // Adjust this value to match the button panel's vertical position
        gbc.anchor = GridBagConstraints.NORTH;
        alignmentPanel.add(scorePanel, gbc);

        // Load background image again
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Use GridBagLayout

        // Add the alignment panel to the background panel
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(50, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        backgroundPanel.add(alignmentPanel, gbc);

        // Add the background panel to the main panel
        add(backgroundPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}
