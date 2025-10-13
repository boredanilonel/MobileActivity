package com.example.zhuk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.List;

public class GameManager {
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_GAME_SPEED = "game_speed";
    private static final String KEY_MAX_BUGS = "max_bugs";
    private static final String KEY_BONUS_INTERVAL = "bonus_interval";
    private static final String KEY_ROUND_DURATION = "round_duration";

    private static final String KEY_CURRENT_PLAYER_ID = "current_player_id";

    private SharedPreferences prefs;
    private AppDatabase database;
    private int currentPlayerId = -1;

    private static final int DEFAULT_GAME_SPEED = 5;
    private static final int DEFAULT_MAX_BUGS = 10;
    private static final int DEFAULT_BONUS_INTERVAL = 30;
    private static final int DEFAULT_ROUND_DURATION = 60;

    public GameManager() {
    }

    public void initialize(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        database = AppDatabase.getDatabase(context);
        currentPlayerId = prefs.getInt(KEY_CURRENT_PLAYER_ID, -1);
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

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayer(int playerId) {
        currentPlayerId = playerId;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_PLAYER_ID, playerId);
        editor.apply();
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

    @SuppressLint("StaticFieldLeak")
    public void saveGameResult(int score, int gameDuration) {
        if (currentPlayerId == -1) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Player player = database.playerDao().getPlayerById(currentPlayerId);
                if (player != null) {
                    @SuppressLint("StaticFieldLeak") ScoreRecord scoreRecord = new ScoreRecord(
                            currentPlayerId,
                            player.getFullName(),
                            score,
                            getGameSpeed(),
                            gameDuration
                    );
                    database.scoreDao().insert(scoreRecord);
                }
                return null;
            }
        }.execute();
    }

    // Получение топовых результатов
    public interface ScoresCallback {
        void onScoresLoaded(List<ScoreRecord> scores);
    }

    @SuppressLint("StaticFieldLeak")
    public void getTopScores(int limit, ScoresCallback callback) {
        new AsyncTask<Void, Void, List<ScoreRecord>>() {
            @Override
            protected List<ScoreRecord> doInBackground(Void... voids) {
                return database.scoreDao().getTopScores(limit);
            }

            @Override
            protected void onPostExecute(List<ScoreRecord> scores) {
                callback.onScoresLoaded(scores);
            }
        }.execute();
    }

    // Получение всех игроков
    public interface PlayersCallback {
        void onPlayersLoaded(List<Player> players);
    }
    public interface CurrentPlayerCallback {
        void onCurrentPlayerSet(boolean success);
    }

    @SuppressLint("StaticFieldLeak")
    public void getAllPlayers(PlayersCallback callback) {
        new AsyncTask<Void, Void, List<Player>>() {
            @Override
            protected List<Player> doInBackground(Void... voids) {
                return database.playerDao().getAllPlayers();
            }

            @Override
            protected void onPostExecute(List<Player> players) {
                callback.onPlayersLoaded(players);
            }
        }.execute();
    }

    // Сохранение нового игрока
    @SuppressLint("StaticFieldLeak")
    public void savePlayer(Player player, PlayerSaveCallback callback) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                return database.playerDao().insert(player);
            }

            @Override
            protected void onPostExecute(Long playerId) {
                callback.onPlayerSaved(playerId.intValue());
            }
        }.execute();
    }

    public interface PlayerSaveCallback {
        void onPlayerSaved(int playerId);
    }
    public interface ClearCallback {
        void onClearCompleted();
    }
    public void getPlayerScores(int playerId, ScoresCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ScoreRecord> scores = database.scoreDao().getPlayerScores(playerId);
            callback.onScoresLoaded(scores);
        });
    }

    public void clearAllScores(ClearCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.scoreDao().clearAllScores();
            callback.onClearCompleted();
        });
    }
}