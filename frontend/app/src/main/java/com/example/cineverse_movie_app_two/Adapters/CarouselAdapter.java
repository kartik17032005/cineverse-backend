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
import android.os.Handler;
import android.os.Looper;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.MovieDetailActivity;
import com.example.cineverse_movie_app_two.R;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final Context context;
    private final List<Movie> movies;
    private final SharedPreferences prefs;
    private final Handler animationHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    public CarouselAdapter(Context context, List<Movie> movies) {
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
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carousel_movie, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // 🎬 Set movie data
        holder.carouselTitle.setText(movie.getTitle());

        // 🖼️ Load poster with holographic glitch effect
        Glide.with(context)
                .load(movie.getPosterUrl())
                .apply(new RequestOptions()
                        .transform(new RoundedCorners(60))
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder))
                .into(holder.carouselPoster);

        // 💖 Update wishlist state
        updateWishlistIcon(holder.wishlistButton, movie.isWishlisted());

        // 🚀 Start continuous animations
        startFloatingAnimation(holder);
        startHolographicGlitch(holder);
        startEnergyPulse(holder);
        startScanLineAnimation(holder);

        // 💫 Wishlist click with explosive animation
        holder.wishlistButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            boolean newState = !movie.isWishlisted();
            movie.setWishlisted(newState);

            updateWishlistIcon(holder.wishlistButton, newState);

            if (newState) {
                playExplosiveHeartAnimation(holder);
                playMagneticFieldPulse(holder);
            } else {
                playHeartBreakAnimation(holder);
            }

            saveWishlistState(movie.getId(), newState);
        });

        // 🎯 Play button with portal effect
        holder.playButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            playPortalAnimation(holder, () -> {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("movie", movie);
                context.startActivity(intent);
            });
        });

        // 🌟 Card hover effects
        holder.itemView.setOnClickListener(v -> {
            playCardTeleportAnimation(holder, () -> {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("movie", movie);
                context.startActivity(intent);
            });
        });
    }

    // 🎭 MIND-BLOWING ANIMATIONS START HERE 🎭

    /**
     * 🌊 Floating holographic card animation
     */
    private void startFloatingAnimation(CarouselViewHolder holder) {
        ObjectAnimator floatY = ObjectAnimator.ofFloat(holder.hologramCard, "translationY", 0f, -15f, 0f);
        floatY.setDuration(3000 + random.nextInt(1000));
        floatY.setRepeatCount(ValueAnimator.INFINITE);
        floatY.setInterpolator(new AccelerateDecelerateInterpolator());
        floatY.start();

        ObjectAnimator floatX = ObjectAnimator.ofFloat(holder.hologramCard, "translationX", 0f, 5f, 0f, -5f, 0f);
        floatX.setDuration(4000 + random.nextInt(1000));
        floatX.setRepeatCount(ValueAnimator.INFINITE);
        floatX.setInterpolator(new AccelerateDecelerateInterpolator());
        floatX.start();

        ObjectAnimator rotateZ = ObjectAnimator.ofFloat(holder.hologramCard, "rotation", 0f, 1f, -1f, 0f);
        rotateZ.setDuration(5000 + random.nextInt(2000));
        rotateZ.setRepeatCount(ValueAnimator.INFINITE);
        rotateZ.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateZ.start();
    }

    /**
     * ⚡ Holographic glitch effect on poster
     */
    private void startHolographicGlitch(CarouselViewHolder holder) {
        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (holder.carouselPoster != null) {
                    // Random glitch effect
                    ObjectAnimator glitchX = ObjectAnimator.ofFloat(holder.carouselPoster, "translationX", 0f, random.nextInt(10) - 5);
                    glitchX.setDuration(100);
                    glitchX.setRepeatCount(1);
                    glitchX.setRepeatMode(ValueAnimator.REVERSE);
                    glitchX.start();

                    ObjectAnimator glitchAlpha = ObjectAnimator.ofFloat(holder.carouselPoster, "alpha", 1f, 0.7f, 1f);
                    glitchAlpha.setDuration(150);
                    glitchAlpha.start();
                }

                // Schedule next glitch
                animationHandler.postDelayed(this, 2000 + random.nextInt(3000));
            }
        }, random.nextInt(1000));
    }

    /**
     * 🔥 Energy pulse around play button
     */
    private void startEnergyPulse(CarouselViewHolder holder) {
        ObjectAnimator pulseScale = ObjectAnimator.ofFloat(holder.energyPulse, "scaleX", 1f, 1.3f, 1f);
        pulseScale.setRepeatCount(ValueAnimator.INFINITE);
        pulseScale.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator pulseScaleY = ObjectAnimator.ofFloat(holder.energyPulse, "scaleY", 1f, 1.3f, 1f);
        pulseScaleY.setRepeatCount(ValueAnimator.INFINITE);
        pulseScaleY.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator pulseAlpha = ObjectAnimator.ofFloat(holder.energyPulse, "alpha", 0.3f, 0.8f, 0.3f);
        pulseAlpha.setRepeatCount(ValueAnimator.INFINITE);
        pulseAlpha.setRepeatMode(ValueAnimator.RESTART);

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulseScale, pulseScaleY, pulseAlpha);
        pulseSet.setDuration(2000);
        pulseSet.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseSet.start();

    }

    /**
     * 📡 Scan line animation across the card
     */
    private void startScanLineAnimation(CarouselViewHolder holder) {
        ObjectAnimator scanTranslate = ObjectAnimator.ofFloat(holder.scanLines, "translationY", -400f, 400f);
        scanTranslate.setDuration(3000);
        scanTranslate.setRepeatCount(ValueAnimator.INFINITE);
        scanTranslate.setInterpolator(new AccelerateDecelerateInterpolator());
        scanTranslate.start();

        ObjectAnimator scanAlpha = ObjectAnimator.ofFloat(holder.scanLines, "alpha", 0f, 0.3f, 0f);
        scanAlpha.setDuration(3000);
        scanAlpha.setRepeatCount(ValueAnimator.INFINITE);
        scanAlpha.start();
    }

    /**
     * 💥 EXPLOSIVE HEART ANIMATION - When adding to wishlist
     */
    private void playExplosiveHeartAnimation(CarouselViewHolder holder) {
        // Heart explosion sequence
        AnimatorSet explosionSet = new AnimatorSet();

        // Scale explosion
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(holder.wishlistButton, "scaleX", 1f, 2.5f, 1.2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(holder.wishlistButton, "scaleY", 1f, 2.5f, 1.2f);

        // Rotation burst
        ObjectAnimator rotateBurst = ObjectAnimator.ofFloat(holder.wishlistButton, "rotation", 0f, 360f);

        // Alpha pulse
        ObjectAnimator alphaPulse = ObjectAnimator.ofFloat(holder.wishlistButton, "alpha", 1f, 0.3f, 1f);

        explosionSet.playTogether(scaleUpX, scaleUpY, rotateBurst, alphaPulse);
        explosionSet.setDuration(600);
        explosionSet.setInterpolator(new OvershootInterpolator(2f));
        explosionSet.start();

        // Heart glow explosion
        ObjectAnimator glowExplosion = ObjectAnimator.ofFloat(holder.heartGlow, "scaleX", 1f, 3f, 1f);
        ObjectAnimator glowExplosionY = ObjectAnimator.ofFloat(holder.heartGlow, "scaleY", 1f, 3f, 1f);
        ObjectAnimator glowAlpha = ObjectAnimator.ofFloat(holder.heartGlow, "alpha", 0.6f, 1f, 0.6f);

        AnimatorSet glowSet = new AnimatorSet();
        glowSet.playTogether(glowExplosion, glowExplosionY, glowAlpha);
        glowSet.setDuration(800);
        glowSet.setInterpolator(new BounceInterpolator());
        glowSet.start();
    }

    /**
     * 💔 Heart break animation - When removing from wishlist
     */
    private void playHeartBreakAnimation(CarouselViewHolder holder) {
        // Shatter effect
        ObjectAnimator shakeX = ObjectAnimator.ofFloat(holder.wishlistButton, "translationX", 0f, -10f, 10f, -5f, 5f, 0f);
        shakeX.setDuration(500);
        shakeX.setInterpolator(new CycleInterpolator(3));

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(holder.wishlistButton, "alpha", 1f, 0.3f, 1f);
        fadeOut.setDuration(500);

        AnimatorSet breakSet = new AnimatorSet();
        breakSet.playTogether(shakeX, fadeOut);
        breakSet.start();
    }

    /**
     * 🌀 Magnetic field pulse when hearting
     */
    private void playMagneticFieldPulse(CarouselViewHolder holder) {
        holder.magneticField.setVisibility(View.VISIBLE);
        holder.magneticField.setAlpha(0f);

        ObjectAnimator fieldAlpha = ObjectAnimator.ofFloat(holder.magneticField, "alpha", 0f, 0.4f, 0f);
        ObjectAnimator fieldScale = ObjectAnimator.ofFloat(holder.magneticField, "scaleX", 0.5f, 1.5f, 1f);
        ObjectAnimator fieldScaleY = ObjectAnimator.ofFloat(holder.magneticField, "scaleY", 0.5f, 1.5f, 1f);

        AnimatorSet fieldSet = new AnimatorSet();
        fieldSet.playTogether(fieldAlpha, fieldScale, fieldScaleY);
        fieldSet.setDuration(1000);
        fieldSet.setInterpolator(new AccelerateDecelerateInterpolator());
        fieldSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                holder.magneticField.setVisibility(View.INVISIBLE);
            }
        });
        fieldSet.start();
    }

    /**
     * 🌌 Portal animation for play button
     */
    private void playPortalAnimation(CarouselViewHolder holder, Runnable onComplete) {
        // Portal opening effect
        ObjectAnimator portalRotate = ObjectAnimator.ofFloat(holder.energyPulse, "rotation", 0f, 1080f);
        ObjectAnimator portalScale = ObjectAnimator.ofFloat(holder.energyPulse, "scaleX", 1f, 5f);
        ObjectAnimator portalScaleY = ObjectAnimator.ofFloat(holder.energyPulse, "scaleY", 1f, 5f);
        ObjectAnimator portalAlpha = ObjectAnimator.ofFloat(holder.energyPulse, "alpha", 0.3f, 1f, 0f);

        AnimatorSet portalSet = new AnimatorSet();
        portalSet.playTogether(portalRotate, portalScale, portalScaleY, portalAlpha);
        portalSet.setDuration(1000);
        portalSet.setInterpolator(new AccelerateDecelerateInterpolator());
        portalSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onComplete != null) onComplete.run();
            }
        });
        portalSet.start();
    }

    /**
     * ⚡ Card teleport animation
     */
    private void playCardTeleportAnimation(CarouselViewHolder holder, Runnable onComplete) {
        // Teleport sequence
        ObjectAnimator teleportScale = ObjectAnimator.ofFloat(holder.hologramCard, "scaleX", 1f, 0.8f, 1.1f, 1f);
        ObjectAnimator teleportScaleY = ObjectAnimator.ofFloat(holder.hologramCard, "scaleY", 1f, 0.8f, 1.1f, 1f);
        ObjectAnimator teleportAlpha = ObjectAnimator.ofFloat(holder.hologramCard, "alpha", 1f, 0.7f, 1f);
        ObjectAnimator teleportRotate = ObjectAnimator.ofFloat(holder.hologramCard, "rotationY", 0f, 180f, 360f);

        AnimatorSet teleportSet = new AnimatorSet();
        teleportSet.playTogether(teleportScale, teleportScaleY, teleportAlpha, teleportRotate);
        teleportSet.setDuration(800);
        teleportSet.setInterpolator(new OvershootInterpolator(1.5f));
        teleportSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onComplete != null) onComplete.run();
            }
        });
        teleportSet.start();
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

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView carouselPoster;
        TextView carouselTitle;
        ImageButton wishlistButton, playButton;
        View scanLines, energyPulse, heartGlow, magneticField, hologramCard;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            carouselPoster = itemView.findViewById(R.id.carouselPoster);
            carouselTitle = itemView.findViewById(R.id.carouselTitle);
            wishlistButton = itemView.findViewById(R.id.wishlistButton);
            playButton = itemView.findViewById(R.id.neonPlayButton);
            scanLines = itemView.findViewById(R.id.scanLines);
            energyPulse = itemView.findViewById(R.id.energyPulse);
            heartGlow = itemView.findViewById(R.id.heartGlow);
            magneticField = itemView.findViewById(R.id.magneticField);
            hologramCard = itemView.findViewById(R.id.hologramCard);
        }
    }
}
