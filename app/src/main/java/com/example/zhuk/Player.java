package com.example.zhuk;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "players")
public class Player {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String fullName;
    private String gender;
    private String course;
    private int difficultyLevel;
    private long birthDate;
    private String zodiacSign;

    // Конструктор по умолчанию
    public Player() {
    }

    // Конструктор для создания нового игрока
    public Player(String fullName, String gender, String course, int difficultyLevel, long birthDate, String zodiacSign) {
        this.fullName = fullName;
        this.gender = gender;
        this.course = course;
        this.difficultyLevel = difficultyLevel;
        this.birthDate = birthDate;
        this.zodiacSign = zodiacSign;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
        return fullName + " (" + zodiacSign + ")";
    }
}