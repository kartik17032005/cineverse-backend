package com.example.cineverse_movie_app_two;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cineverse_movie_app_two.Models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView detailPoster;
    private ImageButton backButton;
    private ImageButton btnWatchTrailer;
    private TextView detailTitle, detailRating, detailGenre, detailYear, movieDescription;
    private ProgressDialog progressDialog;

    private boolean isExpanded = false;
    private final int MAX_LINES = 3;

    private static final String API_KEY = "c5a98bc7dcaea02fe17be1b596830b2e";

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS);
        postponeEnterTransition();

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));
        getWindow().setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        detailPoster = findViewById(R.id.detailPoster);
        backButton = findViewById(R.id.backButton);
        btnWatchTrailer = findViewById(R.id.btnWatchTrailer);

        detailTitle = findViewById(R.id.detailTitle);
        detailRating = findViewById(R.id.detailRating);
        detailGenre = findViewById(R.id.detailGenre);
        detailYear = findViewById(R.id.detailYear);
        movieDescription = findViewById(R.id.movieDescription);

        movie = (Movie) getIntent().getSerializableExtra("movie");

        if (movie != null) {
            detailTitle.setText(movie.getTitle());
            detailGenre.setText(movie.getGenre());
            detailYear.setText(String.valueOf(movie.getYear()));
            detailRating.setText("⭐ " + movie.getRating() + "/10");

            setupExpandableDescription(movie.getDescription());

            Glide.with(this)
                    .load(movie.getPosterUrl())
                    .dontTransform()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            startPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            startPostponedEnterTransition();

                            detailPoster.setScaleX(1.1f);
                            detailPoster.setScaleY(1.1f);
                            detailPoster.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(600)
                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                    .start();

                            return false;
                        }
                    })
                    .into(detailPoster);

            btnWatchTrailer.setEnabled(false);
            btnWatchTrailer.setAlpha(0.5f);
            fetchTrailer(movie.getId(), movie);
        }

        backButton.setAlpha(0f);
        backButton.setTranslationY(-50f);
        backButton.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(200)
                .setInterpolator(new OvershootInterpolator())
                .start();

        detailTitle.setAlpha(0f);
        detailTitle.setTranslationY(30f);
        detailTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        movieDescription.setAlpha(0f);
        movieDescription.setTranslationY(50f);
        movieDescription.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        backButton.setOnClickListener(v -> supportFinishAfterTransition());

        btnWatchTrailer.setOnClickListener(v -> {
            if (movie.getTrailerKey() != null && !movie.getTrailerKey().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + movie.getTrailerKey()));
                intent.putExtra("force_fullscreen", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(Intent.createChooser(intent, "Open with"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MovieDetailActivity.this, "No app found to open this trailer.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MovieDetailActivity.this, "Trailer not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    private void setupExpandableDescription(String fullText) {
        movieDescription.setText(fullText);
        movieDescription.setMaxLines(MAX_LINES);
        movieDescription.setEllipsize(TextUtils.TruncateAt.END);

        movieDescription.setOnClickListener(v -> toggleDescription(fullText));
    }

    private void toggleDescription(String fullText) {
        movieDescription.setEllipsize(null);

        movieDescription.post(() -> {
            int collapsedHeight = measureTextViewHeight(movieDescription, MAX_LINES);
            int expandedHeight = measureTextViewHeight(movieDescription, Integer.MAX_VALUE);

            int startHeight = isExpanded ? expandedHeight : collapsedHeight;
            int endHeight = isExpanded ? collapsedHeight : expandedHeight;

            ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
            animator.setDuration(350);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                movieDescription.getLayoutParams().height = (int) animation.getAnimatedValue();
                movieDescription.requestLayout();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(ValueAnimator animation) {
                    if (!isExpanded) {
                        movieDescription.setEllipsize(TextUtils.TruncateAt.END);
                        movieDescription.setMaxLines(MAX_LINES);
                    }
                }
            });
            animator.start();

            isExpanded = !isExpanded;
            if (isExpanded) {
                movieDescription.setMaxLines(Integer.MAX_VALUE);
            }
        });
    }

    private int measureTextViewHeight(TextView textView, int maxLines) {
        textView.setMaxLines(maxLines);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textView.getWidth(), View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    private void fetchTrailer(String movieId, Movie movie) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching trailer...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + API_KEY;

        JsonObjectRequest trailerRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONArray results = response.getJSONArray("results");
                        boolean trailerFound = false;

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject video = results.getJSONObject(i);
                            if ("YouTube".equalsIgnoreCase(video.getString("site")) && "Trailer".equalsIgnoreCase(video.getString("type"))) {
                                String trailerKey = video.getString("key");
                                movie.setTrailerKey(trailerKey);
                                btnWatchTrailer.setEnabled(true);
                                btnWatchTrailer.setAlpha(1f);
                                trailerFound = true;

                                btnWatchTrailer.setScaleX(0.85f);
                                btnWatchTrailer.setScaleY(0.85f);
                                btnWatchTrailer.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(500)
                                        .setInterpolator(new OvershootInterpolator())
                                        .start();
                                break;
                            }
                        }

                        if (!trailerFound) {
                            btnWatchTrailer.setEnabled(false);
                            btnWatchTrailer.setAlpha(0.5f);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnWatchTrailer.setEnabled(false);
                        btnWatchTrailer.setAlpha(0.5f);
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    btnWatchTrailer.setEnabled(false);
                    btnWatchTrailer.setAlpha(0.5f);
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(trailerRequest);
    }
}
