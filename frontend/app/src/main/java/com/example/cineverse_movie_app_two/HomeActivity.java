package com.example.cineverse_movie_app_two;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.Adapters.DifferentSectionBanners;
import com.example.cineverse_movie_app_two.Adapters.SearchSuggestionsAdapter;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    // UI Elements
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton hamburger;
    private ImageView profileIcon;
    private ViewPager2 bannerViewPager;
    private LinearLayout customBannerDots;

    private RecyclerView trendingRecyclerView,
            bollywoodRecyclerView, hollywoodRecyclerView, tollywoodRecyclerView,
            koreanRecyclerView, japaneseRecyclerView;

    private EditText searchEditText;
    private ImageView clearSearch;
    private RecyclerView searchResultsRecyclerView;

    // Network and Data
    private RequestQueue requestQueue;
    private final Handler handler = new Handler();
    private Runnable bannerRunnable;

    private DifferentSectionBanners bannerAdapter;
    private final List<Movie> bannerMovies = new ArrayList<>();

    private final List<Movie> suggestionList = new ArrayList<>();
    private SearchSuggestionsAdapter suggestionsAdapter;
    private static final String SUGGEST_TAG = "tmdb_suggest";

    // Constants
    private static final String API_KEY = "c5a98bc7dcaea02fe17be1b596830b2e";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String BASE_DISCOVER = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY;
    private static final String TMDB_TRENDING = "https://api.themoviedb.org/3/trending/movie/day?api_key=" + API_KEY;

    private static final String TMDB_BOLLYWOOD = BASE_DISCOVER + "&with_original_language=hi";
    private static final String TMDB_HOLLYWOOD = BASE_DISCOVER + "&with_original_language=en";
    private static final String TMDB_TOLLYWOOD = BASE_DISCOVER + "&with_original_language=te";
    private static final String TMDB_KOREAN   = BASE_DISCOVER + "&with_original_language=ko";
    private static final String TMDB_JAPANESE = BASE_DISCOVER + "&with_original_language=ja";

    private int maxVisibleDots = 5;
    private static final long SEARCH_DEBOUNCE_MS = 300L;
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets i = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(i.left, i.top, i.right, i.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);

        initViews();
        initSearchUI();
        loadUserProfileImage();

        profileIcon.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        fetchFastBanners();
        setupTrendingRecycler();
        setupSectionRecycler(TMDB_BOLLYWOOD, bollywoodRecyclerView, "Bollywood");
        setupSectionRecycler(TMDB_HOLLYWOOD, hollywoodRecyclerView, "Hollywood");
        setupSectionRecycler(TMDB_TOLLYWOOD, tollywoodRecyclerView, "Tollywood");
        setupSectionRecycler(TMDB_KOREAN, koreanRecyclerView, "Korean");
        setupSectionRecycler(TMDB_JAPANESE, japaneseRecyclerView, "Japanese");

        hamburger.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        hamburger = findViewById(R.id.menuIcon);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        customBannerDots = findViewById(R.id.customBannerDots);
        profileIcon = findViewById(R.id.profileIcon);

        trendingRecyclerView = findViewById(R.id.trendingRecyclerView);
        bollywoodRecyclerView = findViewById(R.id.bollywoodRecyclerView);
        hollywoodRecyclerView = findViewById(R.id.hollywoodRecyclerView);
        tollywoodRecyclerView = findViewById(R.id.tollywoodRecyclerView);
        koreanRecyclerView = findViewById(R.id.koreanRecyclerView);
        japaneseRecyclerView = findViewById(R.id.japaneseRecyclerView);

        searchEditText = findViewById(R.id.searchEditText);
        clearSearch = findViewById(R.id.clearSearch);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        suggestionsAdapter = new SearchSuggestionsAdapter(suggestionList, movie -> {
            hideKeyboard();
            collapseSuggestions();
            Intent intent = new Intent(HomeActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie); // Pass the whole movie object!
            startActivity(intent);
        });

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchResultsRecyclerView.setAdapter(suggestionsAdapter);
        searchResultsRecyclerView.setVisibility(View.GONE);
    }

    private void initSearchUI() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                clearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    if (query.isEmpty()) {
                        collapseSuggestions();
                    } else {
                        queryTmdbSuggestions(query);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        searchEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) queryTmdbSuggestions(query);
                return true;
            }
            return false;
        });

        clearSearch.setOnClickListener(v -> {
            searchEditText.setText("");
            collapseSuggestions();
        });

        findViewById(R.id.main).setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (searchResultsRecyclerView.getVisibility() == View.VISIBLE) collapseSuggestions();
        });
    }

    private void collapseSuggestions() {
        suggestionList.clear();
        suggestionsAdapter.notifyDataSetChanged();
        searchResultsRecyclerView.setVisibility(View.GONE);
    }

    private void expandSuggestions() {
        searchResultsRecyclerView.setVisibility(suggestionList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void loadUserProfileImage() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String profileImageUrl = prefs.getString("profile_image_url", "");
        if (!profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load("http://10.26.135.247:8080" + profileImageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(profileIcon);
        } else {
            profileIcon.setImageResource(R.drawable.profile);
        }
    }

    private void fetchFastBanners() {
        String[] bases = {TMDB_BOLLYWOOD, TMDB_TOLLYWOOD, TMDB_HOLLYWOOD};
        for (String baseUrl : bases) {
            String url = baseUrl + "&page=1";
            requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                    res -> {
                        try {
                            JSONArray results = res.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject m = results.getJSONObject(i);
                                String detailsUrl = "https://api.themoviedb.org/3/movie/" + m.getString("id") +
                                        "?api_key=" + API_KEY + "&append_to_response=videos";
                                requestQueue.add(new JsonObjectRequest(Request.Method.GET, detailsUrl, null,
                                        detailsRes -> {
                                            try {
                                                JSONArray videos = detailsRes.getJSONObject("videos").getJSONArray("results");
                                                String trailerKey = null;
                                                for (int j = 0; j < videos.length(); j++) {
                                                    JSONObject v = videos.getJSONObject(j);
                                                    if ("YouTube".equalsIgnoreCase(v.getString("site")) &&
                                                            v.getString("type").toLowerCase().contains("trailer")) {
                                                        trailerKey = v.getString("key");
                                                        break;
                                                    }
                                                }
                                                if (trailerKey != null) {
                                                    Movie movie = parseMovie(detailsRes, "Mixed");
                                                    movie.setTrailerKey(trailerKey);
                                                    bannerMovies.add(movie);
                                                    updateBannerUI();
                                                }
                                            } catch (Exception ignored) {}
                                        },
                                        err -> Log.e("BannerDetails", err.toString())));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, err -> Log.e("BannerListFetch", err.toString())));
        }
    }

    private void updateBannerUI() {
        if (bannerAdapter == null) {
            bannerAdapter = new DifferentSectionBanners(this, bannerMovies);
            bannerViewPager.setAdapter(bannerAdapter);
            initCustomDots(bannerMovies.size());
            setupBannerAutoScroll();
        } else {
            bannerAdapter.notifyDataSetChanged();
            initCustomDots(bannerMovies.size());
        }
    }

    private void initCustomDots(int total) {
        customBannerDots.removeAllViews();
        for (int i = 0; i < total; i++) {
            View dot = new View(this);
            int size = 16;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.dot_drawable);
            dot.setAlpha(0.3f);
            customBannerDots.addView(dot);
        }
        updateCustomDots(0, total);

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                updateCustomDots(position, total);
            }
        });
    }

    private void updateCustomDots(int current, int total) {
        int half = maxVisibleDots / 2;
        int start = Math.max(0, current - half);
        if (start + maxVisibleDots > total) start = Math.max(0, total - maxVisibleDots);
        for (int i = 0; i < total; i++) {
            View dot = customBannerDots.getChildAt(i);
            if (i >= start && i < start + maxVisibleDots) {
                dot.setVisibility(View.VISIBLE);
                dot.setAlpha(i == current ? 1f : 0.5f);
            } else {
                dot.setVisibility(View.GONE);
            }
        }
    }

    private void setupBannerAutoScroll() {
        if (bannerRunnable != null) handler.removeCallbacks(bannerRunnable);
        bannerRunnable = () -> {
            if (bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
                int current = bannerViewPager.getCurrentItem();
                int next = (current + 1) % bannerAdapter.getItemCount();
                bannerViewPager.setCurrentItem(next, true);
                handler.postDelayed(bannerRunnable, 5000);
            }
        };
        handler.postDelayed(bannerRunnable, 5000);
    }

    // In your parseMovie method, add reading movieUrl if available
    private Movie parseMovie(JSONObject obj, String genreLabel) throws JSONException {
        JSONArray genreIdsArray = obj.optJSONArray("genre_ids");
        Map<Integer, String> genreMap = getGenreMap();

        List<String> genreNames = new ArrayList<>();
        if (genreIdsArray != null) {
            for (int i = 0; i < genreIdsArray.length(); i++) {
                int genreId = genreIdsArray.optInt(i);
                if (genreMap.containsKey(genreId)) {
                    genreNames.add(genreMap.get(genreId));
                }
            }
        }
        String genreString = genreNames.isEmpty() ? genreLabel : TextUtils.join(", ", genreNames);

        String posterUrl = IMAGE_BASE_URL + obj.optString("poster_path", "");
        String backdropUrl = "";
        if (obj.has("backdrop_path") && obj.optString("backdrop_path") != null && !obj.optString("backdrop_path").isEmpty()) {
            backdropUrl = IMAGE_BASE_URL + obj.optString("backdrop_path");
        }
        String title = obj.optString("title", obj.optString("name", ""));

        // NEW: Get movieUrl from your API response (adjust the field name accordingly)
        String movieUrl = "";
        if (obj.has("stream_url") && !obj.isNull("stream_url")) {
            movieUrl = obj.getString("stream_url");
        }

        Movie movie = new Movie(
                obj.getString("id"),
                title,
                genreString,
                obj.optString("release_date", obj.optString("first_air_date", "N/A")).split("-")[0],
                obj.optString("overview", ""),
                obj.optDouble("vote_average", 0),
                posterUrl,
                backdropUrl,
                movieUrl
        );
        movie.setMovieUrl(movieUrl);  // Set the new movieUrl field
        return movie;
    }


    private void setupTrendingRecycler() {
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trendingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        List<Movie> trendingList = new ArrayList<>();
        DifferentSectionBanners adapter = new DifferentSectionBanners(this, trendingList);
        trendingRecyclerView.setAdapter(adapter);

        requestQueue.add(new JsonObjectRequest(Request.Method.GET, TMDB_TRENDING, null,
                res -> {
                    try {
                        JSONArray results = res.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject m = results.getJSONObject(i);
                            Movie movie = parseMovie(m, "Trending");
                            trendingList.add(movie);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) { e.printStackTrace(); }
                }, err -> Log.e("Trending", err.toString())));
    }

    private void setupSectionRecycler(String baseUrl, RecyclerView view, String label) {
        view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        view.setItemAnimator(new DefaultItemAnimator());
        List<Movie> list = new ArrayList<>();
        DifferentSectionBanners adapter = new DifferentSectionBanners(this, list);
        view.setAdapter(adapter);
        fetchMoviesForList(baseUrl, list, adapter, label);
    }

    private void fetchMoviesForList(String baseUrl, List<Movie> list, RecyclerView.Adapter<?> adapter, String genreLabel) {
        boolean requireTrailer = genreLabel.equalsIgnoreCase("Bollywood") ||
                genreLabel.equalsIgnoreCase("Tollywood") ||
                genreLabel.equalsIgnoreCase("Hollywood");

        String url = baseUrl + "&page=1";
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                res -> {
                    try {
                        JSONArray results = res.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject m = results.getJSONObject(i);
                            String detailsUrl = "https://api.themoviedb.org/3/movie/" + m.getString("id") +
                                    "?api_key=" + API_KEY + "&append_to_response=videos";
                            requestQueue.add(new JsonObjectRequest(Request.Method.GET, detailsUrl, null,
                                    detailsRes -> {
                                        try {
                                            JSONArray videos = detailsRes.getJSONObject("videos").getJSONArray("results");
                                            String trailerKey = null;
                                            for (int j = 0; j < videos.length(); j++) {
                                                JSONObject v = videos.getJSONObject(j);
                                                if ("YouTube".equalsIgnoreCase(v.getString("site")) &&
                                                        v.getString("type").toLowerCase().contains("trailer")) {
                                                    trailerKey = v.getString("key");
                                                    break;
                                                }
                                            }
                                            if (requireTrailer) {
                                                if (trailerKey != null) {
                                                    Movie movie = parseMovie(detailsRes, genreLabel);
                                                    movie.setTrailerKey(trailerKey);
                                                    list.add(movie);
                                                }
                                            } else {
                                                Movie movie = parseMovie(detailsRes, genreLabel);
                                                if (trailerKey != null) movie.setTrailerKey(trailerKey);
                                                list.add(movie);
                                            }
                                            adapter.notifyDataSetChanged();
                                        } catch (Exception ignored) {}
                                    }, err -> Log.e("SectionDetails", err.toString())));
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                }, err -> Log.e("SectionList", err.toString())));
    }

    private void queryTmdbSuggestions(String query) {
        requestQueue.cancelAll(SUGGEST_TAG);

        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
            String url = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY +
                    "&query=" + encoded + "&include_adult=false";

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                    res -> {
                        suggestionList.clear();
                        try {
                            JSONArray results = res.getJSONArray("results");
                            for (int i = 0; i < results.length() && i < 20; i++) {
                                JSONObject m = results.getJSONObject(i);
                                Movie movie = parseMovie(m, "Movie");
                                suggestionList.add(movie);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        suggestionsAdapter.notifyDataSetChanged();
                        expandSuggestions();
                    },
                    err -> {
                        Log.e("TMDB_SUGGEST", err.toString());
                        collapseSuggestions();
                    }
            );
            req.setTag(SUGGEST_TAG);
            requestQueue.add(req);

        } catch (Exception e) {
            Log.e("TMDB_SUGGEST", "Encoding error: " + e.getMessage());
        }
    }

    // HANDLE DRAWER MENU ITEM CLICKS HERE
    private boolean handleNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already home, do nothing
            return true;
        } else if (id == R.id.nav_category) {
            startActivity(new Intent(this, CategoriesActivity.class));
        } else if (id == R.id.nav_watchlist) {
            startActivity(new Intent(this, WishlistActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            // Example logout logic (implement your own)
            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_reels) {
            startActivity(new Intent(this, ReelsActivity.class));
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerRunnable != null) handler.removeCallbacks(bannerRunnable);
        if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
        if (requestQueue != null) requestQueue.cancelAll(SUGGEST_TAG);
    }

    // Genre map utility
    private Map<Integer, String> getGenreMap() {
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
}
