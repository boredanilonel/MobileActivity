package com.example.zhuk;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class RecordsActivity extends AppCompatActivity {

    private ListView listViewRecords;
    private TextView tvEmpty;
    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Таблица рекордов");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listViewRecords = findViewById(R.id.listViewRecords);
        tvEmpty = findViewById(R.id.tvEmpty);

        gameManager = new GameManager();
        gameManager.initialize(this);

        loadRecords();
    }

    private void loadRecords() {
        gameManager.getTopScores(50, new GameManager.ScoresCallback() {
            @Override
            public void onScoresLoaded(List<ScoreRecord> scores) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (scores.isEmpty()) {
                            tvEmpty.setText("Пока нет рекордов!\nСыграйте в игру чтобы установить первый рекорд.");
                            listViewRecords.setVisibility(ListView.GONE);
                            tvEmpty.setVisibility(TextView.VISIBLE);
                        } else {
                            RecordsAdapter adapter = new RecordsAdapter(RecordsActivity.this, scores);
                            listViewRecords.setAdapter(adapter);
                            listViewRecords.setVisibility(ListView.VISIBLE);
                            tvEmpty.setVisibility(TextView.GONE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}