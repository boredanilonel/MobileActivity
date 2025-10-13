package com.example.zhuk;

import android.os.Bundle;
import android.view.MenuItem;
<<<<<<< HEAD
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.app.AlertDialog;
=======
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {
    private ListView listViewRecords;
    private TextView tvEmpty;
    private Spinner spinnerPlayers;
    private Button btnClear;
    private GameManager gameManager;
    private List<Player> allPlayers;
    private Player selectedPlayer;
=======
import java.util.List;

public class RecordsActivity extends AppCompatActivity {

    private ListView listViewRecords;
    private TextView tvEmpty;
    private GameManager gameManager;
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9

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
<<<<<<< HEAD
        spinnerPlayers = findViewById(R.id.spinnerPlayers);
        btnClear = findViewById(R.id.btnClear);
=======
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9

        gameManager = new GameManager();
        gameManager.initialize(this);

<<<<<<< HEAD
        setupPlayerSpinner();
        setupClearButton();
        loadAllRecords();
    }

    private void setupPlayerSpinner() {
        // Загружаем список всех игроков
        gameManager.getAllPlayers(new GameManager.PlayersCallback() {
            @Override
            public void onPlayersLoaded(List<Player> players) {
                runOnUiThread(() -> {
                    allPlayers = players;

                    // Создаем список для спиннера с опцией "Все игроки"
                    List<Player> spinnerItems = new ArrayList<>();

                    // Создаем специального игрока для "всех игроков"
                    Player allPlayersItem = new Player();
                    allPlayersItem.setId(-1);
                    allPlayersItem.setFullName("Все игроки");
                    spinnerItems.add(allPlayersItem);

                    spinnerItems.addAll(players);

                    ArrayAdapter<Player> adapter = new ArrayAdapter<>(
                            RecordsActivity.this,
                            android.R.layout.simple_spinner_item,
                            spinnerItems
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPlayers.setAdapter(adapter);

                    // Обработчик выбора игрока
                    spinnerPlayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Player player = (Player) parent.getItemAtPosition(position);
                            if (player.getId() == -1) {
                                // Выбраны все игроки
                                selectedPlayer = null;
                                loadAllRecords();
                            } else {
                                // Выбран конкретный игрок
                                selectedPlayer = player;
                                loadPlayerRecords(player.getId());
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                });
            }
        });
    }

    private void setupClearButton() {
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearConfirmationDialog();
            }
        });
    }

    private void showClearConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Очистка рекордов")
                .setMessage("Вы уверены, что хотите очистить всю таблицу рекордов? Это действие нельзя отменить.")
                .setPositiveButton("Очистить", (dialog, which) -> clearAllRecords())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void clearAllRecords() {
        gameManager.clearAllScores(new GameManager.ClearCallback() {
            @Override
            public void onClearCompleted() {
                runOnUiThread(() -> {
                    // После очистки загружаем записи (будет пустой список)
                    if (selectedPlayer != null) {
                        loadPlayerRecords(selectedPlayer.getId());
                    } else {
                        loadAllRecords();
                    }

                    // Показываем сообщение об успешной очистке
                    tvEmpty.setText("Все рекорды очищены!\nСыграйте в игру чтобы установить новые рекорды.");
                    listViewRecords.setVisibility(ListView.GONE);
                    tvEmpty.setVisibility(TextView.VISIBLE);
                });
            }
        });
    }

    private void loadAllRecords() {
=======
        loadRecords();
    }

    private void loadRecords() {
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9
        gameManager.getTopScores(50, new GameManager.ScoresCallback() {
            @Override
            public void onScoresLoaded(List<ScoreRecord> scores) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
<<<<<<< HEAD
                        updateRecordsList(scores);
=======
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
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9
                    }
                });
            }
        });
    }

<<<<<<< HEAD
    private void loadPlayerRecords(int playerId) {
        gameManager.getPlayerScores(playerId, new GameManager.ScoresCallback() {
            @Override
            public void onScoresLoaded(List<ScoreRecord> scores) {
                runOnUiThread(() -> updateRecordsList(scores));
            }
        });
    }

    private void updateRecordsList(List<ScoreRecord> scores) {
        if (scores.isEmpty()) {
            if (selectedPlayer != null) {
                tvEmpty.setText("У игрока " + selectedPlayer.getFullName() + " пока нет рекордов!");
            } else {
                tvEmpty.setText("Пока нет рекордов!\nСыграйте в игру чтобы установить первый рекорд.");
            }
            listViewRecords.setVisibility(ListView.GONE);
            tvEmpty.setVisibility(TextView.VISIBLE);
        } else {
            RecordsAdapter adapter = new RecordsAdapter(RecordsActivity.this, scores);
            listViewRecords.setAdapter(adapter);
            listViewRecords.setVisibility(ListView.VISIBLE);
            tvEmpty.setVisibility(TextView.GONE);
        }
    }

=======
>>>>>>> a1eefc9a6880679cf72d0dc0c533ada8b233deb9
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}