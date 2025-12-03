package com.example.cineverse_movie_app_two.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.MovieDetailActivity;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Movie> movieList;
    private final Context context;

    public BannerAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // Load banner image
        Glide.with(context)
                .load(movie.getPosterUrl())
                .centerCrop()
                .into(holder.imageView);

        // Hide all effects initially
        holder.lottieEffect.setVisibility(View.GONE);
        holder.glowOverlay.setVisibility(View.GONE);
        holder.glowOverlay.setAlpha(0f);
        holder.borderView.setVisibility(View.GONE);
        holder.borderView.setAlpha(0f);

        // On banner click
        holder.imageView.setOnClickListener(v -> {
            // Play Lottie effect
            holder.lottieEffect.setVisibility(View.VISIBLE);
            holder.lottieEffect.playAnimation();

            // Show glow overlay
            holder.glowOverlay.setVisibility(View.VISIBLE);
            holder.glowOverlay.animate().alpha(0.5f).setDuration(150).start();

            // Show border with fade-in
            holder.borderView.setVisibility(View.VISIBLE);
            holder.borderView.animate().alpha(1f).setDuration(200).start();

            // Open MovieDetailActivity with shared element transition
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie", movie); // Ensure Movie implements Serializable or Parcelable

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            (Activity) context,
                            holder.imageView,
                            "posterTransition"
                    );

            context.startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LottieAnimationView lottieEffect;
        View glowOverlay;
        View borderView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
            lottieEffect = itemView.findViewById(R.id.bannerEffect);
            glowOverlay = itemView.findViewById(R.id.glowOverlay);
            borderView = itemView.findViewById(R.id.bannerBorder);
        }
    }
}
