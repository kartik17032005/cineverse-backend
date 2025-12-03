package com.example.cineverse_movie_app_two;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistrationSuccessActivity extends AppCompatActivity {

    private TextView countdownText, successMessage, subMessage;
    private Button skipNow;
    private ImageView successCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_success);

        // System bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind views
        successMessage = findViewById(R.id.success_message);
        subMessage = findViewById(R.id.sub_message);
        countdownText = findViewById(R.id.countdownText);
        skipNow = findViewById(R.id.skipNow);
        successCircle = findViewById(R.id.success_circle);

        // Start modern, clean fade-in animation
        animateSuccessSequence();

        // Skip button click
        skipNow.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationSuccessActivity.this, LoginActivity.class));
            finish();
        });

        // Countdown timer
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                countdownText.setText("Redirecting in " + (secondsLeft + 1) + "...");
            }
            public void onFinish() {
                startActivity(new Intent(RegistrationSuccessActivity.this, LoginActivity.class));
                finish();
            }
        }.start();
    }

    private void animateSuccessSequence() {
        // Fade in and scale pulse for check icon
        successCircle.setScaleX(0.7f);
        successCircle.setScaleY(0.7f);
        successCircle.setAlpha(0f);
        successCircle.animate().alpha(0.95f).scaleX(1.15f).scaleY(1.15f).setDuration(320)
                .withEndAction(() -> successCircle.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();

        // Fade in messages and button with delays for polish
        successMessage.animate().alpha(1f).setDuration(380).setStartDelay(400).start();
        subMessage.animate().alpha(1f).setDuration(340).setStartDelay(620).start();
        countdownText.animate().alpha(1f).setDuration(280).setStartDelay(900).start();
        skipNow.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(1000).start();
    }
}
