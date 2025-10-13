package com.example.zhuk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bonus {
    private Bitmap bitmap;
    private int x, y;
    private int width, height;
    private boolean isActive;
    private Rect bounds;
    private long spawnTime;
    private static final long BONUS_DURATION = 8000; // 8 секунд на сбор
    private BonusType type;

    public enum BonusType {
        GYROSCOPE,  // Бонус гироскопа
        SPEED_BOOST, // Ускорение
        FREEZE       // Заморозка
    }

    public Bonus(Bitmap bitmap, int x, int y, BonusType type) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.isActive = true;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
        this.bounds = new Rect(x, y, x + width, y + height);
    }

    public void update() {
        // Проверяем время жизни бонуса
        if (System.currentTimeMillis() - spawnTime > BONUS_DURATION) {
            isActive = false;
        }
    }

    public void draw(Canvas canvas) {
        if (isActive) {
            // Мигающий эффект перед исчезновением
            long timeLeft = BONUS_DURATION - (System.currentTimeMillis() - spawnTime);
            if (timeLeft > 2000 || (timeLeft / 200) % 2 == 0) {
                canvas.drawBitmap(bitmap, x, y, null);
            }
        }
    }

    public boolean isTouched(int touchX, int touchY) {
        return isActive && bounds.contains(touchX, touchY);
    }

    public void collect() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public BonusType getType() {
        return type;
    }

    public Rect getBounds() {
        return bounds;
    }

    public int getPoints() {
        switch (type) {
            case GYROSCOPE:
                return 50;
            case SPEED_BOOST:
                return 30;
            case FREEZE:
                return 40;
            default:
                return 20;
        }
    }
}