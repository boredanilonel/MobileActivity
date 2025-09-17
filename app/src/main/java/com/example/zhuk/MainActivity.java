package com.example.zhuk;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private EditText etFullName;
    private RadioGroup rgGender;
    private Spinner spCourse;
    private SeekBar sbDifficulty;
    private TextView tvDifficultyValue;

    private String[] courses;
    private String[] difficultyLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSpinner();
        setupSeekBar();

    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        rgGender = findViewById(R.id.rgGender);
        spCourse = findViewById(R.id.spCourse);
        sbDifficulty = findViewById(R.id.sbDifficulty);
        tvDifficultyValue = findViewById(R.id.tvDifficultyValue);

        courses = getResources().getStringArray(R.array.courses);
        difficultyLevels = getResources().getStringArray(R.array.difficulty_levels);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                courses
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCourse.setAdapter(adapter);
    }

    private void setupSeekBar() {
        sbDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String levelText;
                if (progress < difficultyLevels.length) {
                    levelText = difficultyLevels[progress] + " (" + progress + ")";
                } else {
                    levelText = "Уровень " + progress;
                }
                tvDifficultyValue.setText(levelText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}