package com.example.zhuk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class GoldRateService {
    private static final String TAG = "GoldRateService";
    private static final String PREF_NAME = "gold_prefs";
    private static final String KEY_GOLD_RATE = "gold_rate";
    private static final String KEY_LAST_UPDATE = "last_update";

    private CbrApi cbrApi;
    private SharedPreferences prefs;

    public GoldRateService(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.cbr.ru/scripts/")
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        cbrApi = retrofit.create(CbrApi.class);
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public interface GoldRateCallback {
        void onGoldRateLoaded(double goldRate);
        void onError(String error);
    }

    public void loadGoldRate(GoldRateCallback callback) {
        long lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0);
        double cachedRate = prefs.getFloat(KEY_GOLD_RATE, 0);

        if (System.currentTimeMillis() - lastUpdate < 14400000 && cachedRate > 0) {
            Log.d(TAG, "Using cached gold rate: " + cachedRate);
            callback.onGoldRateLoaded(cachedRate);
            return;
        }
        loadCurrentGoldRate(callback);
    }

    private void loadCurrentGoldRate(GoldRateCallback callback) {
        String currentDate = getCurrentDate();

        Log.d(TAG, "Loading gold rate for date: " + currentDate);

        Call<MetalData> call = cbrApi.getMetalRates(currentDate, currentDate);

        call.enqueue(new Callback<MetalData>() {
            @Override
            public void onResponse(Call<MetalData> call, Response<MetalData> response) {
                Log.d(TAG, "Response received: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    MetalData metalData = response.body();
                    MetalRate goldRate = metalData.getGoldRate();

                    if (goldRate != null && goldRate.getBuyPrice() > 0) {
                        double rate = goldRate.getBuyPrice();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat(KEY_GOLD_RATE, (float) rate);
                        editor.putLong(KEY_LAST_UPDATE, System.currentTimeMillis());
                        editor.apply();

                        Log.d(TAG, "Gold rate successfully loaded: " + rate + " руб/г");
                        callback.onGoldRateLoaded(rate);
                    } else {
                        Log.w(TAG, "Gold rate not found or invalid in response");
                        handleLoadError("Gold rate not found in response", callback);
                    }
                } else {
                    Log.w(TAG, "Response not successful: " + response.message());
                    handleLoadError("Response failed: " + response.message(), callback);
                }
            }

            @Override
            public void onFailure(Call<MetalData> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                handleLoadError("Network error: " + t.getMessage(), callback);
            }
        });
    }

    private void handleLoadError(String error, GoldRateCallback callback) {
        double cachedRate = getCachedGoldRate();
        if (cachedRate > 0) {
            Log.d(TAG, "Using cached gold rate due to error: " + cachedRate);
            callback.onGoldRateLoaded(cachedRate);
        } else {
            double defaultRate = 10946.3000;
            Log.w(TAG, "Using default gold rate: " + defaultRate);
            callback.onGoldRateLoaded(defaultRate);
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    public double getCachedGoldRate() {
        float rate = prefs.getFloat(KEY_GOLD_RATE, 0);
        return rate > 0 ? rate : 0;
    }

    private String getWeekAgoDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
    public void forceLoadGoldRate(GoldRateCallback callback) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_LAST_UPDATE);
        editor.apply();

        loadGoldRate(callback);
    }
}