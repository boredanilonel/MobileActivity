package com.example.zhuk;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GyroscopeManager implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] gravity = new float[3];
    private GyroscopeListener listener;
    private boolean isGyroscopeActive = false;
    private long gyroscopeEndTime = 0;
    private static final long GYROSCOPE_DURATION = 15000; // 15 секунд

    public interface GyroscopeListener {
        void onTiltChanged(float tiltX, float tiltY);
        void onGyroscopeActivated();
        void onGyroscopeDeactivated();
    }

    public GyroscopeManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public void setListener(GyroscopeListener listener) {
        this.listener = listener;
    }

    public void startGyroscope() {
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            isGyroscopeActive = true;
            gyroscopeEndTime = System.currentTimeMillis() + GYROSCOPE_DURATION;

            if (listener != null) {
                listener.onGyroscopeActivated();
            }
        }
    }

    public void stopGyroscope() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            isGyroscopeActive = false;

            if (listener != null) {
                listener.onGyroscopeDeactivated();
            }
        }
    }

    public void update() {
        if (isGyroscopeActive && System.currentTimeMillis() > gyroscopeEndTime) {
            stopGyroscope();
        }
    }

    public boolean isGyroscopeActive() {
        return isGyroscopeActive;
    }

    public long getRemainingTime() {
        if (!isGyroscopeActive) return 0;
        return Math.max(0, (gyroscopeEndTime - System.currentTimeMillis()) / 1000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isGyroscopeActive) {
            // Фильтр для плавности
            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            float tiltX = gravity[0];
            float tiltY = gravity[1];

            // Нормализуем значения
            tiltX = Math.max(-1, Math.min(1, tiltX / 10));
            tiltY = Math.max(-1, Math.min(1, tiltY / 10));

            if (listener != null) {
                listener.onTiltChanged(tiltX, tiltY);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Не используется
    }

    public void cleanup() {
        stopGyroscope();
    }
}