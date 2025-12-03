package com.example.cineverse_movie_app_two.Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.cineverse_movie_app_two.Models.Category;
import com.example.cineverse_movie_app_two.Models.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieApiService {

    private static final String API_KEY = "c5a98bc7dcaea02fe17be1b596830b2e"; // 🔑 TMDb API Key
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    // Genre mapping utility
    private static Map<Integer, String> getGenreMap() {
        Map<Integer, String> genreMap = new HashMap<>();
        genreMap.put(28, "Action");
        genreMap.put(12, "Adventure");
        genreMap.put(16, "Animation");
        genreMap.put(35, "Comedy");
        genreMap.put(80, "Crime");
        genreMap.put(99, "Documentary");
        genreMap.put(18, "Drama");
        genreMap.put(10751, "Family");
        genreMap.put(14, "Fantasy");
        genreMap.put(36, "History");
        genreMap.put(27, "Horror");
        genreMap.put(10402, "Music");
        genreMap.put(9648, "Mystery");
        genreMap.put(10749, "Romance");
        genreMap.put(878, "Science Fiction");
        genreMap.put(10770, "TV Movie");
        genreMap.put(53, "Thriller");
        genreMap.put(10752, "War");
        genreMap.put(37, "Western");
        return genreMap;
    }

    // ========================= MOVIE FETCHING =========================
    public interface MovieResponseListener {
        void onResponse(List<Movie> movies);
        void onError(String message);
    }

    public void fetchMovies(Context context, final MovieResponseListener listener) {
        String url = BASE_URL + "/movie/popular?api_key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Movie> movieList = new ArrayList<>();
                        JSONArray moviesArray = response.getJSONArray("results");
                        Map<Integer, String> genreMap = getGenreMap();

                        for (int i = 0; i < moviesArray.length(); i++) {
                            JSONObject movieObj = moviesArray.getJSONObject(i);

                            String id = movieObj.optString("id", "");
                            String title = movieObj.optString("title", "Untitled");
                            String description = movieObj.optString("overview", "");
                            double rating = movieObj.optDouble("vote_average", 0.0);

                            // Year extraction with null safety
                            String year = "";
                            String releaseDate = movieObj.optString("release_date", "");
                            if (releaseDate != null && !releaseDate.isEmpty()) {
                                String[] parts = releaseDate.split("-");
                                year = parts.length > 0 ? parts[0] : "";
                            }

                            // Genre(s) mapping
                            JSONArray genreIdsArray = movieObj.optJSONArray("genre_ids");
                            List<String> genreNames = new ArrayList<>();
                            if (genreIdsArray != null) {
                                for (int j = 0; j < genreIdsArray.length(); j++) {
                                    int genreId = genreIdsArray.optInt(j);
                                    String genreName = genreMap.get(genreId);
                                    if (genreName != null) genreNames.add(genreName);
                                }
                            }
                            String genreLabel = genreNames.isEmpty() ? "N/A" : android.text.TextUtils.join(", ", genreNames);

                            // Images
                            String posterPath = movieObj.optString("poster_path", "");
                            String fullPosterUrl = (posterPath != null && !posterPath.equals("null") && !posterPath.isEmpty())
                                    ? IMAGE_BASE_URL + posterPath : "";
                            String backdropPath = movieObj.optString("backdrop_path", "");
                            String fullBackdropUrl = (backdropPath != null && !backdropPath.equals("null") && !backdropPath.isEmpty())
                                    ? IMAGE_BASE_URL + backdropPath : "";

                            // NEW: Extract stream URL for playback
                            String movieUrl = "";
                            if (movieObj.has("stream_url") && !movieObj.isNull("stream_url")) {
                                movieUrl = movieObj.optString("stream_url", "");
                            }

                            Movie movie = new Movie(
                                    id, title, genreLabel, year, description, rating,
                                    fullPosterUrl, fullBackdropUrl, movieUrl
                            );
                            movieList.add(movie);
                        }

                        listener.onResponse(movieList);

                    } catch (Exception e) {
                        listener.onError("Parsing Error: " + e.getMessage());
                    }
                },
                error -> listener.onError("Volley Error: " + error.getMessage())
        );

        queue.add(request);
    }

    // ========================= CATEGORY FETCHING =========================
    public interface CategoryResponseListener {
        void onResponse(List<Category> categories);
        void onError(String message);
    }

    public void fetchCategories(Context context, final CategoryResponseListener listener) {
        String url = BASE_URL + "/genre/movie/list?api_key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Category> categoryList = new ArrayList<>();
                        JSONArray genresArray = response.getJSONArray("genres");

                        for (int i = 0; i < genresArray.length(); i++) {
                            JSONObject genreObj = genresArray.getJSONObject(i);
                            int id = genreObj.getInt("id");
                            String name = genreObj.getString("name");

                            String lottieFileName = getCategoryLottieFileName(name);
                            Category category = new Category(id, name, lottieFileName);
                            categoryList.add(category);
                        }

                        listener.onResponse(categoryList);

                    } catch (Exception e) {
                        listener.onError("Parsing Error: " + e.getMessage());
                    }
                },
                error -> listener.onError("Volley Error: " + error.getMessage())
        );

        queue.add(request);
    }

    // ========================= LOTTIE FILE NAME MAPPING =========================
    private String getCategoryLottieFileName(String name) {
        name = name.toLowerCase();

        switch (name) {
            case "action": return "genre_action.json";
            case "drama": return "genre_drama.json";
            case "comedy": return "genre_comedy.json";
            case "horror": return "genre_horror.json";
            case "romance": return "genre_romance.json";
            case "animation": return "genre_animation.json";
            case "fantasy": return "genre_fantasy.json";
            case "science fiction":
            case "sci-fi": return "genre_scifi.json";
            case "thriller": return "genre_thriller.json";
            case "crime": return "genre_crime.json";
            case "documentary": return "genre_documentary.json";
            case "mystery": return "genre_mystery.json";
            case "adventure": return "genre_adventure.json";
            case "family": return "genre_family.json";
            default: return "genre_placeholder.json"; // fallback
        }
    }
}
