package com.example.zhuk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private GameManager gameManager;
    private GameViewModel gameViewModel;
    private Handler gameHandler;
    private Runnable gameRunnable;
    private static final long UPDATE_INTERVAL = 16;

    private boolean showingResults = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        gameView = new GameView(this);
        ((android.view.ViewGroup) findViewById(R.id.game_container)).addView(gameView);

        gameManager = new GameManager();
        gameManager.initialize(this);

        gameView.setGameViewModel(gameViewModel);

        if (!gameViewModel.isGameRunning()) {
            gameViewModel.resetGame();
            gameView.resetGame();
        }

        startGameLoop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (gameView != null) {
            gameView.saveStateToViewModel();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (gameView != null) {
            gameView.saveStateToViewModel();
        }
        recreateGameView();
    }

    private void recreateGameView() {
        if (gameView != null) {
            ((android.view.ViewGroup) gameView.getParent()).removeView(gameView);
            gameView.cleanup();
        }
        gameView = new GameView(this);
        gameView.setGameViewModel(gameViewModel);
        ((android.view.ViewGroup) findViewById(R.id.game_container)).addView(gameView);

        if (gameHandler != null) {
            gameHandler.removeCallbacks(gameRunnable);
        }
        startGameLoop();
    }

    private void startGameLoop() {
        gameHandler = new Handler();
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if (gameViewModel.isGameRunning() && !showingResults) {
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
            gameViewModel.setGameRunning(false);
            showingResults = true;

            if (gameHandler != null) {
                gameHandler.removeCallbacks(gameRunnable);
            }
            int gameDuration = gameManager.getRoundDuration();
            gameManager.saveGameResult(gameView.getScore(), gameDuration);
            gameManager.saveHighScore(gameView.getScore());

            new Handler().postDelayed(() -> {
                runOnUiThread(this::showGameResults);
            }, 300);
        }
    }

    private void showGameResults() {
        if (!showingResults) return;

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
        gameViewModel.resetGame();
        gameView.resetGame();
        gameViewModel.setGameRunning(true);
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
        gameViewModel.setGameRunning(false);
        if (gameHandler != null) {
            gameHandler.removeCallbacks(gameRunnable);
        }
        if (gameView != null) {
            gameView.saveStateToViewModel();
            gameView.cleanup();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!showingResults) {
            gameViewModel.setGameRunning(true);
            startGameLoop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.setGameRunning(false);
        if (gameHandler != null) {
            gameHandler.removeCallbacks(gameRunnable);
        }
        if (gameView != null) {
            gameView.cleanup();
        }
    }
}