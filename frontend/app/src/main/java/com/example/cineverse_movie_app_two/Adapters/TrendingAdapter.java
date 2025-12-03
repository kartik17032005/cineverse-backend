package com.example.cineverse_movie_app_two.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.Models.TrendingMovie;
import com.example.cineverse_movie_app_two.R;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder> {
    private Context context;
    private List<TrendingMovie> movieList;

    public TrendingAdapter(Context context, List<TrendingMovie> movies) {
        this.context = context;
        this.movieList = movies;
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trending, parent, false);
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        TrendingMovie movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class TrendingViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        TrendingViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.posterImage);
            title = itemView.findViewById(R.id.movieTitle);
        }
    }
}
