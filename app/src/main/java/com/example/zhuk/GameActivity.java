package com.example.zhuk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private GameManager gameManager;
    private Handler gameHandler;
    private Runnable gameRunnable;
    private static final long UPDATE_INTERVAL = 16;

    private boolean gameRunning = true;
    private boolean showingResults = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        gameManager = new GameManager();
        gameManager.initialize(this);

        startGameLoop();
    }

    private void startGameLoop() {
        gameHandler = new Handler();
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if (gameRunning) {
                    gameView.update();
                    checkGameOver();
                    gameHandler.postDelayed(this, UPDATE_INTERVAL);
                }
            }
        };
        gameHandler.postDelayed(gameRunnable, UPDATE_INTERVAL);
    }

    private void checkGameOver() {
        if (showingResults) return;

        if (gameView.isGameOver()) {
            gameRunning = false;
            showingResults = true;

            if (gameHandler != null) {
                gameHandler.removeCallbacks(gameRunnable);
            }

            gameManager.saveHighScore(gameView.getScore());

            new Handler().postDelayed(() -> {
                runOnUiThread(this::showGameResults);
            }, 300);
        }
    }

    private void showGameResults() {
        if (!showingResults) return;

<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9
        // Сохраняем результат в базу данных
        int gameDuration = gameManager.getRoundDuration(); // Длительность раунда в секундах
        gameManager.saveGameResult(gameView.getScore(), gameDuration);

<<<<<<< HEAD
=======
=======
>>>>>>> 88605711446fabc01e22bcc44a491cebf1c9f072
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9
        String result = "🎮 Игра завершена!\n\n" +
                "🏆 Очки: " + gameView.getScore() + "\n" +
                "🎯 Промахи: " + gameView.getMisses() + "\n" +
                "⭐ Рекорд: " + gameManager.getHighScore() + "\n\n" +
                "⏱️ Время вышло!";

        new android.app.AlertDialog.Builder(this)
                .setTitle("Результаты игры")
                .setMessage(result)
                .setPositiveButton("🔄 Новая игра", (dialog, which) -> {
                    showingResults = false;
                    gameRunning = true;
                    restartGame();
                })
                .setNegativeButton("🚪 В меню", (dialog, which) -> {
                    showingResults = false;
                    exitToMenu();
                })
                .setOnDismissListener(dialog -> {
                    showingResults = false;
                })
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        gameView.resetGame();
        startGameLoop();
    }

    private void exitToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_authors) {
            Intent intent = new Intent(this, AuthorsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_rules) {
            Intent intent = new Intent(this, RulesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_restart) {
            restartGame();
            return true;
        } else if (id == R.id.menu_exit) {
            exitToMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameRunning = false;
        if (gameHandler != null) {
            gameHandler.removeCallbacks(gameRunnable);
        }
        gameManager.saveHighScore(gameView.getScore());
        gameView.cleanup(); // Очищаем ресурсы
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!showingResults) {
            gameRunning = true;
            startGameLoop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameRunning = false;
        if (gameHandler != null) {
            gameHandler.removeCallbacks(gameRunnable);
        }
        gameView.cleanup(); // Очищаем ресурсы
    }
}