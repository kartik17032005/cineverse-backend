package com.example.cineverse_movie_app_two;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.*;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, email, password, confirm;
    Button registerBtn;
    TextView loginActivity;
    ImageView profileImage;
    ImageButton addImageButton;

    private static final String TAG = "RegisterActivity";
    private Uri selectedImageUri;

    private static final String REGISTER_URL = "http://10.26.135.247:8080/api/register";

    ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullName = findViewById(R.id.full_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
        registerBtn = findViewById(R.id.registerbtn);
        loginActivity = findViewById(R.id.loginActivity);
        profileImage = findViewById(R.id.profileImage);
        addImageButton = findViewById(R.id.addImageButton);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            profileImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        addImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        registerBtn.setOnClickListener(v -> processFormFields());
        loginActivity.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    public void processFormFields() {
        if (!validateFullName() || !validateEmail() || !validatePasswordAndConfirm()) {
            return;
        }

        registerBtn.setEnabled(false);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                REGISTER_URL,
                response -> {
                    registerBtn.setEnabled(true);
                    try {
                        String jsonString = new String(response.data);
                        Log.d(TAG, "Server Response: " + jsonString);

                        if (jsonString.trim().equalsIgnoreCase("success")) {
                            Toast.makeText(this, "✅ Registration successful. You can now log in.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, RegistrationSuccessActivity.class));
                        } else {
                            handleFailureMessage(jsonString);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "❌ Error parsing server response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    registerBtn.setEnabled(true);
                    String body = "";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data);
                    }
                    Log.e(TAG, "Volley Error: " + body);
                    Toast.makeText(RegisterActivity.this, "Error: " + body, Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fullName", fullName.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        params.put("profileImage", new DataPart("profile.jpg", baos.toByteArray(), "image/jpeg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private void handleFailureMessage(String status) {
        switch (status.toLowerCase()) {
            case "email already exists":
                Toast.makeText(this, "❌ Email already exists.", Toast.LENGTH_LONG).show();
                break;
            case "invalid password":
                Toast.makeText(this, "⚠️ Password must include uppercase, lowercase, number, and special character.", Toast.LENGTH_LONG).show();
                break;
            case "missing fields":
                Toast.makeText(this, "⚠️ Please fill in all required fields.", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "❌ Registration failed: " + status, Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateFullName() {
        String full_Name = fullName.getText().toString().trim();
        if (full_Name.isEmpty()) {
            fullName.setError("Full Name is required");
            return false;
        }
        fullName.setError(null);
        return true;
    }

    public boolean validateEmail() {
        String email_Address = email.getText().toString().trim();
        if (email_Address.isEmpty()) {
            email.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_Address).matches()) {
            email.setError("Enter a valid email address");
            return false;
        }
        email.setError(null);
        return true;
    }

    public boolean validatePasswordAndConfirm() {
        String password_Text = password.getText().toString().trim();
        String confirm_Text = confirm.getText().toString().trim();

        if (password_Text.isEmpty()) {
            password.setError("Password is required");
            return false;
        } else if (password_Text.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return false;
        } else if (!password_Text.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$")) {
            password.setError("Include upper, lower, number & special char");
            return false;
        } else if (confirm_Text.isEmpty()) {
            confirm.setError("Confirm Password is required");
            return false;
        } else if (!password_Text.equals(confirm_Text)) {
            password.setError("Passwords do not match");
            confirm.setError("Passwords do not match");
            return false;
        }
        password.setError(null);
        confirm.setError(null);
        return true;
    }
}
