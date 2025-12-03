package com.example.cineverse_movie_app_two.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {

    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults(){
        return results;
    }
}
