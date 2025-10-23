package com.example.zhuk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class GoldenBug {
    private Bitmap bitmap;
    private int x, y;
    private int speedX, speedY;
    private int width, height;
    private boolean isAlive;
    private Rect bounds;
    private int baseSpeed;
    private double goldRate;
    private boolean isAffectedByGyroscope = false;

    private int originalSpeedX, originalSpeedY;
    private boolean isFrozen = false;
    private boolean isSpeedBoosted = false;
    private long effectEndTime = 0;

    public GoldenBug(Bitmap bitmap, int startX, int startY, int baseSpeed, double goldRate) {
        this.bitmap = bitmap;
        this.x = startX;
        this.y = startY;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.isAlive = true;
        this.baseSpeed = baseSpeed;
        this.goldRate = goldRate;
        this.speedX = (Math.random() > 0.5 ? 1 : -1) * baseSpeed;
        this.speedY = (Math.random() > 0.5 ? 1 : -1) * baseSpeed;
        this.originalSpeedX = this.speedX;
        this.originalSpeedY = this.speedY;

        this.bounds = new Rect(x, y, x + width, y + height);
    }

    public void update(int screenWidth, int screenHeight, float tiltX, float tiltY) {
        if (!isAlive) return;

        checkEffects();

        if (isFrozen) {
            return;
        }

        if (isAffectedByGyroscope) {
            speedX += tiltX * 3;
            speedY += tiltY * 3;

            int maxSpeed = 15;
            speedX = Math.max(-maxSpeed, Math.min(maxSpeed, speedX));
            speedY = Math.max(-maxSpeed, Math.min(maxSpeed, speedY));
        }

        x += speedX;
        y += speedY;

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
            if (isFrozen) {
                canvas.drawBitmap(bitmap, x, y, null);
            }
            else if (isSpeedBoosted) {
                canvas.drawBitmap(bitmap, x, y, null);
            }
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
        int basePoints = (int) (goldRate / 10);
        if (isSpeedBoosted) basePoints += 10;
        if (isFrozen) basePoints += 5;
        return basePoints;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setAffectedByGyroscope(boolean affected) {
        this.isAffectedByGyroscope = affected;
    }

    public boolean isAffectedByGyroscope() {
        return isAffectedByGyroscope;
    }

    public double getGoldRate() {
        return goldRate;
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
    public void updateGoldRate(double newGoldRate) {
        this.goldRate = newGoldRate;
    }
}