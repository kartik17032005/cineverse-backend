package com.example.cineverse_movie_app_two;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cineverse_movie_app_two.Adapters.ReelAdapter;
import com.example.cineverse_movie_app_two.Models.Reel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Shows vertical movie reels using TMDb for metadata and YouTube Data API for the trailer videos.
 */
public class ReelsActivity extends AppCompatActivity {

    // Keys
    private static final String TMDB_API_KEY = "c5a98bc7dcaea02fe17be1b596830b2e";
    private static final String YT_API_KEY = "AIzaSyANnzFQ7beSAZOVOtRrFyfvXNHG5SG_JwE";

    // API
    private static final String TMDB_BASE = "https://api.themoviedb.org/3";
    private static final String YT_SEARCH_BASE = "https://www.googleapis.com/youtube/v3/search";

    // Categories to pick from
    private static final List<String> CATEGORIES =
            Arrays.asList("popular", "top_rated", "upcoming");

    // Known studio → official YouTube channel IDs
    private static final Map<String, String> OFFICIAL_CHANNELS = new HashMap<>();
    static {
        OFFICIAL_CHANNELS.put("Warner Bros. Pictures", "UCjmJDM5pRKbUlVIzDYYWb6g");
        OFFICIAL_CHANNELS.put("Universal Pictures", "UCq0OueAsdxH6b8nyAspwViw");
        OFFICIAL_CHANNELS.put("20th Century Studios", "UC2-BeLxzUBSs0uSrmzWhJuQ");
        OFFICIAL_CHANNELS.put("Sony Pictures Entertainment", "UCz97F7dMxBNOfGYu3rx8aCw");
        OFFICIAL_CHANNELS.put("Sony Pictures", "UCz97F7dMxBNOfGYu3rx8aCw");
        OFFICIAL_CHANNELS.put("Paramount Pictures", "UCg2e22zT4LVod3T3yYyT_FA");
        OFFICIAL_CHANNELS.put("Marvel Studios", "UCvC4D8onUfXzvjTOM-dBfEA");
        OFFICIAL_CHANNELS.put("Walt Disney Pictures", "UCuaFvcY4MhZY3U43mMt1dYQ");
        OFFICIAL_CHANNELS.put("Pixar", "UCKy1dAqELo0zrOtPkf0eTMw");
        OFFICIAL_CHANNELS.put("Lionsgate Movies", "UCvziJ96kx7SWRZ4UEvT00pg");
        OFFICIAL_CHANNELS.put("Lionsgate", "UCvziJ96kx7SWRZ4UEvT00pg");
        OFFICIAL_CHANNELS.put("Netflix", "UCWOA1ZGywLbqmigxE4Qlvuw");
        OFFICIAL_CHANNELS.put("Netflix Film", "UCi8e0iOVk1fEOogdfu4YgfA");
        OFFICIAL_CHANNELS.put("Legendary Entertainment", "UCnbn8ANUIUQkxLXtUSbG0sQ");
        OFFICIAL_CHANNELS.put("Blumhouse Productions", "UCk9txS5hG3Q5OUjD2ZhD6mw");
        OFFICIAL_CHANNELS.put("A24", "UCpjxkB0ZkTtxTWq1bR6KJdA");
        OFFICIAL_CHANNELS.put("Focus Features", "UCp2CkivS6dtE8c2GQ0_lxBA");
        OFFICIAL_CHANNELS.put("SearchlightPictures", "UCoYyu4L3me2AatkGS-zlR4g");
        OFFICIAL_CHANNELS.put("Apple TV", "UC2SLZtVu0sV_88ZJkW6a3mg");
    }

    // UI / logic
    private ViewPager2 viewPager;
    private ReelAdapter reelAdapter;
    private RequestQueue requestQueue;
    private Random random;
    private int currentPlayingPosition = -1;
    private final Set<String> usedVideoIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        viewPager = findViewById(R.id.viewPagerReels);
        requestQueue = Volley.newRequestQueue(this);
        random = new Random();

        reelAdapter = new ReelAdapter(this);
        viewPager.setAdapter(reelAdapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                reelAdapter.setCurrentPlayingPosition(position);
                playVideoAtPosition(position);
            }
        });

        fetchRandomMoviesBatch();
    }

    /** Pause previous, play next */
    private void playVideoAtPosition(int position) {
        if (position == currentPlayingPosition) return;
        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        if (recyclerView == null) {
            currentPlayingPosition = position;
            return;
        }
        // Pause old
        if (currentPlayingPosition != -1) {
            RecyclerView.ViewHolder oldVH = recyclerView.findViewHolderForAdapterPosition(currentPlayingPosition);
            if (oldVH instanceof ReelAdapter.ReelViewHolder) {
                ReelAdapter.ReelViewHolder oldHolder = (ReelAdapter.ReelViewHolder) oldVH;
                oldHolder.shouldPlayOnReady = false;
                if (oldHolder.youTubePlayer != null) oldHolder.youTubePlayer.pause();
            }
        }
        // Play new
        RecyclerView.ViewHolder newVH = recyclerView.findViewHolderForAdapterPosition(position);
        if (newVH instanceof ReelAdapter.ReelViewHolder) {
            ReelAdapter.ReelViewHolder newHolder = (ReelAdapter.ReelViewHolder) newVH;
            newHolder.shouldPlayOnReady = true;
            if (newHolder.youTubePlayer != null) {
                newHolder.youTubePlayer.play();
            }
        } else {
            recyclerView.postDelayed(() -> {
                RecyclerView.ViewHolder vh2 = recyclerView.findViewHolderForAdapterPosition(position);
                if (vh2 instanceof ReelAdapter.ReelViewHolder) {
                    ReelAdapter.ReelViewHolder delayedHolder = (ReelAdapter.ReelViewHolder) vh2;
                    delayedHolder.shouldPlayOnReady = true;
                    if (delayedHolder.youTubePlayer != null) delayedHolder.youTubePlayer.play();
                }
            }, 300);
        }
        currentPlayingPosition = position;
    }

    /** Random category fetch */
    private void fetchRandomMoviesBatch() {
        String category = CATEGORIES.get(random.nextInt(CATEGORIES.size()));
        String url = TMDB_BASE + "/movie/" + category +
                "?api_key=" + TMDB_API_KEY + "&language=en-US&page=1";

        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray results = response.getJSONArray("results");
                for (int i = 0; i < results.length() && i < 10; i++) {
                    JSONObject movie = results.getJSONObject(i);
                    String title = movie.getString("title");
                    String releaseDate = movie.optString("release_date", "");
                    String year = (!releaseDate.isEmpty() && releaseDate.contains("-")) ?
                            releaseDate.split("-")[0] : "";
                    int movieId = movie.getInt("id");
                    fetchMovieGenresAndCompanies(movieId, title, year);
                }
            } catch (Exception e) {
                Log.e("Reels", "TMDB parse error: " + e.getMessage());
            }
        }, error -> Log.e("Reels", "TMDB request error: " + error.getMessage())));
    }

    /** Get genre + potential official channelId */
    private void fetchMovieGenresAndCompanies(int movieId, String movieTitle, String year) {
        String url = TMDB_BASE + "/movie/" + movieId +
                "?api_key=" + TMDB_API_KEY + "&language=en-US";
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                StringBuilder genres = new StringBuilder();
                JSONArray genreArray = response.getJSONArray("genres");
                for (int j = 0; j < genreArray.length(); j++) {
                    genres.append(genreArray.getJSONObject(j).getString("name"));
                    if (j < genreArray.length() - 1) genres.append(", ");
                }
                String genreText = genres.length() > 0 ? genres.toString() : "Unknown Genre";

                // Check production_companies for official channel
                String channelId = null;
                JSONArray companies = response.getJSONArray("production_companies");
                for (int k = 0; k < companies.length(); k++) {
                    String companyName = companies.getJSONObject(k).getString("name");
                    if (OFFICIAL_CHANNELS.containsKey(companyName)) {
                        channelId = OFFICIAL_CHANNELS.get(companyName);
                        break;
                    }
                }
                searchYouTubeTrailer(movieTitle, genreText, year, channelId);
            } catch (Exception e) {
                Log.e("Reels", "Details parse error: " + e.getMessage());
            }
        }, error -> Log.e("Reels", "Details API error: " + error.getMessage())));
    }

    /** Search official channel first, fallback to filtered general search */
    private void searchYouTubeTrailer(String movieTitle, String genreText, String year, String channelId) {
        if (channelId != null) {
            String officialUrl = buildYouTubeSearchUrl(movieTitle, year, channelId);
            requestQueue.add(makeYouTubeRequest(officialUrl, movieTitle, genreText, year, true));
        } else {
            String searchUrl = buildYouTubeSearchUrl(movieTitle, year, null);
            requestQueue.add(makeYouTubeRequest(searchUrl, movieTitle, genreText, year, false));
        }
    }

    private String buildYouTubeSearchUrl(String movieTitle, String year, String channelId) {
        String query = movieTitle + " " + year + " official trailer";
        StringBuilder urlBuilder = new StringBuilder(YT_SEARCH_BASE)
                .append("?part=snippet")
                .append("&maxResults=5")
                .append("&type=video")
                .append("&videoDuration=short")
                .append("&q=").append(Uri.encode(query))
                .append("&relevanceLanguage=en")
                .append("&key=").append(YT_API_KEY);
        if (channelId != null) {
            urlBuilder.append("&channelId=").append(channelId);
        }
        return urlBuilder.toString();
    }

    /** Handle YouTube API result parsing with strict filtering */
    private JsonObjectRequest makeYouTubeRequest(String url, String movieTitle,
                                                 String genreText, String year, boolean wasOfficialAttempt) {
        return new JsonObjectRequest(Request.Method.GET, url, null, ytResponse -> {
            try {
                JSONArray items = ytResponse.optJSONArray("items");
                if (items == null || items.length() == 0) {
                    // Fallback if official channel empty
                    if (wasOfficialAttempt) {
                        String fallbackUrl = buildYouTubeSearchUrl(movieTitle, year, null);
                        requestQueue.add(makeYouTubeRequest(fallbackUrl, movieTitle, genreText, year, false));
                    }
                    return;
                }
                String yearLower = year.toLowerCase();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject idObj = item.getJSONObject("id");
                    JSONObject snippet = item.getJSONObject("snippet");
                    String snippetTitle = snippet.getString("title").toLowerCase();
                    String videoId = idObj.getString("videoId");

                    // Strict match on movie title + optional year in title
                    if (!snippetTitle.contains(movieTitle.toLowerCase())) continue;
                    if (!year.isEmpty() && !snippetTitle.contains(yearLower)) continue;

                    String thumbUrl = snippet.getJSONObject("thumbnails")
                            .getJSONObject("high").getString("url");
                    String description = snippet.optString("description", "");

                    if (!usedVideoIds.contains(videoId)) {
                        usedVideoIds.add(videoId);
                        reelAdapter.addItem(new Reel(
                                movieTitle,
                                genreText,
                                videoId,
                                "Trailer",
                                thumbUrl,
                                description
                        ));
                    }
                    break;
                }
            } catch (Exception e) {
                Log.e("YT_API", "Parse error: " + e.getMessage());
            }
        }, error -> Log.e("YT_API", "YouTube API error: " + error.getMessage()));
    }
}
