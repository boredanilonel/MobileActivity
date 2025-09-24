package com.example.zhuk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentSettings extends Fragment {

    private SeekBar seekBarSpeed;
    private SeekBar seekBarMaxCockroaches;
    private SeekBar seekBarBonusInterval;
    private SeekBar seekBarRoundDuration;

    private TextView tvSpeedValue;
    private TextView tvMaxCockroachesValue;
    private TextView tvBonusIntervalValue;
    private TextView tvRoundDurationValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeViews(view);
        setupSeekBars();
        loadSettings();

        return view;
    }

    private void initializeViews(View view) {
        seekBarSpeed = view.findViewById(R.id.seekBarSpeed);
        seekBarMaxCockroaches = view.findViewById(R.id.seekBarMaxCockroaches);
        seekBarBonusInterval = view.findViewById(R.id.seekBarBonusInterval);
        seekBarRoundDuration = view.findViewById(R.id.seekBarRoundDuration);

        tvSpeedValue = view.findViewById(R.id.tvSpeedValue);
        tvMaxCockroachesValue = view.findViewById(R.id.tvMaxCockroachesValue);
        tvBonusIntervalValue = view.findViewById(R.id.tvBonusIntervalValue);
        tvRoundDurationValue = view.findViewById(R.id.tvRoundDurationValue);
    }

    private void setupSeekBars() {
        setupSeekBar(seekBarSpeed, tvSpeedValue, 5, 1, 10, "ед.");
        setupSeekBar(seekBarMaxCockroaches, tvMaxCockroachesValue, 10, 1, 20, "шт.");
        setupSeekBar(seekBarBonusInterval, tvBonusIntervalValue, 30, 10, 60, "сек.");
        setupSeekBar(seekBarRoundDuration, tvRoundDurationValue, 60, 30, 120, "сек.");
    }

    private void setupSeekBar(SeekBar seekBar, TextView textView, int defaultValue, int min, int max, String unit) {
        seekBar.setMax(max - min);
        seekBar.setProgress(defaultValue - min);

        updateSeekBarText(textView, defaultValue, unit);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + min;
                updateSeekBarText(textView, value, unit);
                saveSettings();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateSeekBarText(TextView textView, int value, String unit) {
        textView.setText(value + " " + unit);
    }

    private void loadSettings() {
        // Загрузка сохраненных настроек
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        seekBarSpeed.setProgress(prefs.getInt("speed", 4));
        seekBarMaxCockroaches.setProgress(prefs.getInt("maxCockroaches", 9));
        seekBarBonusInterval.setProgress(prefs.getInt("bonusInterval", 20));
        seekBarRoundDuration.setProgress(prefs.getInt("roundDuration", 30));
    }

    private void saveSettings() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("speed", seekBarSpeed.getProgress());
        editor.putInt("maxCockroaches", seekBarMaxCockroaches.getProgress());
        editor.putInt("bonusInterval", seekBarBonusInterval.getProgress());
        editor.putInt("roundDuration", seekBarRoundDuration.getProgress());

        editor.apply();
    }
}
