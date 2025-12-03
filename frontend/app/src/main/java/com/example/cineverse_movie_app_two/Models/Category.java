package com.example.cineverse_movie_app_two.Models;

public class Category {
    private int id;
    private String name;
    private String lottieFileName; // NEW: genre_action.json etc.

    public Category(int id, String name, String lottieFileName) {
        this.id = id;
        this.name = name;
        this.lottieFileName = lottieFileName;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLottieFileName() { return lottieFileName; }

    public void setLottieFileName(String lottieFileName) {
        this.lottieFileName = lottieFileName;
    }
}
