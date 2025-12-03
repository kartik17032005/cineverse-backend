package com.example.cineverse_movie_app_two;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cineverse_movie_app_two.Adapters.MovieGridAdapter;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.Utils.GridSpacingItemDecoration;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView wishlistRecyclerView;
    private TextView wishlistTitle, wishlistEmptyMessage;
    private static final String API_KEY = "c5a98bc7dcaea02fe17be1b596830b2e";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private Set<String> wishlistSet;
    private List<Movie> wishlistMovies;
    private int moviesFetchedCount = 0; // Tracks successful requests

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        wishlistRecyclerView = findViewById(R.id.wishlistRecyclerView);
        wishlistTitle = findViewById(R.id.wishlistHeader);
        wishlistEmptyMessage = findViewById(R.id.wishlistEmptyMessage);

        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int spacingPx = getResources().getDimensionPixelSize(R.dimen.category_grid_spacing);
        wishlistRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingPx, true));

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        wishlistSet = prefs.getStringSet("wishlist", new HashSet<>());

        if (wishlistSet.isEmpty()) {
            wishlistEmptyMessage.setVisibility(View.VISIBLE);
            wishlistTitle.setText("Your Wishlist is Empty");
        } else {
            wishlistEmptyMessage.setVisibility(View.GONE);
            wishlistMovies = new ArrayList<>();
            fetchMoviesByIds(new ArrayList<>(wishlistSet));
        }
    }

    private void fetchMoviesByIds(List<String> movieIds) {
        RequestQueue queue = Volley.newRequestQueue(this);
        for (String id : movieIds) {
            String url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + API_KEY;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            String movieId = response.optString("id");
                            String title = response.optString("title");
                            String year = "";
                            if (response.has("release_date") && !response.isNull("release_date")) {
                                String releaseDate = response.getString("release_date");
                                if (!releaseDate.isEmpty()) {
                                    String[] parts = releaseDate.split("-");
                                    if (parts.length > 0) year = parts[0];
                                }
                            }
                            String description = response.optString("overview", "");
                            double rating = response.optDouble("vote_average", 0.0);
                            String posterPath = response.optString("poster_path", "");
                            String backdropPath = response.optString("backdrop_path", "");
                            String fullPosterUrl = (posterPath != null && !posterPath.equals("null") && !posterPath.isEmpty())
                                    ? IMAGE_BASE_URL + posterPath : "";
                            String fullBackdropUrl = (backdropPath != null && !backdropPath.equals("null") && !backdropPath.isEmpty())
                                    ? IMAGE_BASE_URL + backdropPath : "";

                            Movie movie = new Movie(movieId, title, "Wishlist", year, description, rating, fullPosterUrl, fullBackdropUrl, "");
                            movie.setWishlisted(true);

                            wishlistMovies.add(movie);

                            // Increment the counter and check if all movies have been fetched
                            moviesFetchedCount++;
                            if (moviesFetchedCount == movieIds.size()) {
                                // All movies are fetched. Update the UI on the main thread.
                                runOnUiThread(() -> {
                                    MovieGridAdapter adapter = new MovieGridAdapter(this, wishlistMovies);
                                    wishlistRecyclerView.setAdapter(adapter);
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        // Handle the case where a movie fails to load
                        moviesFetchedCount++;
                        if (moviesFetchedCount == movieIds.size()) {
                            // Even if some failed, we still need to show the fetched ones.
                            runOnUiThread(() -> {
                                MovieGridAdapter adapter = new MovieGridAdapter(this, wishlistMovies);
                                wishlistRecyclerView.setAdapter(adapter);
                                Toast.makeText(this, "Failed to load some movies", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });

            queue.add(request);
        }
    }
}