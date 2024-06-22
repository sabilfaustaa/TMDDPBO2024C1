package models;

import connection.Database;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Score extends Database {
    private String username;
    private int points;
    private int upwardMovements;
    private int downwardMovements;

    public Score() {}

    public Score(String username, int points, int upwardMovements, int downwardMovements) {
        this.username = username;
        this.points = points;
        this.upwardMovements = upwardMovements;
        this.downwardMovements = downwardMovements;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getUpwardMovements() {
        return upwardMovements;
    }

    public void setUpwardMovements(int upwardMovements) {
        this.upwardMovements = upwardMovements;
    }

    public int getDownwardMovements() {
        return downwardMovements;
    }

    public void setDownwardMovements(int downwardMovements) {
        this.downwardMovements = downwardMovements;
    }

    public ArrayList<Score> getAllScoresFromDb() {
        ArrayList<Score> scoreList = new ArrayList<>();
        ResultSet resultSet = super.selectQuery("SELECT * FROM score");
        try {
            while (resultSet.next()) {
                Score tempScore = new Score(
                        resultSet.getString("username"),
                        resultSet.getInt("points"),
                        resultSet.getInt("upwardMovements"),
                        resultSet.getInt("downwardMovements")
                );
                scoreList.add(tempScore);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return scoreList;
    }
}
