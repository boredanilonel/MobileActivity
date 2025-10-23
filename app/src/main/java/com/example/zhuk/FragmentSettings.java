package com.example.zhuk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private Button btnApply;
    private Button btnReset;
    private int currentSpeed;
    private int currentMaxBugs;
    private int currentBonusInterval;
    private int currentRoundDuration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeViews(view);
        loadSettings();
        setupSeekBars();
        setupButtons();

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

        btnApply = view.findViewById(R.id.btnApply);
        btnReset = view.findViewById(R.id.btnReset);
    }

    private void loadSettings() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        currentSpeed = prefs.getInt("game_speed", 5);
        currentMaxBugs = prefs.getInt("max_bugs", 10);
        currentBonusInterval = prefs.getInt("bonus_interval", 30);
        currentRoundDuration = prefs.getInt("round_duration", 60);

        seekBarSpeed.setProgress(currentSpeed - 1);
        seekBarMaxCockroaches.setProgress(currentMaxBugs - 1);
        seekBarBonusInterval.setProgress(currentBonusInterval - 10);
        seekBarRoundDuration.setProgress(currentRoundDuration - 30);

        updateAllTextViews();
    }

    private void setupSeekBars() {
        seekBarSpeed.setMax(9);
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentSpeed = progress + 1;
                tvSpeedValue.setText(currentSpeed + " ед.");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarMaxCockroaches.setMax(19);
        seekBarMaxCockroaches.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentMaxBugs = progress + 1;
                tvMaxCockroachesValue.setText(currentMaxBugs + " шт.");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarBonusInterval.setMax(50);
        seekBarBonusInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentBonusInterval = progress + 10;
                tvBonusIntervalValue.setText(currentBonusInterval + " сек.");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarRoundDuration.setMax(90);
        seekBarRoundDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRoundDuration = progress + 30;
                tvRoundDurationValue.setText(currentRoundDuration + " сек.");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupButtons() {
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(getContext(), "Настройки применены!", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetToDefaults();
                Toast.makeText(getContext(), "Настройки сброшены!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSettings() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("game_speed", currentSpeed);
        editor.putInt("max_bugs", currentMaxBugs);
        editor.putInt("bonus_interval", currentBonusInterval);
        editor.putInt("round_duration", currentRoundDuration);

        editor.apply();

        GameManager gameManager = new GameManager();
        gameManager.initialize(getContext());
        gameManager.saveGameSettings(currentSpeed, currentMaxBugs, currentBonusInterval, currentRoundDuration);
    }

    private void resetToDefaults() {
        currentSpeed = 5;
        currentMaxBugs = 10;
        currentBonusInterval = 30;
        currentRoundDuration = 60;

        seekBarSpeed.setProgress(currentSpeed - 1);
        seekBarMaxCockroaches.setProgress(currentMaxBugs - 1);
        seekBarBonusInterval.setProgress(currentBonusInterval - 10);
        seekBarRoundDuration.setProgress(currentRoundDuration - 30);

        updateAllTextViews();

        saveSettings();
    }

    private void updateAllTextViews() {
        tvSpeedValue.setText(currentSpeed + " ед.");
        tvMaxCockroachesValue.setText(currentMaxBugs + " шт.");
        tvBonusIntervalValue.setText(currentBonusInterval + " сек.");
        tvRoundDurationValue.setText(currentRoundDuration + " сек.");
    }
}