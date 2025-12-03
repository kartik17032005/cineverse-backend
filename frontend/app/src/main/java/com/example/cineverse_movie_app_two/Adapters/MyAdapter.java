package com.example.cineverse_movie_app_two.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.Models.Movie;
import com.example.cineverse_movie_app_two.MovieDetailActivity;
import com.example.cineverse_movie_app_two.MovieListActivity;
import com.example.cineverse_movie_app_two.R;
import com.example.cineverse_movie_app_two.ViewHolder.MovieViewHolder;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    Context context;
    List<Movie> movies;

    public MyAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());

        Glide.with(context)
                .load(movie.getPosterUrl())
                .into(holder.banner);

        // If you want to open detail on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie", movie);  // Pass full movie object
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        private final Context context;
        private final List<String> categoryList;
        private final List<Integer> categoryIcons;

        public CategoryAdapter(Context context, List<String> categoryList, List<Integer> categoryIcons) {
            this.context = context;
            this.categoryList = categoryList;
            this.categoryIcons = categoryIcons;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_tile, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            String category = categoryList.get(position);
            holder.categoryName.setText(category);
            holder.categoryIcon.setImageResource(categoryIcons.get(position));

            holder.itemView.setOnClickListener(v -> {
                // Add click animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        0.95f, 1.0f, 0.95f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(300);
                v.startAnimation(scaleAnimation);

                // Navigate to MovieListActivity
                Intent intent = new Intent(context, MovieListActivity.class);
                intent.putExtra("category", category.toLowerCase());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        public static class CategoryViewHolder extends RecyclerView.ViewHolder {
            ImageView categoryIcon;
            TextView categoryName;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                categoryIcon = itemView.findViewById(R.id.lottieCategoryIcon);
                categoryName = itemView.findViewById(R.id.categoryName);
            }
        }
    }
}
