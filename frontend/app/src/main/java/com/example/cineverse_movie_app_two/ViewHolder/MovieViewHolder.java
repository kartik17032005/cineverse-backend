package com.example.cineverse_movie_app_two.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cineverse_movie_app_two.R;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    public ImageView banner;
    public TextView title;
    public TextView genre;
    public TextView year;
    public MovieViewHolder(@NonNull View itemView) {
        super(itemView);

        banner = itemView.findViewById(R.id.sampleBanner);
        title = itemView.findViewById(R.id.titleText);
        genre = itemView.findViewById(R.id.genreText);
        year = itemView.findViewById(R.id.yearText);
    }
}
