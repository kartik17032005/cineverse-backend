package com.example.cineverse_movie_app_two.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.MovieDetailActivity;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.R;

import java.util.List;

/**
 * Universal banner adapter to be used for different sections
 * like Bollywood, Tollywood, Hollywood, etc.
 */
public class DifferentSectionBanners extends RecyclerView.Adapter<DifferentSectionBanners.BannerViewHolder> {

    private final Context context;
    private final List<Movie> bannerMovies;

    public DifferentSectionBanners(Context context, List<Movie> bannerMovies) {
        this.context = context;
        this.bannerMovies = bannerMovies;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_different_section_banners, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = bannerMovies.get(position);
        if (movie == null) return;

        // Load movie poster/banner image
        Glide.with(context)
                .load(movie.getPosterUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.bannerImage);

        // Set title and genre/year info
        holder.bannerTitle.setText(movie.getTitle());
        holder.bannerMeta.setText(
                String.format("%s • %s", movie.getGenre(), movie.getYear())
        );

        // Click → open MovieDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie", movie); // movie should implement Serializable or Parcelable

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context,
                    holder.bannerImage,
                    "posterTransition"
            );

            context.startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return bannerMovies.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        TextView bannerTitle, bannerMeta;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            bannerTitle = itemView.findViewById(R.id.bannerTitle);
            bannerMeta = itemView.findViewById(R.id.bannerMeta);
        }
    }
}
