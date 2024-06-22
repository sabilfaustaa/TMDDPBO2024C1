package views;

import viewmodels.GameViewModel;
import models.Movement;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GamePlay extends JPanel {
    private final GameViewModel gameViewModel;
    private final Image backgroundImg;
    private final int frameWidth = 1000, frameHeight = 700;
    private final JLabel lblTotalScore;
    private final JLabel lblTotalUp;
    private final JLabel lblTotalDown;
    private final JPanel scorePanel;
    private final String username;

    public GamePlay(String username, JFrame gameFrame) {
        this.username = username;
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        setLayout(null); // Set layout to null for absolute positioning

        setBackground(Color.CYAN);
        backgroundImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("../assets/background.jpg"))).getImage();

        gameViewModel = new GameViewModel(this, frameWidth, frameHeight, username, gameFrame);
        Movement movement = new Movement(this);

        lblTotalScore = new JLabel("Score: " + gameViewModel.getScore());
        lblTotalUp = new JLabel("Up: " + gameViewModel.getUpCount());
        lblTotalDown = new JLabel("Down: " + gameViewModel.getDownCount());

        lblTotalScore.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalUp.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalDown.setFont(new Font("Arial", Font.BOLD, 16));

        lblTotalScore.setForeground(Color.WHITE);
        lblTotalUp.setForeground(Color.WHITE);
        lblTotalDown.setForeground(Color.WHITE);

        scorePanel = new JPanel();
        scorePanel.setBackground(new Color(0, 128, 0, 150)); // Green background with transparency
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.add(lblTotalScore);
        scorePanel.add(lblTotalUp);
        scorePanel.add(lblTotalDown);
        scorePanel.setBounds(10, 10, 200, 100); // Positioning the panel at top-left

        add(scorePanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, frameWidth, frameHeight, null);
        gameViewModel.draw(g);
        updateScore();
    }

    private void updateScore() {
        lblTotalScore.setText("Score: " + gameViewModel.getScore());
        lblTotalUp.setText("Up: " + gameViewModel.getUpCount());
        lblTotalDown.setText("Down: " + gameViewModel.getDownCount());
    }

    public Image getBackgroundImage() {
        return backgroundImg;
    }

    public int getWindowWidth() {
        return frameWidth;
    }

    public int getWindowHeight() {
        return frameHeight;
    }

    public GameViewModel getGameViewModel() {
        return gameViewModel;
    }

    public String getUsername() {
        return username;
    }
}
