package viewmodels;

import models.Character;
import models.Obstacle;
import views.GamePlay;
import connection.Database;
import views.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

public class GameViewModel extends AbstractViewModel implements ActionListener {
    private GamePlay gamePlay;
    private Timer gameLoop;
    private Character player;
    private Image playerImg;
    private int playerStartPosX;
    private int playerStartPosY;
    private int playerWidth = 50;
    private int playerHeight = 80;
    private double gravity = 0.5;
    private int score = 0;
    private int upCount = 0; // Counter untuk rintangan atas
    private int downCount = 0; // Counter untuk rintangan bawah
    private int upScore = 0; // Counter untuk rintangan atas
    private int downScore = 0;

    private ArrayList<Obstacle> lowerObstacles;
    private ArrayList<Obstacle> upperObstacles;
    private Timer lowerObstacleTimer;
    private Timer upperObstacleTimer;
    private boolean isGameOver = false;
    private Database db;
    private String username;
    private JFrame gameFrame;
    private boolean gameStarted = false;

    private MusicPlayer musicPlayer;
    private boolean onInitialObstacle = true;

    public GameViewModel(GamePlay gamePlay, int frameWidth, int frameHeight, String username, JFrame gameFrame) {
        this.gamePlay = gamePlay;
        this.playerStartPosX = frameWidth / 8;
        this.playerStartPosY = frameHeight / 2;
        this.playerImg = new ImageIcon(getClass().getResource("../assets/character.png")).getImage();
        this.player = new Character(playerImg, playerStartPosX, playerStartPosY, playerWidth, playerHeight);
        this.username = username;
        this.gameFrame = gameFrame;

        lowerObstacles = new ArrayList<>();
        upperObstacles = new ArrayList<>();

        db = new Database();

        // Initialize the initial obstacles for the player to stand on
        generateInitialObstacles();

        initializeTimers();

        musicPlayer = new MusicPlayer();
        musicPlayer.playBackgroundMusic("music/battle.wav"); // Play battle music

        gameLoop = new Timer(1000 / 70, this);
        gameLoop.start();
    }

    private void initializeTimers() {
        lowerObstacleTimer = new Timer(2500, e -> generateLowerObstacle());
        upperObstacleTimer = new Timer(4500, e -> generateUpperObstacle());
    }

    @Override
    public void initialize() {
        super.initialize();
        // Additional initialization logic if needed
    }

    @Override
    public void update() {
        if (gameStarted) {
            player.update();
            updateObstacles(lowerObstacles);
            updateObstacles(upperObstacles);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        gamePlay.repaint();
        if (isGameOver) {
            gameLoop.stop();
            lowerObstacleTimer.stop();
            upperObstacleTimer.stop();
            musicPlayer.stopBackgroundMusic(); // Stop battle music
            saveScoreToDatabase();
            showGameOverDialog();
        }
    }

    public void draw(Graphics g) {
        g.drawImage(gamePlay.getBackgroundImage(), 0, 0, gamePlay.getWindowWidth(), gamePlay.getWindowHeight(), null);
        g.drawImage(player.getImg(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);
        for (Obstacle obstacle : lowerObstacles) {
            g.drawImage(obstacle.getImg(), obstacle.getPosX(), obstacle.getPosY(), obstacle.getWidth(), obstacle.getHeight(), null);
        }
        for (Obstacle obstacle : upperObstacles) {
            g.drawImage(obstacle.getImg(), obstacle.getPosX(), obstacle.getPosY(), obstacle.getWidth(), obstacle.getHeight(), null);
        }
    }

    public void move() {
        if (!gameStarted && player.getVelocityY() < 0) {
            gameStarted = true;
            onInitialObstacle = false;
            lowerObstacleTimer.start();
            upperObstacleTimer.start();
            // Set the initial obstacles to start moving
            for (Obstacle obstacle : lowerObstacles) {
                obstacle.setVelocityX(2);
            }
            for (Obstacle obstacle : upperObstacles) {
                obstacle.setVelocityX(2);
            }
        }

        player.setVelocityY(player.getVelocityY() + gravity);

        double movementPosY = player.getPosY() + player.getVelocityY();
        if (movementPosY > 0) {
            player.setPosY((int) movementPosY);
        }
        if ((player.getPosY() + player.getHeight()) > gamePlay.getWindowHeight()) {
            isGameOver = true;
        }

        int movementPosX = (int) (player.getPosX() + player.getVelocityX());
        if (movementPosX > 0 && (movementPosX + player.getWidth()) < gamePlay.getWindowWidth()) {
            player.setPosX(movementPosX);
        }

        for (Obstacle obstacle : lowerObstacles) {
            obstacle.setPosX(obstacle.getPosX() - obstacle.getVelocityX());
            handleBottomWallCollision(player, obstacle);
        }
        for (Obstacle obstacle : upperObstacles) {
            obstacle.setPosX(obstacle.getPosX() - obstacle.getVelocityX());
            handleTopWallCollision(player, obstacle);
        }

        if (player.getPosX() < 0) {
            player.setPosX(0);
        } else if (player.getPosX() + player.getWidth() > gamePlay.getWindowWidth()) {
            player.setPosX(gamePlay.getWindowWidth() - player.getWidth());
        }
        if (player.getPosY() < 0) {
            player.setPosY(0);
        } else if (player.getPosY() + player.getHeight() > gamePlay.getWindowHeight()) {
            player.setPosY(gamePlay.getWindowHeight() - player.getHeight());
        }
    }

    public void handleBottomWallCollision(Character player, Obstacle obstacle) {
        int playerRight = player.getPosX() + player.getWidth();
        int playerBottom = player.getPosY() + player.getHeight();
        int obstacleRight = obstacle.getPosX() + obstacle.getWidth();
        int obstacleTop = obstacle.getPosY();

        boolean collisionDetected = playerRight > obstacle.getPosX() &&
                player.getPosX() < obstacleRight &&
                playerBottom > obstacleTop &&
                player.getPosY() < obstacleTop + obstacle.getHeight();

        if (collisionDetected) {
            int overlapBottom = playerBottom - obstacleTop;
            int overlapTop = (obstacleTop + obstacle.getHeight()) - player.getPosY();
            int overlapRight = playerRight - obstacle.getPosX();
            int overlapLeft = obstacleRight - player.getPosX();

            if (overlapBottom < overlapTop && overlapBottom < overlapLeft && overlapBottom < overlapRight) {
                player.setVelocityY(0);
                player.setPosY(obstacleTop - player.getHeight());
                if (!obstacle.isScored() && !onInitialObstacle) {
                    downScore += calculateScore(obstacle.getHeight());
                    score = upScore + downScore;
                    downCount++;
                    obstacle.setScored(true);
                    System.out.println("Bottom obstacle score updated: " + downScore);  // Debug print
                }
                player.setPosX(player.getPosX() - obstacle.getVelocityX());
            } else if (overlapTop < overlapBottom && overlapTop < overlapLeft && overlapTop < overlapRight) {
                player.setVelocityY(0);
                player.setPosY(obstacleTop + obstacle.getHeight());
            } else if (overlapLeft < overlapRight && overlapLeft < overlapTop && overlapLeft < overlapBottom) {
                player.setVelocityX(0);
                player.setPosX(obstacleRight);
            } else if (overlapRight < overlapLeft && overlapRight < overlapTop && overlapRight < overlapBottom) {
                player.setVelocityX(0);
                player.setPosX(obstacle.getPosX() - player.getWidth());
            }

            player.setPosX(player.getPosX() - obstacle.getVelocityX());
            if (player.getPosX() <= 0) {
                isGameOver = true;
            }
        }
    }

    public void handleTopWallCollision(Character player, Obstacle obstacle) {
        int playerRight = player.getPosX() + player.getWidth();
        int playerBottom = player.getPosY() + player.getHeight();
        int obstacleRight = obstacle.getPosX() + obstacle.getWidth();
        int obstacleBottom = obstacle.getPosY() + obstacle.getHeight();

        int groundHeight = 60; // Tinggi tanah pada obstacle atas

        boolean collisionDetected = playerRight > obstacle.getPosX() &&
                player.getPosX() < obstacleRight &&
                playerBottom > obstacle.getPosY() + (obstacle.getHeight() - groundHeight) &&
                player.getPosY() < obstacleBottom;

        if (collisionDetected) {
            int overlapRight = playerRight - obstacle.getPosX();
            int overlapLeft = obstacleRight - player.getPosX();
            int overlapBottom = playerBottom - (obstacle.getPosY() + (obstacle.getHeight() - groundHeight));
            int overlapTop = (obstacleBottom - groundHeight) - player.getPosY();

            if (overlapBottom < overlapTop && overlapBottom < overlapLeft && overlapBottom < overlapRight) {
                player.setVelocityY(0);
                player.setPosY(obstacle.getPosY() + (obstacle.getHeight() - groundHeight) - player.getHeight());
                if (!obstacle.isScored() && !onInitialObstacle) {
                    upScore += calculateScore(obstacle.getHeight());
                    score = upScore + downScore;
                    upCount++;
                    obstacle.setScored(true);
                    System.out.println("Top obstacle score updated: " + upScore);  // Debug print
                }
            } else if (overlapTop < overlapBottom && overlapTop < overlapLeft && overlapTop < overlapRight) {
                player.setVelocityY(0);
                player.setPosY(obstacleBottom);
            } else if (overlapLeft < overlapRight && overlapLeft < overlapTop && overlapLeft < overlapBottom) {
                player.setVelocityX(0);
                player.setPosX(obstacleRight);
            } else if (overlapRight < overlapLeft && overlapRight < overlapTop && overlapRight < overlapBottom) {
                player.setVelocityX(0);
                player.setPosX(obstacle.getPosX() - player.getWidth());
            }

            player.setPosX(player.getPosX() - obstacle.getVelocityX());
            if (player.getPosX() <= 0) {
                isGameOver = true;
            }
        }
    }

    private int calculateScore(int obstacleHeight) {
        int maxHeight = 350;
        int minHeight = 100;
        int maxScore = 100;
        int minScore = 10;
        int calculatedScore = maxScore - ((obstacleHeight - minHeight) * (maxScore - minScore) / (maxHeight - minHeight));
        return calculatedScore > 0 ? calculatedScore : 1; // Ensure at least 1 point
    }

    private void saveScoreToDatabase() {
        if (username == null || username.isEmpty()) {
            return;
        }

        System.out.println("test " + username);
        String checkUserSql = "SELECT * FROM score WHERE username = '" + username + "'";
        ResultSet resultSet = db.selectQuery(checkUserSql);
        try {
            if (resultSet.next()) {
                // Username exists, update the score
                int existingScore = resultSet.getInt("points");
                int existingUp = resultSet.getInt("upwardMovements");
                int existingDown = resultSet.getInt("downwardMovements");

                int newScore = existingScore + score;
                int newUp = existingUp + upCount;
                int newDown = existingDown + downCount;

                String updateScoreSql = "UPDATE score SET points = " + newScore + ", upwardMovements = " + newUp +
                        ", downwardMovements = " + newDown + " WHERE username = '" + username + "'";
                db.insertUpdateDeleteQuery(updateScoreSql);
            } else {
                // Username does not exist, insert new record
                String insertScoreSql = "INSERT INTO score (username, points, upwardMovements, downwardMovements) VALUES ('" +
                        username + "', " + score + ", " + upCount + ", " + downCount + ")";
                db.insertUpdateDeleteQuery(insertScoreSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGameOverDialog() {
        musicPlayer.playBackgroundMusic("music/gameover.wav"); // Play game over music

        UIManager.put("OptionPane.background", new Color(60, 179, 113)); // Warna hijau
        UIManager.put("Panel.background", new Color(60, 179, 113)); // Warna hijau
        UIManager.put("OptionPane.messageForeground", new Color(255, 215, 0)); // Warna kuning
        UIManager.put("Button.background", new Color(255, 215, 0)); // Warna kuning
        UIManager.put("Button.foreground", new Color(60, 179, 113)); // Warna hijau

        int option = JOptionPane.showOptionDialog(
                gameFrame,
                "Game Over\nYour Score: " + score + "\nWould you like to retry?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Retry", "Main Menu"},
                "Retry"
        );

        musicPlayer.stopBackgroundMusic();

        if (option == JOptionPane.YES_OPTION) {
            // Retry the game
            gameFrame.dispose();
            startGame();
        } else {
            returnToHomePage();
        }
    }

    private void startGame() {
        JFrame gameFrame = new JFrame("TMD DPBO 2024");
        gameFrame.setSize(1000, 700);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setResizable(false);

        GamePlay gamePanel = new GamePlay(username, gameFrame);
        gameFrame.add(gamePanel);
        gameFrame.pack();
        gamePanel.requestFocus();

        gameFrame.setVisible(true);
        gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                returnToHomePage();
            }
        });
    }

    private void returnToHomePage() {
        gameFrame.dispose();  // Close the game frame

        // Create a new instance of the main menu
        MainMenu mainMenu = new MainMenu();
        mainMenu.setVisible(true);

        // Create a new instance of the MenuViewModel to handle the updated main menu
        new MenuViewModel(mainMenu);
    }

    public int getScore() {
        return score;
    }

    public int getUpCount() {
        return upCount;
    }

    public int getDownCount() {
        return downCount;
    }

    public int getUpScore() {
        return upScore;
    }

    public int getDownScore() {
        return downScore;
    }

    public Character getPlayer() {
        return player;
    }

    public void generateLowerObstacle() {
        int minHeight = 100;
        int maxHeight = 350;
        int randomHeight = (int) (minHeight + Math.random() * (maxHeight - minHeight));
        Image obstacleImg = new ImageIcon(getClass().getResource("../assets/lowerObstacle.png")).getImage();
        Obstacle lowerObstacle = new Obstacle(gamePlay.getWindowWidth(), gamePlay.getWindowHeight() - randomHeight, this.playerWidth, randomHeight, obstacleImg);
        lowerObstacle.setVelocityX(2); // Set the speed of obstacle moving from right to left
        lowerObstacles.add(lowerObstacle);
    }

    public void generateUpperObstacle() {
        int minHeight = 200;
        int maxHeight = 350;
        int randomHeight = (int) (minHeight + Math.random() * (maxHeight - minHeight));
        Image obstacleImg = new ImageIcon(getClass().getResource("../assets/upperObstacle.png")).getImage();
        Obstacle upperObstacle = new Obstacle(gamePlay.getWindowWidth(), 0, this.playerWidth + 70, randomHeight, obstacleImg);
        upperObstacle.setVelocityX(2); // Set the speed of obstacle moving from right to left
        upperObstacles.add(upperObstacle);
    }

    private void updateObstacles(ArrayList<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
        }
        obstacles.removeIf(obstacle -> obstacle.getPosX() + obstacle.getWidth() < 0);
    }

    public ArrayList<Obstacle> getLowerObstacles() {
        return lowerObstacles;
    }

    public ArrayList<Obstacle> getUpperObstacles() {
        return upperObstacles;
    }

    public Timer getLowerObstacleTimer() {
        return lowerObstacleTimer;
    }

    public Timer getUpperObstacleTimer() {
        return upperObstacleTimer;
    }

    private void generateInitialObstacles() {
        int minHeight = 200;
        int maxHeight = 400;
        int spacing = 300;

        for (int i = 0; i < 4; i++) {
            int lowerHeight = (int) (minHeight + Math.random() * (maxHeight - minHeight));
            int upperHeight = (int) (minHeight + Math.random() * (maxHeight - minHeight));

            int xPos = playerStartPosX + (i * spacing);

            Image lowerObstacleImg = new ImageIcon(getClass().getResource("../assets/lowerObstacle.png")).getImage();
            Obstacle lowerObstacle = new Obstacle(xPos, gamePlay.getWindowHeight() - lowerHeight, this.playerWidth, lowerHeight, lowerObstacleImg);
            lowerObstacle.setVelocityX(0); // Initial obstacles do not move

            Image upperObstacleImg = new ImageIcon(getClass().getResource("../assets/upperObstacle.png")).getImage();
            Obstacle upperObstacle = new Obstacle(xPos, 0, this.playerWidth + 70, upperHeight, upperObstacleImg);
            upperObstacle.setVelocityX(0); // Initial obstacles do not move

            if (i % 2 == 0) {
                lowerObstacles.add(lowerObstacle);
            } else {
                upperObstacles.add(upperObstacle);
            }
        }
        player.setPosY(gamePlay.getWindowHeight() - lowerObstacles.get(0).getHeight() - playerHeight);
    }
}
