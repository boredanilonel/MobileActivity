package com.example.zhuk;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends ViewModel {
    private int score = 0;
    private int misses = 0;
    private long gameStartTime = 0;
    private long lastBugSpawnTime = 0;
    private long lastBonusSpawnTime = 0;
    private boolean gameOver = false;
    private boolean gameRunning = false;
    private boolean gyroscopeActive = false;
    private boolean globalFreeze = false;
    private boolean globalSpeedBoost = false;
    private long freezeEndTime = 0;
    private long speedBoostEndTime = 0;
    private long gyroscopeEndTime = 0;
    public GameViewModel() {
        resetGame();
    }
    public void resetGame() {
        score = 0;
        misses = 0;
        gameStartTime = System.currentTimeMillis();
        lastBugSpawnTime = System.currentTimeMillis();
        lastBonusSpawnTime = System.currentTimeMillis();
        gameOver = false;
        gameRunning = true;

        gyroscopeActive = false;
        globalFreeze = false;
        globalSpeedBoost = false;
        freezeEndTime = 0;
        speedBoostEndTime = 0;
        gyroscopeEndTime = 0;
    }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getMisses() { return misses; }
    public void setMisses(int misses) { this.misses = misses; }

    public long getGameStartTime() { return gameStartTime; }
    public void setGameStartTime(long gameStartTime) { this.gameStartTime = gameStartTime; }

    public long getLastBugSpawnTime() { return lastBugSpawnTime; }
    public void setLastBugSpawnTime(long lastBugSpawnTime) { this.lastBugSpawnTime = lastBugSpawnTime; }

    public long getLastBonusSpawnTime() { return lastBonusSpawnTime; }
    public void setLastBonusSpawnTime(long lastBonusSpawnTime) { this.lastBonusSpawnTime = lastBonusSpawnTime; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public boolean isGameRunning() { return gameRunning; }
    public void setGameRunning(boolean gameRunning) { this.gameRunning = gameRunning; }

    public boolean isGyroscopeActive() { return gyroscopeActive; }
    public void setGyroscopeActive(boolean gyroscopeActive) { this.gyroscopeActive = gyroscopeActive; }

    public boolean isGlobalFreeze() { return globalFreeze; }
    public void setGlobalFreeze(boolean globalFreeze) { this.globalFreeze = globalFreeze; }

    public boolean isGlobalSpeedBoost() { return globalSpeedBoost; }
    public void setGlobalSpeedBoost(boolean globalSpeedBoost) { this.globalSpeedBoost = globalSpeedBoost; }

    public long getFreezeEndTime() { return freezeEndTime; }
    public void setFreezeEndTime(long freezeEndTime) { this.freezeEndTime = freezeEndTime; }

    public long getSpeedBoostEndTime() { return speedBoostEndTime; }
    public void setSpeedBoostEndTime(long speedBoostEndTime) { this.speedBoostEndTime = speedBoostEndTime; }

    public long getGyroscopeEndTime() { return gyroscopeEndTime; }
    public void setGyroscopeEndTime(long gyroscopeEndTime) { this.gyroscopeEndTime = gyroscopeEndTime; }
}