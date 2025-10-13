package com.example.zhuk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnRegistration = findViewById(R.id.btnRegistration);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnAuthors = findViewById(R.id.btnAuthors);
        Button btnRules = findViewById(R.id.btnRules);
        TextView tvHighScore = findViewById(R.id.tvHighScore);

        GameManager gameManager = new GameManager();
        gameManager.initialize(this);
        tvHighScore.setText("Рекорд: " + gameManager.getHighScore());

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistration();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        btnAuthors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAuthors();
            }
        });

        btnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRules();
            }
        });

        Button btnRecords = findViewById(R.id.btnRecords);
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecords();
            }
        });
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void openRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openAuthors() {
        Intent intent = new Intent(this, AuthorsActivity.class);
        startActivity(intent);
    }

    private void openRules() {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

    private void openRecords() {
        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
    }
}