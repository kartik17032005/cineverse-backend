package com.example.cineverse_movie_app_two;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText email, password;
    Button loginBtn;
    TextView registerRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        registerRedirect = findViewById(R.id.registerRedirect);

        loginBtn.setOnClickListener(v -> {
            if (!validateEmail() || !validatePassword()) {
                return;
            }
            loginBtn.setEnabled(false);
            loginUser(email.getText().toString().trim(), password.getText().toString().trim());
        });

        registerRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateEmail() {
        String emailText = email.getText().toString().trim();
        if (emailText.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return false;
        }
        email.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String passwordText = password.getText().toString().trim();
        if (passwordText.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        }
        password.setError(null);
        return true;
    }

    private void loginUser(String emailStr, String passwordStr) {
        String url = "http://10.26.135.247:8080/api/login";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", emailStr);
            jsonBody.put("password", passwordStr);
        } catch (JSONException e) {
            e.printStackTrace();
            loginBtn.setEnabled(true);
            Toast.makeText(this, "Unexpected error while creating login request", Toast.LENGTH_LONG).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    loginBtn.setEnabled(true);
                    Log.d(TAG, "Login successful: " + response);

                    try {
                        // Backend sends UserDTO: { userId, fullName, email, profileImageUrl }
                        int userId = response.optInt("userId", -1);
                        String fullName = response.optString("fullName", "");
                        String emailVal = response.optString("email", "");
                        String profileImageUrl = response.optString("profileImageUrl", "");

                        if (userId != -1 && !emailVal.isEmpty()) {
                            // Save user data to SharedPreferences for later use in app
                            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                            editor.putInt("user_id", userId);
                            editor.putString("full_name", fullName);
                            editor.putString("user_email", emailVal);
                            editor.putString("profile_image_url", profileImageUrl);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "✅ Welcome, " + fullName, Toast.LENGTH_LONG).show();

                            // Go to HomeActivity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid login response", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing login response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    loginBtn.setEnabled(true);
                    String message = "Login failed";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        message += ": " + new String(error.networkResponse.data);
                    } else if (error.getMessage() != null) {
                        message += ": " + error.getMessage();
                    }
                    Log.e(TAG, "Login error: " + message, error);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}
