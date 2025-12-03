package com.example.cineverse_movie_app_two.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.MovieDetailActivity;
import com.example.cineverse_movie_app_two.R;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder> {

    private final Context context;
    private final List<Movie> movies;
    private final SharedPreferences prefs;
    private final Random random = new Random();

    public MovieGridAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
        this.prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        loadWishlistState();
    }

    private void loadWishlistState() {
        Set<String> wishlistSet = prefs.getStringSet("wishlist", new HashSet<>());
        for (Movie movie : movies) {
            movie.setWishlisted(wishlistSet.contains(movie.getId()));
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_movie_tiles, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.titleText.setText(movie.getTitle());
        holder.ratingText.setText(String.valueOf(movie.getRating()));

        // 🎬 Load poster
        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.posterImage);

        // 💖 Update wishlist icon
        updateWishlistIcon(holder.wishlistButton, movie.isWishlisted());

        // 🎯 Movie tile click → open details with teleport animation
        holder.itemView.setOnClickListener(v -> {
            playCardTeleportAnimation(holder, () -> {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("movie", movie);
                context.startActivity(intent);
            });
        });

        // 💖 Wishlist button click → animate + toggle
        holder.wishlistButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            boolean newState = !movie.isWishlisted();
            movie.setWishlisted(newState);

            updateWishlistIcon(holder.wishlistButton, newState);

            if (newState) {
                playExplosiveHeartAnimation(holder);
            } else {
                playHeartBreakAnimation(holder);
            }

            saveWishlistState(movie.getId(), newState);
        });
    }

    private void updateWishlistIcon(ImageButton button, boolean isWishlisted) {
        Drawable drawable = ContextCompat.getDrawable(context,
                isWishlisted ? R.drawable.ic_heart_neon : R.drawable.ic_heart_hologram);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            int color = ContextCompat.getColor(context,
                    isWishlisted ? R.color.red : android.R.color.white);
            DrawableCompat.setTint(drawable, color);
        }
        button.setImageDrawable(drawable);
    }

    private void saveWishlistState(String movieId, boolean isWishlisted) {
        Set<String> wishlistSet = prefs.getStringSet("wishlist", new HashSet<>());
        Set<String> newSet = new HashSet<>(wishlistSet);
        if (isWishlisted) {
            newSet.add(movieId);
        } else {
            newSet.remove(movieId);
        }
        prefs.edit().putStringSet("wishlist", newSet).apply();
    }

    // ⚡ Teleport animation for card click
    private void playCardTeleportAnimation(MovieViewHolder holder, Runnable onComplete) {
        ObjectAnimator teleportScaleX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 1f, 0.8f, 1.1f, 1f);
        ObjectAnimator teleportScaleY = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 1f, 0.8f, 1.1f, 1f);
        ObjectAnimator teleportAlpha = ObjectAnimator.ofFloat(holder.itemView, "alpha", 1f, 0.7f, 1f);

        AnimatorSet teleportSet = new AnimatorSet();
        teleportSet.playTogether(teleportScaleX, teleportScaleY, teleportAlpha);
        teleportSet.setDuration(600);
        teleportSet.setInterpolator(new OvershootInterpolator(1.5f));
        teleportSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onComplete != null) onComplete.run();
            }
        });
        teleportSet.start();
    }

    // 💥 Explosive animation when adding to wishlist
    private void playExplosiveHeartAnimation(MovieViewHolder holder) {
        AnimatorSet explosionSet = new AnimatorSet();

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(holder.wishlistButton, "scaleX", 1f, 2f, 1.2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(holder.wishlistButton, "scaleY", 1f, 2f, 1.2f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(holder.wishlistButton, "rotation", 0f, 360f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(holder.wishlistButton, "alpha", 1f, 0.3f, 1f);

        explosionSet.playTogether(scaleUpX, scaleUpY, rotate, alpha);
        explosionSet.setDuration(600);
        explosionSet.setInterpolator(new OvershootInterpolator(2f));
        explosionSet.start();
    }

    // 💔 Break animation when removing from wishlist
    private void playHeartBreakAnimation(MovieViewHolder holder) {
        ObjectAnimator shakeX = ObjectAnimator.ofFloat(holder.wishlistButton, "translationX",
                0f, -10f, 10f, -5f, 5f, 0f);
        shakeX.setDuration(500);
        shakeX.setInterpolator(new CycleInterpolator(3));

        ObjectAnimator fade = ObjectAnimator.ofFloat(holder.wishlistButton, "alpha", 1f, 0.3f, 1f);
        fade.setDuration(500);

        AnimatorSet breakSet = new AnimatorSet();
        breakSet.playTogether(shakeX, fade);
        breakSet.start();
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView titleText, ratingText;
        ImageButton wishlistButton;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.posterImage);
            titleText = itemView.findViewById(R.id.titleText);
            ratingText = itemView.findViewById(R.id.ratingText);
            wishlistButton = itemView.findViewById(R.id.wishlistButton);
        }
    }
}
