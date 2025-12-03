package com.example.cineverse_movie_app_two.Models;

public class TrendingMovie {
    private String title;
    private String poster_path;
    private int id;

    public TrendingMovie(String title, String poster_path, int id) {
        this.title = title;
        this.poster_path = poster_path;
        this.id = id;
    }

    public String getTitle() { return title; }
    public String getPosterPath() { return poster_path; }
    public int getId() { return id; }
}
