package com.example.cineverse_movie_app_two;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.Adapters.CarouselAdapter;
import com.example.cineverse_movie_app_two.Adapters.MovieGridAdapter;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.Utils.GridSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieListActivity extends AppCompatActivity {

    private MotionLayout rootLayout;
    private RecyclerView movieListRecyclerView, carouselRecyclerView;
    private TextView categoryTitle;
    private int categoryId;
    private String categoryName;

    private static final String API_KEY = "c5a98bc7dcaea02fe17be1b596830b2e";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private SharedPreferences prefs;
    private Set<String> wishlistSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        wishlistSet = prefs.getStringSet("wishlist", new HashSet<>());

        rootLayout = findViewById(R.id.rootLayout);
        movieListRecyclerView = findViewById(R.id.movieListRecyclerView);
        carouselRecyclerView = findViewById(R.id.carouselRecyclerView);
        categoryTitle = findViewById(R.id.categoryTitle);
        ImageButton backButton = findViewById(R.id.backButton);

        movieListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int spacingPx = getResources().getDimensionPixelSize(R.dimen.category_grid_spacing);
        movieListRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingPx, true));

        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        categoryId = getIntent().getIntExtra("categoryId", 0);
        categoryName = getIntent().getStringExtra("categoryName");
        if (categoryName != null) {
            categoryTitle.setText(categoryName);
        }

        if (categoryId != 0) {
            fetchMoviesByGenre(categoryId);
        } else {
            Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show();
        }

        backButton.setOnClickListener(v -> onBackPressed());

        rootLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {}

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {}

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {}

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {}
        });
    }

    private void fetchMoviesByGenre(int genreId) {
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY
                + "&with_genres=" + genreId + "&page=1";

        List<Movie> movieList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject firstMovie = results.getJSONObject(0);
                            String heroBackdropPath = firstMovie.optString("backdrop_path", "");
                            String fullBackdropUrl = (heroBackdropPath != null && !heroBackdropPath.equals("null") && !heroBackdropPath.isEmpty())
                                    ? IMAGE_BASE_URL + heroBackdropPath : "";


                        }

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject movieJson = results.getJSONObject(i);

                            String id = movieJson.optString("id");
                            String title = movieJson.optString("title");
                            String year = "";
                            if (movieJson.has("release_date") && !movieJson.isNull("release_date")) {
                                String releaseDate = movieJson.getString("release_date");
                                if (!releaseDate.isEmpty()) {
                                    String[] parts = releaseDate.split("-");
                                    if (parts.length > 0) year = parts[0];
                                }
                            }
                            String description = movieJson.optString("overview", "");
                            double rating = movieJson.optDouble("vote_average", 0.0);
                            String posterPath = movieJson.optString("poster_path", "");
                            String backdropPath = movieJson.optString("backdrop_path", "");
                            String fullPosterUrl = (posterPath != null && !posterPath.equals("null") && !posterPath.isEmpty())
                                    ? IMAGE_BASE_URL + posterPath : "";
                            String fullBackdropUrl = (backdropPath != null && !backdropPath.equals("null") && !backdropPath.isEmpty())
                                    ? IMAGE_BASE_URL + backdropPath : "";

                            String movieUrl = "";
                            if (movieJson.has("stream_url") && !movieJson.isNull("stream_url")) {
                                movieUrl = movieJson.getString("stream_url");
                            }

                            Movie movie = new Movie(id, title, categoryName, year, description, rating, fullPosterUrl, fullBackdropUrl, movieUrl);

                            movie.setWishlisted(wishlistSet.contains(id));
                            movieList.add(movie);
                        }

                        if (!movieList.isEmpty()) {
                            List<Movie> featured = movieList.subList(0, Math.min(movieList.size(), 6));
                            carouselRecyclerView.setAdapter(new CarouselAdapter(MovieListActivity.this, featured));

                            movieListRecyclerView.setAdapter(new MovieGridAdapter(MovieListActivity.this, movieList));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MovieListActivity.this, "Parsing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(MovieListActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
}
