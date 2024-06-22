package viewmodels;

import connection.Database;
import models.Score;
import views.GamePlay;
import views.MainMenu;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sound.sampled.*;
import java.io.InputStream;

public class MenuViewModel {
    private MainMenu mainMenu;
    private Database db;

    private int selectedIndex = -1;
    private Clip clip;
    private Score scoreModel;
    private ArrayList<Score> scoreData;
    private String username;

    public MenuViewModel(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.mainMenu.setSize(700, 500);
        this.mainMenu.setLocationRelativeTo(null);
        this.mainMenu.setContentPane(this.mainMenu.getMainPanel());

        this.mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        db = new Database();
        scoreModel = new Score();

        this.mainMenu.getTableScore().setModel(setTable());
        playMusic("music/soundtrack.wav");

        this.mainMenu.getTableScore().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedIndex = MenuViewModel.this.mainMenu.getTableScore().getSelectedRow();
                Score selectedItem = scoreData.get(selectedIndex);
                username = selectedItem.getUsername();
                mainMenu.getInUsername().setText(username); // Set the selected username to the input field
            }
        });

        this.mainMenu.getBtnStart().addActionListener(e -> {
            if (mainMenu.getInUsername().getText().isEmpty() && selectedIndex == -1) {
                JOptionPane.showMessageDialog(mainMenu.getMainPanel(), "Please enter or select a username first!");
                return;
            } else if (!mainMenu.getInUsername().getText().isEmpty()) {
                username = mainMenu.getInUsername().getText();
                insertOrUpdateData(username);
            }
            mainMenu.dispose();
            pauseMusic("stop");
            startGame(username);
        });

        this.mainMenu.getBtnExit().addActionListener(e -> System.exit(0));
    }

    public final DefaultTableModel setTable() {
        Object[] columnHeader = {"Username", "Score", "Up", "Down"};
        DefaultTableModel temp = new DefaultTableModel(null, columnHeader);
        scoreData = scoreModel.getAllScoresFromDb();
        for (Score item : scoreData) {
            Object[] row = new Object[4];
            row[0] = item.getUsername();
            row[1] = item.getPoints();
            row[2] = item.getUpwardMovements();
            row[3] = item.getDownwardMovements();

            temp.addRow(row);
        }
        return temp;
    }

    public void insertOrUpdateData(String username) {
        boolean userExists = false;
        for (Score score : scoreData) {
            if (score.getUsername().equals(username)) {
                userExists = true;
                break;
            }
        }

        if (!userExists) {
            String sql = "INSERT INTO score (username, points, upwardMovements, downwardMovements) VALUES ('" + username + "', 0, 0, 0)";
            db.insertUpdateDeleteQuery(sql);
            scoreData = scoreModel.getAllScoresFromDb(); // Refresh score data
            this.mainMenu.getTableScore().setModel(setTable());
            this.mainMenu.getInUsername().setText("");
            this.mainMenu.getMainPanel();
        }
    }

    public void playMusic(String filepath) {

        try {
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(filepath);
            File file = new File(filepath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(audioSrc);

            // AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error load music: " + e.getMessage());
        }
    }

    public void pauseMusic(String mode) {
        if (clip != null && clip.isRunning() && mode.equals("stop")) {
            clip.stop();
        } else if (clip != null && mode.equals("play")) {
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void startGame(String username) {
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
                mainMenu.setVisible(true);
                playMusic("music/soundtrack.wav");
            }
        });
    }

    // Getters and setters
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public Database getDb() {
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public Clip getClip() {
        return clip;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public Score getScoreModel() {
        return scoreModel;
    }

    public void setScoreModel(Score scoreModel) {
        this.scoreModel = scoreModel;
    }

    public ArrayList<Score> getScoreData() {
        return scoreData;
    }

    public void setScoreData(ArrayList<Score> scoreData) {
        this.scoreData = scoreData;
    }
}
