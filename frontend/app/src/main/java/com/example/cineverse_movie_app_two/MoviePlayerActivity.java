package com.example.cineverse_movie_app_two;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class MoviePlayerActivity extends AppCompatActivity {
    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String movieUrl = getIntent().getStringExtra("MOVIE_URL");

        if (movieUrl != null && !movieUrl.isEmpty()) {
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(movieUrl));
            player.setMediaItem(mediaItem);

            player.prepare();
            player.setPlayWhenReady(true);
        } else {
            // Handle missing URL gracefully, e.g., show message or finish
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
