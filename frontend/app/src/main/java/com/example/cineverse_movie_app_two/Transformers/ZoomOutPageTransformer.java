package com.example.cineverse_movie_app_two.Transformers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        float MIN_SCALE = 0.85f;
        float MIN_ALPHA = 0.5f;

        if (position < -1 || position > 1) {
            page.setAlpha(0);
        } else {
            float scale = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float alpha = MIN_ALPHA + (scale - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA);

            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(alpha);
            page.setTranslationX(page.getWidth() * -position * 0.25f); // Parallax effect
        }
    }
}
