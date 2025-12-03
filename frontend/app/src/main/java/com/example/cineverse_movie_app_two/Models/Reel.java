package com.example.cineverse_movie_app_two.Models;

public class Reel {
    private final String movieTitle;
    private final String genre;
    private final String videoKey;  // YouTube Video ID
    private final String videoType; // Trailer, Clip, Teaser
    private final String thumbnailUrl;
    private final String description;

    public Reel(String movieTitle, String genre, String videoKey, String videoType,
                String thumbnailUrl, String description) {
        this.movieTitle = movieTitle;
        this.genre = genre;
        this.videoKey = videoKey;
        this.videoType = videoType;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }

    public String getMovieTitle() { return movieTitle; }
    public String getGenre() { return genre; }
    public String getVideoKey() { return videoKey; }
    public String getVideoType() { return videoType; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getDescription() { return description; }
}
