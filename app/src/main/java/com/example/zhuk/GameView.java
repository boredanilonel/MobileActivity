package com.example.zhuk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    private List<Bug> bugs;
    private List<Bitmap> bugBitmaps;
    private int score;
    private int misses;
    private GameManager gameManager;
    private Paint scorePaint;
    private Paint missPaint;
    private Paint infoPaint;
    private long lastBugSpawnTime;
    private static final long BUG_SPAWN_INTERVAL = 1500;
    private Random random;
    private long gameStartTime;
    private int roundDuration;
    private boolean gameOver = false;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bugs = new ArrayList<>();
        bugBitmaps = new ArrayList<>();
        score = 0;
        misses = 0;
        random = new Random();
        lastBugSpawnTime = System.currentTimeMillis();
        gameStartTime = System.currentTimeMillis();

        gameManager = new GameManager();
        gameManager.initialize(getContext());

        roundDuration = gameManager.getRoundDuration() * 1000;

        loadBugBitmaps();
        setupPaints();
    }

    private void setupPaints() {
        scorePaint = new Paint();
        scorePaint.setColor(Color.GREEN);
        scorePaint.setTextSize(60);
        scorePaint.setAntiAlias(true);
        scorePaint.setShadowLayer(5, 3, 3, Color.BLACK);

        missPaint = new Paint();
        missPaint.setColor(Color.RED);
        missPaint.setTextSize(60);
        missPaint.setAntiAlias(true);
        missPaint.setShadowLayer(5, 3, 3, Color.BLACK);

        infoPaint = new Paint();
        infoPaint.setColor(Color.BLUE);
        infoPaint.setTextSize(40);
        infoPaint.setAntiAlias(true);
        infoPaint.setShadowLayer(3, 2, 2, Color.BLACK);
    }

    private void loadBugBitmaps() {
        int[] bugResources = {
                R.drawable.bug1,
                R.drawable.bug2,
                R.drawable.bug3,
                R.drawable.bug4
        };

        for (int resId : bugResources) {
            Bitmap original = BitmapFactory.decodeResource(getResources(), resId);
            Bitmap scaled = Bitmap.createScaledBitmap(original, 250, 250, true);
            bugBitmaps.add(scaled);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        for (Bug bug : bugs) {
            bug.draw(canvas);
        }

        int timeLeft = getRemainingTime();

        canvas.drawText("Очки: " + score, 20, 80, scorePaint);
        canvas.drawText("Промахи: " + misses, 20, 150, missPaint);
        canvas.drawText("Время: " + timeLeft + "с", 20, 220, infoPaint);
        canvas.drawText("Жуков: " + bugs.size() + "/" + gameManager.getMaxBugs(), 20, 290, infoPaint);

        // Отладочная информация
        if (gameOver) {
            Paint gameOverPaint = new Paint();
            gameOverPaint.setColor(Color.RED);
            gameOverPaint.setTextSize(80);
            gameOverPaint.setTextAlign(Paint.Align.CENTER);
            gameOverPaint.setShadowLayer(10, 5, 5, Color.BLACK);
            canvas.drawText("ИГРА ОКОНЧЕНА", getWidth() / 2, getHeight() / 2, gameOverPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();

            boolean hit = false;

            for (int i = bugs.size() - 1; i >= 0; i--) {
                Bug bug = bugs.get(i);
                if (bug.isTouched(touchX, touchY)) {
                    bug.kill();
                    score += bug.getPoints();
                    hit = true;
                    bugs.remove(i);
                    break;
                }
            }

            if (!hit) {
                misses++;
                score = Math.max(0, score - 5);
            }

            invalidate();
        }
        return true;
    }

    public void update() {
        if (gameOver) return;

        if (System.currentTimeMillis() - gameStartTime > roundDuration) {
            gameOver = true;
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBugSpawnTime > BUG_SPAWN_INTERVAL &&
                bugs.size() < gameManager.getMaxBugs()) {
            spawnBug();
            lastBugSpawnTime = currentTime;
        }

        for (int i = bugs.size() - 1; i >= 0; i--) {
            Bug bug = bugs.get(i);
            bug.update(getWidth(), getHeight());

            if (!bug.isAlive()) {
                bugs.remove(i);
            }
        }

        invalidate();
    }

    private void spawnBug() {
        if (bugBitmaps.isEmpty()) return;

        Bitmap bugBitmap = bugBitmaps.get(random.nextInt(bugBitmaps.size()));

        int startX = random.nextInt(Math.max(1, getWidth() - bugBitmap.getWidth()));
        int startY = random.nextInt(Math.max(1, getHeight() - bugBitmap.getHeight()));

        int baseSpeed = 3 + random.nextInt(5);
        int gameSpeedSetting = gameManager.getGameSpeed();

        Bug newBug = new Bug(bugBitmap, startX, startY, baseSpeed, gameSpeedSetting);
        bugs.add(newBug);
    }

    public int getScore() {
        return score;
    }

    public int getMisses() {
        return misses;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getRemainingTime() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        return Math.max(0, (int)((roundDuration - elapsed) / 1000));
    }

    public void resetGame() {
        bugs.clear();
        score = 0;
        misses = 0;
        lastBugSpawnTime = System.currentTimeMillis();
        gameStartTime = System.currentTimeMillis();
        gameOver = false;

        roundDuration = gameManager.getRoundDuration() * 1000;

        invalidate();
    }
}