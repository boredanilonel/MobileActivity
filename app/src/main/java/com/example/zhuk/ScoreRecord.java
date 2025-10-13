package com.example.zhuk;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(
        tableName = "scores",
        foreignKeys = @ForeignKey(
                entity = Player.class,
                parentColumns = "id",
                childColumns = "playerId",
                onDelete = ForeignKey.CASCADE
        )
)
public class ScoreRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int playerId;
    private String playerName;
    private int score;
    private int difficultyLevel;
    private long date;
    private int gameDuration; // в секундах

    public ScoreRecord() {
    }

    public ScoreRecord(int playerId, String playerName, int score, int difficultyLevel, int gameDuration) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = score;
        this.difficultyLevel = difficultyLevel;
        this.date = System.currentTimeMillis();
        this.gameDuration = gameDuration;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public int getGameDuration() { return gameDuration; }
    public void setGameDuration(int gameDuration) { this.gameDuration = gameDuration; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(date));
    }

    public String getDifficultyName() {
        String[] difficultyLevels = {"Очень легкий", "Легкий", "Ниже среднего", "Средний",
                "Выше среднего", "Сложный", "Очень сложный", "Эксперт",
                "Мастер", "Легендарный"};
        if (difficultyLevel >= 0 && difficultyLevel < difficultyLevels.length) {
            return difficultyLevels[difficultyLevel];
        }
        return "Уровень " + difficultyLevel;
    }
}