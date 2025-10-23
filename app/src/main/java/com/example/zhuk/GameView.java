package com.example.zhuk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View implements GyroscopeManager.GyroscopeListener {
    private List<Bug> bugs;
    private List<Bonus> bonuses;
    private List<Bitmap> bugBitmaps;
    private List<Bitmap> bonusBitmaps;
    private int score;
    private int misses;

    private boolean globalFreeze = false;
    private boolean globalSpeedBoost = false;
    private long freezeEndTime = 0;
    private long speedBoostEndTime = 0;
    private GameManager gameManager;
    private GyroscopeManager gyroscopeManager;

    private Paint scorePaint;
    private Paint missPaint;
    private Paint infoPaint;
    private Paint bonusPaint;

    private long lastBugSpawnTime;
    private long lastBonusSpawnTime;
    private static final long BUG_SPAWN_INTERVAL = 1500;
    private static final long BONUS_SPAWN_INTERVAL = 15000;

    private Random random;
    private long gameStartTime;
    private int roundDuration;
    private boolean gameOver = false;

    private MediaPlayer screamSound;
    private MediaPlayer bonusSound;

    private float currentTiltX = 0;
    private float currentTiltY = 0;

    private List<GoldenBug> goldenBugs;
    private Bitmap goldenBugBitmap;
    private long lastGoldenBugSpawnTime;
    private static final long GOLDEN_BUG_SPAWN_INTERVAL = 20000;
    private GoldRateService goldRateService;
    private double currentGoldRate = 10946.8700;

    private GameViewModel gameViewModel;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void setGameViewModel(GameViewModel viewModel) {
        this.gameViewModel = viewModel;
        restoreStateFromViewModel();
    }
    public void saveStateToViewModel() {
        if (gameViewModel != null) {
            gameViewModel.setScore(score);
            gameViewModel.setMisses(misses);
            gameViewModel.setGameStartTime(gameStartTime);
            gameViewModel.setLastBugSpawnTime(lastBugSpawnTime);
            gameViewModel.setLastBonusSpawnTime(lastBonusSpawnTime);
            gameViewModel.setGameOver(gameOver);

            gameViewModel.setGyroscopeActive(gyroscopeManager.isGyroscopeActive());
            gameViewModel.setGlobalFreeze(globalFreeze);
            gameViewModel.setGlobalSpeedBoost(globalSpeedBoost);
            gameViewModel.setFreezeEndTime(freezeEndTime);
            gameViewModel.setSpeedBoostEndTime(speedBoostEndTime);
        }
    }
    private void restoreStateFromViewModel() {
        if (gameViewModel != null) {
            score = gameViewModel.getScore();
            misses = gameViewModel.getMisses();
            gameStartTime = gameViewModel.getGameStartTime();
            lastBugSpawnTime = gameViewModel.getLastBugSpawnTime();
            lastBonusSpawnTime = gameViewModel.getLastBonusSpawnTime();
            gameOver = gameViewModel.isGameOver();

            if (gameViewModel.isGyroscopeActive()) {
                gyroscopeManager.startGyroscope();
                for (Bug bug : bugs) {
                    bug.setAffectedByGyroscope(true);
                }
                for (GoldenBug goldenBug : goldenBugs) {
                    goldenBug.setAffectedByGyroscope(true);
                }
            }

            globalFreeze = gameViewModel.isGlobalFreeze();
            globalSpeedBoost = gameViewModel.isGlobalSpeedBoost();
            freezeEndTime = gameViewModel.getFreezeEndTime();
            speedBoostEndTime = gameViewModel.getSpeedBoostEndTime();

            if (globalSpeedBoost && System.currentTimeMillis() < speedBoostEndTime) {
                long timeLeft = speedBoostEndTime - System.currentTimeMillis();
                for (Bug bug : bugs) {
                    bug.applySpeedBoost(timeLeft);
                }
            }

            if (globalFreeze && System.currentTimeMillis() < freezeEndTime) {
                long timeLeft = freezeEndTime - System.currentTimeMillis();
                for (Bug bug : bugs) {
                    bug.freeze(timeLeft);
                }
            }
        }
    }

    private void init() {
        bugs = new ArrayList<>();
        bonuses = new ArrayList<>();
        bugBitmaps = new ArrayList<>();
        bonusBitmaps = new ArrayList<>();

        score = 0;
        misses = 0;
        random = new Random();
        lastBugSpawnTime = System.currentTimeMillis();
        lastBonusSpawnTime = System.currentTimeMillis();
        gameStartTime = System.currentTimeMillis();

        gameManager = new GameManager();
        gameManager.initialize(getContext());

        roundDuration = gameManager.getRoundDuration() * 1000;

        gyroscopeManager = new GyroscopeManager(getContext());
        gyroscopeManager.setListener(this);

        goldenBugs = new ArrayList<>();
        goldRateService = new GoldRateService(getContext());
        loadGoldRate();

        loadBitmaps();
        setupPaints();
        setupSounds();
    }

    private void loadGoldRate() {
        goldRateService.loadGoldRate(new GoldRateService.GoldRateCallback() {
            @Override
            public void onGoldRateLoaded(double goldRate) {
                currentGoldRate = goldRate;
                Log.d("GameView", "Gold rate loaded from CBR: " + goldRate + " руб/г");

                GoldWidget.updateAllWidgets(getContext());
            }

            @Override
            public void onError(String error) {
                double cachedRate = goldRateService.getCachedGoldRate();
                currentGoldRate = cachedRate > 0 ? cachedRate : 0;
                Log.e("GameView", "Failed to load gold rate: " + error + ", using: " + currentGoldRate);
            }
        });
    }

    private void setupSounds() {
        screamSound = MediaPlayer.create(getContext(), R.raw.scream);
        bonusSound = MediaPlayer.create(getContext(), R.raw.bonus);

        if (screamSound != null) {
            screamSound.setVolume(0.7f, 0.7f);
        }
        if (bonusSound != null) {
            bonusSound.setVolume(1.0f, 1.0f);
        }
    }

    private void loadBitmaps() {
        int[] bugResources = {R.drawable.bug1, R.drawable.bug2, R.drawable.bug3, R.drawable.bug4};
        for (int resId : bugResources) {
            Bitmap original = BitmapFactory.decodeResource(getResources(), resId);
            Bitmap scaled = Bitmap.createScaledBitmap(original, 250, 250, true);
            bugBitmaps.add(scaled);
        }

        int[] bonusResources = {R.drawable.bonus_gyro, R.drawable.bonus_speed, R.drawable.bonus_freeze};
        for (int resId : bonusResources) {
            Bitmap original = BitmapFactory.decodeResource(getResources(), resId);
            Bitmap scaled = Bitmap.createScaledBitmap(original, 150, 150, true);
            bonusBitmaps.add(scaled);
        }

        goldenBugBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.golden_bug);
        if (goldenBugBitmap != null) {
            goldenBugBitmap = Bitmap.createScaledBitmap(goldenBugBitmap, 120, 120, true);
        }
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

        bonusPaint = new Paint();
        bonusPaint.setColor(Color.MAGENTA);
        bonusPaint.setTextSize(35);
        bonusPaint.setAntiAlias(true);
        bonusPaint.setShadowLayer(3, 2, 2, Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        for (Bonus bonus : bonuses) {
            bonus.draw(canvas);
        }

        for (Bug bug : bugs) {
            bug.draw(canvas);
        }

        for (GoldenBug goldenBug : goldenBugs) {
            goldenBug.draw(canvas);
        }

        int timeLeft = getRemainingTime();

        canvas.drawText("Очки: " + score, 20, 80, scorePaint);
        canvas.drawText("Промахи: " + misses, 20, 150, missPaint);
        canvas.drawText("Время: " + timeLeft + "с", 20, 220, infoPaint);
        canvas.drawText("Жуков: " + bugs.size() + "/" + gameManager.getMaxBugs(), 20, 290, infoPaint);

        if (gyroscopeManager.isGyroscopeActive()) {
            long remaining = gyroscopeManager.getRemainingTime();
            canvas.drawText("🌀 Гироскоп: " + remaining + "с", 20, 360, bonusPaint);
        }

        if (globalFreeze && System.currentTimeMillis() < freezeEndTime) {
            long remaining = (freezeEndTime - System.currentTimeMillis()) / 1000;
            canvas.drawText("❄️ Заморозка: " + remaining + "с", 20, 430, bonusPaint);
        }

        if (globalSpeedBoost && System.currentTimeMillis() < speedBoostEndTime) {
            long remaining = (speedBoostEndTime - System.currentTimeMillis()) / 1000;
            canvas.drawText("⚡ Ускорение: " + remaining + "с", 20, 500, bonusPaint);
        }

        canvas.drawText("💰 Золото: " + (int)currentGoldRate + " руб/г", 20, 570, infoPaint);

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

            for (int i = bonuses.size() - 1; i >= 0; i--) {
                Bonus bonus = bonuses.get(i);
                if (bonus.isTouched(touchX, touchY)) {
                    collectBonus(bonus);
                    bonuses.remove(i);
                    hit = true;
                    break;
                }
            }

            if (!hit) {
                for (int i = goldenBugs.size() - 1; i >= 0; i--) {
                    GoldenBug goldenBug = goldenBugs.get(i);
                    if (goldenBug.isTouched(touchX, touchY)) {
                        collectGoldenBug(goldenBug);
                        goldenBugs.remove(i);
                        hit = true;
                        break;
                    }
                }
            }

            if (!hit) {
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
            }

            if (!hit) {
                misses++;
                score = Math.max(0, score - 5);
            }

            saveStateToViewModel();
            invalidate();
        }
        return true;
    }

    private void collectGoldenBug(GoldenBug goldenBug) {
        goldenBug.kill();
        int points = goldenBug.getPoints();
        score += points;

        Toast.makeText(getContext(), "💰 +" + points + " очков (золото)!", Toast.LENGTH_SHORT).show();
        saveStateToViewModel();
    }

    private void collectBonus(Bonus bonus) {
        bonus.collect();
        score += bonus.getPoints();

        playBonusSound();

        switch (bonus.getType()) {
            case GYROSCOPE:
                activateGyroscopeBonus();
                break;
            case SPEED_BOOST:
                activateSpeedBoost();
                break;
            case FREEZE:
                activateFreeze();
                break;
        }
        saveStateToViewModel();
    }

    private void activateGyroscopeBonus() {
        gyroscopeManager.startGyroscope();

        if (gameViewModel != null) {
            gameViewModel.setGyroscopeActive(true);
            gameViewModel.setGyroscopeEndTime(System.currentTimeMillis() + 15000);
        }

        for (Bug bug : bugs) {
            bug.setAffectedByGyroscope(true);
        }
        for (GoldenBug goldenBug : goldenBugs) {
            goldenBug.setAffectedByGyroscope(true);
        }

        playScreamSound();
    }

    private void activateSpeedBoost() {
        globalSpeedBoost = true;
        speedBoostEndTime = System.currentTimeMillis() + 5000;

        if (gameViewModel != null) {
            gameViewModel.setGlobalSpeedBoost(true);
            gameViewModel.setSpeedBoostEndTime(speedBoostEndTime);
        }

        for (Bug bug : bugs) {
            bug.applySpeedBoost(5000);
        }

        new android.os.Handler().postDelayed(() -> {
            globalSpeedBoost = false;
            if (gameViewModel != null) {
                gameViewModel.setGlobalSpeedBoost(false);
            }
        }, 5000);
    }

    private void activateFreeze() {
        globalFreeze = true;
        freezeEndTime = System.currentTimeMillis() + 3000;

        if (gameViewModel != null) {
            gameViewModel.setGlobalFreeze(true);
            gameViewModel.setFreezeEndTime(freezeEndTime);
        }

        for (Bug bug : bugs) {
            bug.freeze(3000);
        }

        new android.os.Handler().postDelayed(() -> {
            globalFreeze = false;
            if (gameViewModel != null) {
                gameViewModel.setGlobalFreeze(false);
            }
        }, 3000);
    }

    private void playScreamSound() {
        if (screamSound != null) {
            if (screamSound.isPlaying()) {
                screamSound.seekTo(0);
            }
            screamSound.start();
        }
    }

    private void playBonusSound() {
        if (bonusSound != null) {
            if (bonusSound.isPlaying()) {
                bonusSound.seekTo(0);
            }
            bonusSound.start();
        }
    }

    public void update() {
        if (gameOver) {
            saveStateToViewModel();
            return;
        }

        if (System.currentTimeMillis() - gameStartTime > roundDuration) {
            gameOver = true;
            if (gameViewModel != null) {
                gameViewModel.setGameOver(true);
            }
            saveStateToViewModel();
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBugSpawnTime > BUG_SPAWN_INTERVAL &&
                bugs.size() < gameManager.getMaxBugs()) {
            spawnBug();
            lastBugSpawnTime = currentTime;
            if (gameViewModel != null) {
                gameViewModel.setLastBugSpawnTime(lastBugSpawnTime);
            }
        }

        if (currentTime - lastBonusSpawnTime > BONUS_SPAWN_INTERVAL) {
            spawnBonus();
            lastBonusSpawnTime = currentTime;
            if (gameViewModel != null) {
                gameViewModel.setLastBonusSpawnTime(lastBonusSpawnTime);
            }
        }

        if (currentTime - lastGoldenBugSpawnTime > GOLDEN_BUG_SPAWN_INTERVAL) {
            spawnGoldenBug();
            lastGoldenBugSpawnTime = currentTime;
        }

        gyroscopeManager.update();

        for (int i = bonuses.size() - 1; i >= 0; i--) {
            Bonus bonus = bonuses.get(i);
            bonus.update();
            if (!bonus.isActive()) {
                bonuses.remove(i);
            }
        }

        for (int i = bugs.size() - 1; i >= 0; i--) {
            Bug bug = bugs.get(i);
            bug.update(getWidth(), getHeight(), currentTiltX, currentTiltY);

            if (!bug.isAlive()) {
                bugs.remove(i);
            }
        }

        for (int i = goldenBugs.size() - 1; i >= 0; i--) {
            GoldenBug goldenBug = goldenBugs.get(i);
            goldenBug.update(getWidth(), getHeight(), currentTiltX, currentTiltY);
            if (!goldenBug.isAlive()) {
                goldenBugs.remove(i);
            }
        }

        if (currentTime % 1000 < 16) {
            saveStateToViewModel();
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

        if (gyroscopeManager.isGyroscopeActive()) {
            newBug.setAffectedByGyroscope(true);
        }
        if (globalSpeedBoost && System.currentTimeMillis() < speedBoostEndTime) {
            long timeLeft = speedBoostEndTime - System.currentTimeMillis();
            newBug.applySpeedBoost(timeLeft);
        }
        if (globalFreeze && System.currentTimeMillis() < freezeEndTime) {
            long timeLeft = freezeEndTime - System.currentTimeMillis();
            newBug.freeze(timeLeft);
        }

        bugs.add(newBug);
    }

    private void spawnGoldenBug() {
        if (goldenBugBitmap == null) return;

        int x = random.nextInt(Math.max(1, getWidth() - goldenBugBitmap.getWidth()));
        int y = random.nextInt(Math.max(1, getHeight() - goldenBugBitmap.getHeight()));
        int baseSpeed = 2;

        GoldenBug goldenBug = new GoldenBug(goldenBugBitmap, x, y, baseSpeed, currentGoldRate);

        if (gyroscopeManager.isGyroscopeActive()) {
            goldenBug.setAffectedByGyroscope(true);
        }

        goldenBugs.add(goldenBug);
    }

    private void spawnBonus() {
        if (bonusBitmaps.isEmpty()) return;

        Bonus.BonusType[] types = Bonus.BonusType.values();
        Bonus.BonusType randomType = types[random.nextInt(types.length)];

        Bitmap bonusBitmap = bonusBitmaps.get(randomType.ordinal());
        int x = random.nextInt(Math.max(1, getWidth() - bonusBitmap.getWidth()));
        int y = random.nextInt(Math.max(1, getHeight() - bonusBitmap.getHeight()));

        Bonus newBonus = new Bonus(bonusBitmap, x, y, randomType);
        bonuses.add(newBonus);
    }

    @Override
    public void onTiltChanged(float tiltX, float tiltY) {
        currentTiltX = tiltX;
        currentTiltY = tiltY;
    }

    @Override
    public void onGyroscopeActivated() {
        for (Bug bug : bugs) {
            bug.setAffectedByGyroscope(true);
        }
        for (GoldenBug goldenBug : goldenBugs) {
            goldenBug.setAffectedByGyroscope(true);
        }
        if (gameViewModel != null) {
            gameViewModel.setGyroscopeActive(true);
        }
    }

    @Override
    public void onGyroscopeDeactivated() {
        currentTiltX = 0;
        currentTiltY = 0;

        for (Bug bug : bugs) {
            bug.setAffectedByGyroscope(false);
        }
        for (GoldenBug goldenBug : goldenBugs) {
            goldenBug.setAffectedByGyroscope(false);
        }
        if (gameViewModel != null) {
            gameViewModel.setGyroscopeActive(false);
        }
    }

    public void cleanup() {
        gyroscopeManager.cleanup();
        if (screamSound != null) {
            screamSound.release();
        }
        if (bonusSound != null) {
            bonusSound.release();
        }
    }

    public int getScore() { return score; }
    public int getMisses() { return misses; }
    public boolean isGameOver() { return gameOver; }

    public int getRemainingTime() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        return Math.max(0, (int)((roundDuration - elapsed) / 1000));
    }

    public void resetGame() {
        bugs.clear();
        bonuses.clear();
        goldenBugs.clear();
        score = 0;
        misses = 0;
        lastBugSpawnTime = System.currentTimeMillis();
        lastBonusSpawnTime = System.currentTimeMillis();
        lastGoldenBugSpawnTime = System.currentTimeMillis();
        gameStartTime = System.currentTimeMillis();
        gameOver = false;

        globalFreeze = false;
        globalSpeedBoost = false;
        freezeEndTime = 0;
        speedBoostEndTime = 0;

        gyroscopeManager.stopGyroscope();
        currentTiltX = 0;
        currentTiltY = 0;
        loadGoldRate();
        roundDuration = gameManager.getRoundDuration() * 1000;

        // Сбрасываем ViewModel
        if (gameViewModel != null) {
            gameViewModel.resetGame();
        }

        invalidate();
    }
}