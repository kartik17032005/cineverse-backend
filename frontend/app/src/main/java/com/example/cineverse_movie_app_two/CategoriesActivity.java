package com.example.cineverse_movie_app_two;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cineverse_movie_app_two.Adapters.CategoryAdapter;
import com.example.cineverse_movie_app_two.Adapters.ShimmerAdapter;
import com.example.cineverse_movie_app_two.Models.Category;
import com.example.cineverse_movie_app_two.Utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private final List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        recyclerView = findViewById(R.id.categoryRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.category_grid_spacing);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        // Step 1: Show shimmer placeholder adapter
        ShimmerAdapter shimmerAdapter = new ShimmerAdapter(6);
        recyclerView.setAdapter(shimmerAdapter);

        // Step 2: After delay, load real categories with animation
        new Handler().postDelayed(() -> {
            loadCategories();
            // Animate recycler items appearance once the adapter is set
            recyclerView.setAdapter(adapter);
            runLayoutAnimation();
        }, 2000); // 2 seconds delay
    }

    private void loadCategories() {
        categoryList.clear();

        categoryList.add(new Category(28, "Action", "genre_action.json"));
        categoryList.add(new Category(35, "Comedy", "genre_comedy.json"));
        categoryList.add(new Category(12, "Adventure", "genre_adventure.json"));
        categoryList.add(new Category(16, "Animation", "genre_animation.json"));
        categoryList.add(new Category(80, "Crime", "genre_crime.json"));
        categoryList.add(new Category(99, "Documentary", "genre_documentary.json"));
        categoryList.add(new Category(10751, "Family", "genre_family.json"));
        categoryList.add(new Category(14, "Fantasy", "genre_fantasy.json"));
        categoryList.add(new Category(36, "History", "genre_history.json"));
        categoryList.add(new Category(27, "Horror", "genre_horror.json"));
        categoryList.add(new Category(9648, "Mystery", "genre_mystery.json"));
        categoryList.add(new Category(10749, "Romance", "genre_romance.json"));
        categoryList.add(new Category(878, "Sci-Fi", "genre_scifi.json"));
        // Add more categories as needed

        adapter = new CategoryAdapter(this, categoryList, category -> {
            Intent intent = new Intent(CategoriesActivity.this, MovieListActivity.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("categoryName", category.getName());
            startActivity(intent);
        });
    }

    private void runLayoutAnimation() {
        // Animate the recycler view items with scale and fade (nice pop-in effect)
        recyclerView.setAlpha(0f);
        recyclerView.setScaleX(0.9f);
        recyclerView.setScaleY(0.9f);

        recyclerView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator())
                .start();

        // Optional: animate each item individually (if adapter supports it)
        // Could be done inside adapter onBindViewHolder to stagger item animations
    }
}
