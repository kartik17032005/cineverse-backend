package com.example.cineverse_movie_app_two.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cineverse_movie_app_two.R;

public class BannerViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    View borderView;
    LottieAnimationView lottieEffect;
    View glowOverlay;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.bannerImage);
        borderView = itemView.findViewById(R.id.bannerBorder);
        lottieEffect = itemView.findViewById(R.id.bannerEffect);
        glowOverlay = itemView.findViewById(R.id.glowOverlay);
    }
}

