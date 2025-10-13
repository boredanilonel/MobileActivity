package com.example.zhuk;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDao {

    @Insert
    void insert(ScoreRecord score);

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT :limit")
    List<ScoreRecord> getTopScores(int limit);

    @Query("SELECT * FROM scores WHERE playerId = :playerId ORDER BY score DESC")
    List<ScoreRecord> getPlayerScores(int playerId);

    @Query("SELECT * FROM scores ORDER BY score DESC")
    List<ScoreRecord> getAllScores();

    @Query("SELECT * FROM scores WHERE difficultyLevel = :difficulty ORDER BY score DESC LIMIT :limit")
    List<ScoreRecord> getTopScoresByDifficulty(int difficulty, int limit);

    @Query("DELETE FROM scores WHERE id = :id")
    void deleteScore(int id);

    @Query("SELECT COUNT(*) FROM scores")
    int getScoreCount();

    @Query("DELETE FROM scores")
    void clearAllScores();
}