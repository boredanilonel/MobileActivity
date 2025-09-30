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

    public Bug(Bitmap bitmap, int startX, int startY, int baseSpeed, int gameSpeedSetting) {
        this.bitmap = bitmap;
        this.x = startX;
        this.y = startY;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.isAlive = true;
        this.baseSpeed = baseSpeed;

        int speedMultiplier = 1 + (gameSpeedSetting / 2); // От 1x до 6x скорости

        this.speedX = (Math.random() > 0.5 ? 1 : -1) * baseSpeed * speedMultiplier;
        this.speedY = (Math.random() > 0.5 ? 1 : -1) * baseSpeed * speedMultiplier;

        this.bounds = new Rect(x, y, x + width, y + height);
    }

    public void update(int screenWidth, int screenHeight) {
        if (!isAlive) return;

        // Движение
        x += speedX;
        y += speedY;

        // Отскок от границ экрана
        if (x <= 0 || x + width >= screenWidth) {
            speedX = -speedX;
            // Корректировка позиции чтобы не застревать в стене
            x = Math.max(0, Math.min(x, screenWidth - width));
        }
        if (y <= 0 || y + height >= screenHeight) {
            speedY = -speedY;
            y = Math.max(0, Math.min(y, screenHeight - height));
        }

        // Обновление границ для коллизии
        bounds.set(x, y, x + width, y + height);
    }

    public void draw(Canvas canvas) {
        if (isAlive) {
            canvas.drawBitmap(bitmap, x, y, null);
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
        return 10 + (Math.abs(speedX) + Math.abs(speedY)) / 5;
    }

    public Rect getBounds() {
        return bounds;
    }

    public int getSpeed() {
        return Math.abs(speedX) + Math.abs(speedY);
    }
}