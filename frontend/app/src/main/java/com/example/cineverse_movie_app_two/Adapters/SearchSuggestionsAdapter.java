package com.example.cineverse_movie_app_two.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.R;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import java.util.List;

public class SearchSuggestionsAdapter extends RecyclerView.Adapter<SearchSuggestionsAdapter.SuggestionVH> {

    public interface OnSuggestionClick {
        void onClick(Movie movie);
    }

    private final List<Movie> data;
    private final OnSuggestionClick onClick;

    // 🔥 Create shimmer once (reuse for all items)
    private final ShimmerDrawable shimmerDrawable;

    public SearchSuggestionsAdapter(List<Movie> data, OnSuggestionClick onClick) {
        this.data = data;
        this.onClick = onClick;

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDuration(1000)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(1f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();

        shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
    }

    @NonNull
    @Override
    public SuggestionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion, parent, false);
        return new SuggestionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionVH h, int pos) {
        Movie m = data.get(pos);

        // ✅ Handle null safely
        String title = (m.getTitle() == null || m.getTitle().isEmpty()) ? "Untitled" : m.getTitle();
        String genre = (m.getGenre() == null || m.getGenre().isEmpty()) ? "Unknown" : m.getGenre();
        String year = (m.getYear() == null || m.getYear().isEmpty()) ? "N/A" : m.getYear();

        h.title.setText(title);
        h.meta.setText(genre + " • " + year);

        // ✅ Ratings (TMDB 0-10 → Stars 0-5)
        float stars = (float) (m.getRating() / 2.0);
        h.ratingBar.setRating(stars);
        h.ratingText.setText(String.format("%.1f", m.getRating()));

        // ✅ Glide with shimmer + crossfade
        Glide.with(h.itemView.getContext())
                .load(m.getBannerImageUrl()) // Using backdropUrl if available, else posterUrl
                .placeholder(shimmerDrawable) // shimmer while loading
                .error(R.drawable.ic_placeholder) // fallback if image fails
                .transition(DrawableTransitionOptions.withCrossFade()) // smooth fade
                .into(h.poster);

        // ✅ Click event
        h.itemView.setOnClickListener(v -> onClick.onClick(m));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SuggestionVH extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, meta, ratingText;
        RatingBar ratingBar;

        SuggestionVH(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.suggPoster);
            title = itemView.findViewById(R.id.suggTitle);
            meta = itemView.findViewById(R.id.suggMeta);
            ratingBar = itemView.findViewById(R.id.suggRatingBar);
            ratingText = itemView.findViewById(R.id.suggRatingText);
        }
    }
}
