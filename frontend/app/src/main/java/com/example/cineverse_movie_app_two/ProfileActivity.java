package com.example.cineverse_movie_app_two;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageLarge;
    private TextView fullNameText, emailText;
    private Button editProfileBtn, changePasswordBtn, logoutBtn;
    private ImageButton backButton;

    private static final String BASE_URL = "http://10.26.135.247:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();

        // Edit profile
        editProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        // Change password
        changePasswordBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
        });

        // Logout
        logoutBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        profileImageLarge = findViewById(R.id.profileImageLarge);
        fullNameText = findViewById(R.id.fullNameText);
        emailText = findViewById(R.id.emailText);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("full_name", "User");
        String email = prefs.getString("user_email", "user@example.com");
        String profileImageUrl = prefs.getString("profile_image_url", "");

        fullNameText.setText(fullName);
        emailText.setText(email);

        if (!profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(BASE_URL + profileImageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(profileImageLarge);
        } else {
            profileImageLarge.setImageResource(R.drawable.profile);
        }
    }
}
