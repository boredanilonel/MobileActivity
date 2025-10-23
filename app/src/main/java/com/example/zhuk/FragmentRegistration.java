package com.example.zhuk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentRegistration extends Fragment {

    private EditText etFullName;
    private RadioGroup rgGender;
    private Spinner spCourse;
    private SeekBar sbDifficulty;
    private TextView tvDifficultyValue;
    private CalendarView cvBirthDate;
    private ImageView ivZodiac;
    private Button btnRegister;
    private TextView tvOutput;
    private TextView tvZodiacName;

    private RadioGroup rgDateInputType;
    private LinearLayout llManualDate;
    private EditText etDay;
    private EditText etMonth;
    private EditText etYear;

    private Spinner spExistingPlayers;
    private Button btnSelectExisting;
    private LinearLayout llNewPlayerForm;
    private RadioGroup rgPlayerSelection;
    private TextView tvExistingPlayersLabel;

    private String[] courses;
    private String[] difficultyLevels;

    private Calendar selectedCalendar;

    public FragmentRegistration() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        selectedCalendar = Calendar.getInstance();
        selectedCalendar.add(Calendar.YEAR, -15);
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        initializeViews(view);
        setupSpinner();
        setupSeekBar();
        setupDateInputSwitcher();
        setupCalendar();
        setupManualDateInput();
        setupButton();

        return view;
    }

    private void initializeViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        rgGender = view.findViewById(R.id.rgGender);
        spCourse = view.findViewById(R.id.spCourse);
        sbDifficulty = view.findViewById(R.id.sbDifficulty);
        tvDifficultyValue = view.findViewById(R.id.tvDifficultyValue);
        cvBirthDate = view.findViewById(R.id.cvBirthDate);
        ivZodiac = view.findViewById(R.id.ivZodiac);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvOutput = view.findViewById(R.id.tvOutput);
        tvZodiacName = view.findViewById(R.id.tvZodiacName);

        rgDateInputType = view.findViewById(R.id.rgDateInputType);
        llManualDate = view.findViewById(R.id.llManualDate);
        etDay = view.findViewById(R.id.etDay);
        etMonth = view.findViewById(R.id.etMonth);
        etYear = view.findViewById(R.id.etYear);

        courses = getResources().getStringArray(R.array.courses);
        difficultyLevels = getResources().getStringArray(R.array.difficulty_levels);
    }

    private void setupDateInputSwitcher() {
        rgDateInputType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbCalendar) {
                    cvBirthDate.setVisibility(View.VISIBLE);
                    llManualDate.setVisibility(View.GONE);

                    cvBirthDate.setDate(selectedCalendar.getTimeInMillis());

                } else if (checkedId == R.id.rbManual) {
                    cvBirthDate.setVisibility(View.GONE);
                    llManualDate.setVisibility(View.VISIBLE);

                    etDay.setText(String.valueOf(selectedCalendar.get(Calendar.DAY_OF_MONTH)));
                    etMonth.setText(String.valueOf(selectedCalendar.get(Calendar.MONTH) + 1));
                    etYear.setText(String.valueOf(selectedCalendar.get(Calendar.YEAR)));
                }

                updateZodiacSign(selectedCalendar);
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
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

    private void setupCalendar() {
        cvBirthDate.setDate(selectedCalendar.getTimeInMillis());
        updateZodiacSign(selectedCalendar);

        cvBirthDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, month);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                selectedCalendar.set(Calendar.MINUTE, 0);
                selectedCalendar.set(Calendar.SECOND, 0);
                selectedCalendar.set(Calendar.MILLISECOND, 0);

                updateZodiacSign(selectedCalendar);
            }
        });
    }

    private void setupButton() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPlayer();
            }
        });
    }

    private Calendar getSelectedDate() {
        Calendar calendar = Calendar.getInstance();

        if (rgDateInputType.getCheckedRadioButtonId() == R.id.rbCalendar) {
            calendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
        } else {
            try {
                int day = Integer.parseInt(etDay.getText().toString());
                int month = Integer.parseInt(etMonth.getText().toString()) - 1;
                int year = Integer.parseInt(etYear.getText().toString());

                calendar.set(year, month, day);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                selectedCalendar.setTimeInMillis(calendar.getTimeInMillis());

            } catch (NumberFormatException e) {
                calendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
            }
        }

        return calendar;
    }

    private String calculateZodiacSign(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "Овен";
        if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "Телец";
        if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) return "Близнецы";
        if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) return "Рак";
        if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Лев";
        if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Дева";
        if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) return "Весы";
        if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) return "Скорпион";
        if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) return "Стрелец";
        if ((month == 12 && day >= 22) || (month == 1 && day <= 19)) return "Козерог";
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "Водолей";
        if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "Рыбы";

        return "Неизвестно";
    }

    private void updateZodiacSign(Calendar calendar) {
        String zodiacSign = calculateZodiacSign(calendar);
        setZodiacImage(zodiacSign);
        tvZodiacName.setText(zodiacSign);
    }

    private void setZodiacImage(String zodiacSign) {
        int resourceId;
        switch (zodiacSign) {
            case "Овен": resourceId = R.drawable.aries; break;
            case "Телец": resourceId = R.drawable.taurus; break;
            case "Близнецы": resourceId = R.drawable.gemini; break;
            case "Рак": resourceId = R.drawable.cancer; break;
            case "Лев": resourceId = R.drawable.leo; break;
            case "Дева": resourceId = R.drawable.virgo; break;
            case "Весы": resourceId = R.drawable.libra; break;
            case "Скорпион": resourceId = R.drawable.scorpio; break;
            case "Стрелец": resourceId = R.drawable.sagittarius; break;
            case "Козерог": resourceId = R.drawable.capricorn; break;
            case "Водолей": resourceId = R.drawable.aquarius; break;
            case "Рыбы": resourceId = R.drawable.pisces; break;
            default: resourceId = R.mipmap.ic_launcher;
        }
        ivZodiac.setImageResource(resourceId);
    }
    private void registerPlayer() {
        String fullName = etFullName.getText().toString().trim();

        String gender = "";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rbMale) {
            gender = "Мужской";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Женский";
        }

        String course = spCourse.getSelectedItem().toString();
        int difficultyLevel = sbDifficulty.getProgress();

        Calendar birthCalendar = getSelectedDate();
        long birthDate = birthCalendar.getTimeInMillis();
        String zodiacSign = calculateZodiacSign(birthCalendar);

        if (fullName.isEmpty()) {
            Toast.makeText(getContext(), "Введите ФИО", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gender.isEmpty()) {
            Toast.makeText(getContext(), "Выберите пол", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgDateInputType.getCheckedRadioButtonId() == R.id.rbManual) {
            if (etDay.getText().toString().isEmpty() ||
                    etMonth.getText().toString().isEmpty() ||
                    etYear.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Заполните дату рождения", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Player player = new Player(fullName, gender, course, difficultyLevel, birthDate, zodiacSign);

        GameManager gameManager = new GameManager();
        gameManager.initialize(getContext());
        gameManager.savePlayer(player, new GameManager.PlayerSaveCallback() {
            @Override
            public void onPlayerSaved(int playerId) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameManager.setCurrentPlayer(playerId);

                        tvOutput.setText("Игрок зарегистрирован!\n\n" + player.toString());
                        updateZodiacSign(birthCalendar);

                        Toast.makeText(getContext(), "Регистрация завершена! ID: " + playerId, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void savePlayerData(Player player) {
        SharedPreferences prefs = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("player_fullName", player.getFullName());
        editor.putString("player_gender", player.getGender());
        editor.putString("player_course", player.getCourse());
        editor.putInt("player_difficulty", player.getDifficultyLevel());
        editor.putLong("player_birthDate", player.getBirthDate());
        editor.putString("player_zodiac", player.getZodiacSign());

        editor.apply();
    }

    private void setupManualDateInput() {
        TextWatcher dateTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (rgDateInputType.getCheckedRadioButtonId() == R.id.rbManual) {
                    try {
                        int day = Integer.parseInt(etDay.getText().toString());
                        int month = Integer.parseInt(etMonth.getText().toString()) - 1;
                        int year = Integer.parseInt(etYear.getText().toString());

                        selectedCalendar.set(year, month, day);
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        selectedCalendar.set(Calendar.MINUTE, 0);
                        selectedCalendar.set(Calendar.SECOND, 0);
                        selectedCalendar.set(Calendar.MILLISECOND, 0);

                        updateZodiacSign(selectedCalendar);

                    } catch (NumberFormatException e) {
                    }
                }
            }
        };

        etDay.addTextChangedListener(dateTextWatcher);
        etMonth.addTextChangedListener(dateTextWatcher);
        etYear.addTextChangedListener(dateTextWatcher);
    }
}