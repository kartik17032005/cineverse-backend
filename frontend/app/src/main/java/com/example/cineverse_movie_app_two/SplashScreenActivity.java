package com.example.cineverse_movie_app_two;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.cineverse_movie_app_two.MainActivity;
import com.example.cineverse_movie_app_two.R;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        VideoView splashVideo = findViewById(R.id.splashVideo);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cineverse_splash);
        splashVideo.setVideoURI(video);
        splashVideo.start();

        splashVideo.setOnCompletionListener(mp -> {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        });
    }
}
