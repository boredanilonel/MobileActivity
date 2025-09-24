package com.example.zhuk;

public class Author {
    private String name;
    private int photoResId;

    public Author(String name, int photoResId) {
        this.name = name;
        this.photoResId = photoResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhotoResId() {
        return photoResId;
    }

    public void setPhotoResId(int photoResId) {
        this.photoResId = photoResId;
    }
}
