package com.example.zhuk;

import android.content.Context;
import android.content.SharedPreferences;

public class GameManager {
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_GAME_SPEED = "game_speed";
    private static final String KEY_MAX_BUGS = "max_bugs";
    private static final String KEY_BONUS_INTERVAL = "bonus_interval";
    private static final String KEY_ROUND_DURATION = "round_duration";

    private SharedPreferences prefs;

    private static final int DEFAULT_GAME_SPEED = 5;
    private static final int DEFAULT_MAX_BUGS = 10;
    private static final int DEFAULT_BONUS_INTERVAL = 30;
    private static final int DEFAULT_ROUND_DURATION = 60;

    public GameManager() {
    }

    public void initialize(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getHighScore() {
        return prefs.getInt(KEY_HIGH_SCORE, 0);
    }

    public void saveHighScore(int score) {
        if (score > getHighScore()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_HIGH_SCORE, score);
            editor.apply();
        }
    }

    public int getGameSpeed() {
        return prefs.getInt(KEY_GAME_SPEED, DEFAULT_GAME_SPEED);
    }

    public int getMaxBugs() {
        return prefs.getInt(KEY_MAX_BUGS, DEFAULT_MAX_BUGS);
    }

    public int getBonusInterval() {
        return prefs.getInt(KEY_BONUS_INTERVAL, DEFAULT_BONUS_INTERVAL);
    }

    public int getRoundDuration() {
        return prefs.getInt(KEY_ROUND_DURATION, DEFAULT_ROUND_DURATION);
    }

    public void saveGameSettings(int speed, int maxBugs, int bonusInterval, int roundDuration) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_GAME_SPEED, speed);
        editor.putInt(KEY_MAX_BUGS, maxBugs);
        editor.putInt(KEY_BONUS_INTERVAL, bonusInterval);
        editor.putInt(KEY_ROUND_DURATION, roundDuration);
        editor.apply();
    }

    public void resetHighScore() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_HIGH_SCORE);
        editor.apply();
    }
}