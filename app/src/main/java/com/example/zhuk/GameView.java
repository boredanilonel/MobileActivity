package com.example.zhuk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
    private static final long BONUS_SPAWN_INTERVAL = 15000; // 15 секунд

    private Random random;
    private long gameStartTime;
    private int roundDuration;
    private boolean gameOver = false;

    private MediaPlayer screamSound;
    private MediaPlayer bonusSound;

    // Для гироскопа
    private float currentTiltX = 0;
    private float currentTiltY = 0;

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

        // Инициализация гироскопа
        gyroscopeManager = new GyroscopeManager(getContext());
        gyroscopeManager.setListener(this);

        loadBitmaps();
        setupPaints();
        setupSounds();
    }

    private void setupSounds() {
        // Создаем звуки (добавьте файлы в res/raw/)
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
        // Загрузка изображений жуков
        int[] bugResources = {R.drawable.bug1, R.drawable.bug2, R.drawable.bug3, R.drawable.bug4};
        for (int resId : bugResources) {
            Bitmap original = BitmapFactory.decodeResource(getResources(), resId);
            Bitmap scaled = Bitmap.createScaledBitmap(original, 250, 250, true);
            bugBitmaps.add(scaled);
        }

        // Загрузка изображений бонусов
        int[] bonusResources = {R.drawable.bonus_gyro, R.drawable.bonus_speed, R.drawable.bonus_freeze};
        for (int resId : bonusResources) {
            Bitmap original = BitmapFactory.decodeResource(getResources(), resId);
            Bitmap scaled = Bitmap.createScaledBitmap(original, 150, 150, true);
            bonusBitmaps.add(scaled);
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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        // Отрисовка бонусов
        for (Bonus bonus : bonuses) {
            bonus.draw(canvas);
        }

        // Отрисовка жуков
        for (Bug bug : bugs) {
            bug.draw(canvas);
        }

        // Отрисовка информации
        int timeLeft = getRemainingTime();

        canvas.drawText("Очки: " + score, 20, 80, scorePaint);
        canvas.drawText("Промахи: " + misses, 20, 150, missPaint);
        canvas.drawText("Время: " + timeLeft + "с", 20, 220, infoPaint);
        canvas.drawText("Жуков: " + bugs.size() + "/" + gameManager.getMaxBugs(), 20, 290, infoPaint);

        // Отображение статуса гироскопа
        if (gyroscopeManager.isGyroscopeActive()) {
            long remaining = gyroscopeManager.getRemainingTime();
            canvas.drawText("🌀 Гироскоп: " + remaining + "с", 20, 360, bonusPaint);

            // Воспроизводим звук крика при активном гироскопе
            //playScreamSound();
        }

        // Отображение статуса заморозки
        if (globalFreeze && System.currentTimeMillis() < freezeEndTime) {
            long remaining = (freezeEndTime - System.currentTimeMillis()) / 1000;
            canvas.drawText("❄️ Заморозка: " + remaining + "с", 20, 430, bonusPaint);
        }

        // Отображение статуса ускорения
        if (globalSpeedBoost && System.currentTimeMillis() < speedBoostEndTime) {
            long remaining = (speedBoostEndTime - System.currentTimeMillis()) / 1000;
            canvas.drawText("⚡ Ускорение: " + remaining + "с", 20, 500, bonusPaint);
        }

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

            // Сначала проверяем бонусы
            for (int i = bonuses.size() - 1; i >= 0; i--) {
                Bonus bonus = bonuses.get(i);
                if (bonus.isTouched(touchX, touchY)) {
                    collectBonus(bonus);
                    bonuses.remove(i);
                    hit = true;
                    break;
                }
            }

            // Затем проверяем жуков
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

            // Штраф за промах
            if (!hit) {
                misses++;
                score = Math.max(0, score - 5);
            }

            invalidate();
        }
        return true;
    }

    private void collectBonus(Bonus bonus) {
        bonus.collect();
        score += bonus.getPoints();

        // Воспроизводим звук бонуса
        playBonusSound();

        // Активируем эффект бонуса
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
    }

    private void activateGyroscopeBonus() {
        gyroscopeManager.startGyroscope();

        // Применяем эффект ко всем жукам (существующим и будущим)
        for (Bug bug : bugs) {
            bug.setAffectedByGyroscope(true);
        }

        // Воспроизводим звук крика при активации гироскопа
        playScreamSound();
    }

    private void activateSpeedBoost() {
        globalSpeedBoost = true;
        speedBoostEndTime = System.currentTimeMillis() + 5000; // 5 секунд

        // Увеличиваем скорость всем жукам в 2 раза
        for (Bug bug : bugs) {
            bug.applySpeedBoost(5000);
        }

        // Запускаем таймер для отключения эффекта
        new android.os.Handler().postDelayed(() -> {
            globalSpeedBoost = false;
            // Скорость вернется автоматически через checkEffects() в Bug
        }, 5000);
    }

    private void activateFreeze() {
        globalFreeze = true;
        freezeEndTime = System.currentTimeMillis() + 3000; // 3 секунды

        // Замораживаем всех жуков
        for (Bug bug : bugs) {
            bug.freeze(3000);
        }

        // Запускаем таймер для отключения эффекта
        new android.os.Handler().postDelayed(() -> {
            globalFreeze = false;
            // Разморозка произойдет автоматически через checkEffects() в Bug
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
        if (gameOver) return;

        if (System.currentTimeMillis() - gameStartTime > roundDuration) {
            gameOver = true;
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Создание новых жуков
        if (currentTime - lastBugSpawnTime > BUG_SPAWN_INTERVAL &&
                bugs.size() < gameManager.getMaxBugs()) {
            spawnBug();
            lastBugSpawnTime = currentTime;
        }

        // Создание бонусов каждые 15 секунд
        if (currentTime - lastBonusSpawnTime > BONUS_SPAWN_INTERVAL) {
            spawnBonus();
            lastBonusSpawnTime = currentTime;
        }

        // Обновление гироскопа
        gyroscopeManager.update();

        // Обновление бонусов
        for (int i = bonuses.size() - 1; i >= 0; i--) {
            Bonus bonus = bonuses.get(i);
            bonus.update();
            if (!bonus.isActive()) {
                bonuses.remove(i);
            }
        }

        // Обновление позиций жуков с учетом гироскопа
        for (int i = bugs.size() - 1; i >= 0; i--) {
            Bug bug = bugs.get(i);
            bug.update(getWidth(), getHeight(), currentTiltX, currentTiltY);

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

        // Применяем активные эффекты к новому жуку
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

    // Реализация GyroscopeListener
    @Override
    public void onTiltChanged(float tiltX, float tiltY) {
        currentTiltX = tiltX;
        currentTiltY = tiltY;
    }

    @Override
    public void onGyroscopeActivated() {
        // Применяем эффект ко всем жукам (включая новых)
        for (Bug bug : bugs) {
            bug.setAffectedByGyroscope(true);
        }
        //playScreamSound();
    }

    @Override
    public void onGyroscopeDeactivated() {
        currentTiltX = 0;
        currentTiltY = 0;

        // Отключаем эффект гироскопа у всех жуков
        for (Bug bug : bugs) {
            bug.setAffectedByGyroscope(false);
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
        score = 0;
        misses = 0;
        lastBugSpawnTime = System.currentTimeMillis();
        lastBonusSpawnTime = System.currentTimeMillis();
        gameStartTime = System.currentTimeMillis();
        gameOver = false;

        // Сбрасываем все эффекты
        globalFreeze = false;
        globalSpeedBoost = false;
        freezeEndTime = 0;
        speedBoostEndTime = 0;

        gyroscopeManager.stopGyroscope();
        currentTiltX = 0;
        currentTiltY = 0;

        roundDuration = gameManager.getRoundDuration() * 1000;

        invalidate();
    }
}