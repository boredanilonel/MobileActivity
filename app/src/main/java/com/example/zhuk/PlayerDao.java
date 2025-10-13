package com.example.zhuk;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlayerDao {

    @Insert
    long insert(Player player);

    @Update
    void update(Player player);

    @Query("SELECT * FROM players ORDER BY fullName")
    List<Player> getAllPlayers();

    @Query("SELECT * FROM players WHERE id = :id")
    Player getPlayerById(int id);

    @Query("SELECT * FROM players WHERE fullName = :name")
    Player getPlayerByName(String name);

    @Query("DELETE FROM players WHERE id = :id")
    void deletePlayer(int id);
}