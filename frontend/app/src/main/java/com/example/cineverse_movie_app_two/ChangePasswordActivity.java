package com.example.cineverse_movie_app_two;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Password...");
        progressDialog.setCancelable(false);

        btnChangePassword.setOnClickListener(v -> {
            if (validateInputs()) {
                changePassword();
            }
        });
    }

    private boolean validateInputs() {
        String oldPass = etOldPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass)) {
            etOldPassword.setError("Old password is required");
            etOldPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(newPass)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return false;
        }

        if (newPass.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return false;
        }

        if (!newPass.equals(confirmPass)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void changePassword() {
        String oldPass = etOldPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();

        progressDialog.show();
        btnChangePassword.setEnabled(false);

        // Get stored email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);

        if (userEmail == null) {
            Toast.makeText(this, "User email not found. Please log in again.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            btnChangePassword.setEnabled(true);
            return;
        }

        JSONObject payload = new JSONObject();
        try {
            payload.put("email", userEmail);
            payload.put("oldPassword", oldPass);
            payload.put("newPassword", newPass);
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            btnChangePassword.setEnabled(true);
            return;
        }

        // Correct backend URL based on your controller mapping
        String url = "http://10.26.135.247:8080/api/change-password";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {
                    progressDialog.dismiss();
                    btnChangePassword.setEnabled(true);
                    if (response.optBoolean("success")) {
                        String successMsg = response.optString("message", "Password changed successfully!");
                        Toast.makeText(ChangePasswordActivity.this, successMsg, Toast.LENGTH_LONG).show();
                        // Delay finish to let user see message
                        finish();
                    } else {
                        String errorMsg = response.optString("message", response.optString("error", "Failed to change password"));
                        Toast.makeText(ChangePasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    btnChangePassword.setEnabled(true);
                    String message = "Network error";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        message = "Error " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data);
                    } else if (error.getMessage() != null) {
                        message = error.getMessage();
                    }
                    Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                String authToken = prefs.getString("auth_token", null);
                if (authToken != null) {
                    headers.put("Authorization", "Bearer " + authToken);
                }

                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
