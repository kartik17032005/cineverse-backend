package com.example.cineverse_movie_app_two.Models;

import java.io.Serializable;

public class Movie implements Serializable {

    private String id;
    private String trailerKey;
    private String title;
    private String genre;
    private String year;
    private String description;
    private double rating;
    private String posterUrl;
    private String backdropUrl;  // Added for banners and wide images
    private boolean isWishlisted; // For dynamic loading from server
    private String movieUrl;

    public Movie(String id, String title, String genre, String year, String description, double rating, String posterUrl, String backdropUrl, String movieUrl) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.description = description;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.trailerKey = "";
        this.isWishlisted = false;
        this.movieUrl = movieUrl;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getId(){
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public String getYear() {
        return year;
    }

    public String getTrailerKey(){
        return trailerKey;
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }
    
    public String getMovieUrl(){return movieUrl;}

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public boolean isWishlisted(){
        return isWishlisted;
    }

    // Returns the appropriate image URL for banners, fallback to poster if backdrop not available
    public String getBannerImageUrl() {
        return (backdropUrl != null && !backdropUrl.isEmpty()) ? backdropUrl : posterUrl;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    private void setId(String id){
        this.id = id;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setTrailerKey(String trailerKey){
        this.trailerKey = trailerKey;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setMovieUrl(String movieUrl){this.movieUrl = movieUrl;}

    public void setBackdropUrl(String backdropUrl) {
        this.backdropUrl = backdropUrl;
    }

    public void setWishlisted(boolean isWishlisted){
        this.isWishlisted = isWishlisted;
    }
}
