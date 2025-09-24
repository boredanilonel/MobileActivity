package com.example.zhuk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Player {
    private String fullName;
    private String gender;
    private String course;
    private int difficultyLevel;
    private long birthDate;
    private String zodiacSign;

    public Player() {
    }

    public Player(String fullName, String gender, String course,
                  int difficultyLevel, long birthDate, String zodiacSign) {
        this.fullName = fullName;
        this.gender = gender;
        this.course = course;
        this.difficultyLevel = difficultyLevel;
        this.birthDate = birthDate;
        this.zodiacSign = zodiacSign;
    }

    // Геттеры и сеттеры
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public long getBirthDate() { return birthDate; }
    public void setBirthDate(long birthDate) { this.birthDate = birthDate; }

    public String getZodiacSign() { return zodiacSign; }
    public void setZodiacSign(String zodiacSign) { this.zodiacSign = zodiacSign; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date(birthDate));

        return "ФИО: " + fullName + "\n" +
                "Пол: " + gender + "\n" +
                "Курс: " + course + "\n" +
                "Уровень сложности: " + difficultyLevel + "\n" +
                "Дата рождения: " + formattedDate + "\n" +
                "Знак зодиака: " + zodiacSign;
    }
}
