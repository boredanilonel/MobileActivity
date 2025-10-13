package com.example.zhuk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bug {
    private Bitmap bitmap;
    private int x, y;
    private int speedX, speedY;
    private int width, height;
    private boolean isAlive;
    private Rect bounds;
    private int baseSpeed;
    private int originalSpeedX, originalSpeedY;
    private boolean isAffectedByGyroscope = false;
    private boolean isFrozen = false;
    private boolean isSpeedBoosted = false;
    private long effectEndTime = 0;

    public Bug(Bitmap bitmap, int startX, int startY, int baseSpeed, int gameSpeedSetting) {
        this.bitmap = bitmap;
        this.x = startX;
        this.y = startY;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.isAlive = true;
        this.baseSpeed = baseSpeed;

        int speedMultiplier = 1 + (gameSpeedSetting / 2);

        this.speedX = (Math.random() > 0.5 ? 1 : -1) * baseSpeed * speedMultiplier;
        this.speedY = (Math.random() > 0.5 ? 1 : -1) * baseSpeed * speedMultiplier;
        this.originalSpeedX = this.speedX;
        this.originalSpeedY = this.speedY;

        this.bounds = new Rect(x, y, x + width, y + height);
    }

    public void update(int screenWidth, int screenHeight, float tiltX, float tiltY) {
        if (!isAlive) return;

        // Проверяем окончание эффектов
        checkEffects();

        // Если заморожен - не двигаемся
        if (isFrozen) {
            return;
        }

        // Если активен гироскоп, добавляем влияние наклона
        if (isAffectedByGyroscope) {
            speedX += tiltX * 5; // Увеличиваем влияние наклона
            speedY += tiltY * 5;

            // Ограничиваем максимальную скорость
            int maxSpeed = 25;
            speedX = Math.max(-maxSpeed, Math.min(maxSpeed, speedX));
            speedY = Math.max(-maxSpeed, Math.min(maxSpeed, speedY));
        }

        // Движение
        x += speedX;
        y += speedY;

        // Отскок от границ экрана
        if (x <= 0 || x + width >= screenWidth) {
            speedX = -speedX;
            x = Math.max(0, Math.min(x, screenWidth - width));
        }
        if (y <= 0 || y + height >= screenHeight) {
            speedY = -speedY;
            y = Math.max(0, Math.min(y, screenHeight - height));
        }

        bounds.set(x, y, x + width, y + height);
    }

    private void checkEffects() {
        if (effectEndTime > 0 && System.currentTimeMillis() > effectEndTime) {
            if (isFrozen) {
                unfreeze();
            }
            if (isSpeedBoosted) {
                removeSpeedBoost();
            }
            effectEndTime = 0;
        }
    }

    public void draw(Canvas canvas) {
        if (isAlive) {
            // Эффект заморозки - синий оттенок
            if (isFrozen) {
                // Можно добавить визуальный эффект заморозки
                canvas.drawBitmap(bitmap, x, y, null);
            }
            // Эффект ускорения - красный оттенок
            else if (isSpeedBoosted) {
                canvas.drawBitmap(bitmap, x, y, null);
            }
            // Обычное отображение
            else {
                canvas.drawBitmap(bitmap, x, y, null);
            }
        }
    }

    public boolean isTouched(int touchX, int touchY) {
        return isAlive && bounds.contains(touchX, touchY);
    }

    public void kill() {
        isAlive = false;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getPoints() {
        int basePoints = 10 + (Math.abs(speedX) + Math.abs(speedY)) / 5;
        // Дополнительные очки за эффекты
        if (isSpeedBoosted) basePoints += 5;
        if (isFrozen) basePoints += 3;
        return basePoints;
    }

    public Rect getBounds() {
        return bounds;
    }

    public int getSpeed() {
        return Math.abs(speedX) + Math.abs(speedY);
    }

    // Методы для эффектов
    public void setAffectedByGyroscope(boolean affected) {
        this.isAffectedByGyroscope = affected;
    }

    public boolean isAffectedByGyroscope() {
        return isAffectedByGyroscope;
    }

    public void freeze(long duration) {
        this.isFrozen = true;
        this.effectEndTime = System.currentTimeMillis() + duration;
    }

    public void unfreeze() {
        this.isFrozen = false;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void applySpeedBoost(long duration) {
        if (!isSpeedBoosted) {
            this.originalSpeedX = this.speedX;
            this.originalSpeedY = this.speedY;
            this.speedX *= 2;
            this.speedY *= 2;
            this.isSpeedBoosted = true;
            this.effectEndTime = System.currentTimeMillis() + duration;
        }
    }

    public void removeSpeedBoost() {
        if (isSpeedBoosted) {
            this.speedX = this.originalSpeedX;
            this.speedY = this.originalSpeedY;
            this.isSpeedBoosted = false;
        }
    }

    public boolean isSpeedBoosted() {
        return isSpeedBoosted;
    }
}