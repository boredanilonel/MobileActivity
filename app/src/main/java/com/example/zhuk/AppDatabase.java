package com.example.zhuk;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

<<<<<<< HEAD
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

=======
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9
@Database(entities = {Player.class, ScoreRecord.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlayerDao playerDao();
    public abstract ScoreDao scoreDao();

    private static volatile AppDatabase INSTANCE;

<<<<<<< HEAD
    // Делаем Executor публичным и статическим
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

=======
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "game_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}