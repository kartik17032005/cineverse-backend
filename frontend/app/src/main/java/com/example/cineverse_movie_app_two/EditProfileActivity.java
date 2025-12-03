package com.example.cineverse_movie_app_two;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail;
    private Button buttonSave, buttonCancel, buttonChangePhoto;
    private ImageView imageProfile;

    private Uri profileImageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private SharedPreferences prefs;
    private String oldEmail; // For identifying user when updating profile
    private RequestQueue requestQueue;

    private static final String BASE_URL = "http://10.26.135.247:8080/";

    private static final String TAG = "EditProfileDebug";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageProfile = findViewById(R.id.image_profile);
        buttonChangePhoto = findViewById(R.id.button_change_photo);
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        oldEmail = prefs.getString("user_email", "");
        String userName = prefs.getString("user_name", "");
        String userProfileImageUriString = prefs.getString("user_profile_image_uri", "");

        editName.setText(userName);
        editEmail.setText(oldEmail);

        if (!TextUtils.isEmpty(userProfileImageUriString)) {
            profileImageUri = Uri.parse(userProfileImageUriString);
            imageProfile.setImageURI(profileImageUri);
        }

        requestQueue = Volley.newRequestQueue(this);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            profileImageUri = uri;
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                imageProfile.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(EditProfileActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        buttonChangePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        buttonSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            if (TextUtils.isEmpty(oldEmail)) {
                Log.e(TAG, "Current email is empty! User might not be logged in properly.");
                Toast.makeText(this, "User not logged in. Please login again.", Toast.LENGTH_LONG).show();
                return;
            }

            if (validateInputs(name, email)) {
                // Log current email info and input data before request
                Log.d(TAG, "Sending profile update request:");
                Log.d(TAG, "currentEmail (oldEmail): " + oldEmail);
                Log.d(TAG, "newFullName: " + name);
                Log.d(TAG, "newEmail: " + email);
                Log.d(TAG, "newProfileImageUrl: " + (profileImageUri != null ? profileImageUri.toString() : "null"));

                callUpdateProfileApi(name, email, profileImageUri != null ? profileImageUri.toString() : "");
            }
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private boolean validateInputs(String name, String email) {
        if (TextUtils.isEmpty(name)) {
            editName.setError("Name is required");
            editName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email");
            editEmail.requestFocus();
            return false;
        }
        return true;
    }

    private void callUpdateProfileApi(String name, String email, String profileImageUriString) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("currentEmail", oldEmail);
            requestBody.put("newFullName", name);
            requestBody.put("newEmail", email);
            requestBody.put("newProfileImageUrl", profileImageUriString);

            Log.d(TAG, "Volley request JSON: " + requestBody.toString());

            String url = BASE_URL + "api/edit-profile";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    requestBody,
                    response -> {
                        Log.d(TAG, "Profile update successful: " + response.toString());

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_name", name);
                        editor.putString("user_email", email);  // Update stored email after successful update
                        editor.putString("user_profile_image_uri", profileImageUriString);
                        editor.apply();

                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        String body = "";
                        int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            body = new String(error.networkResponse.data);
                        }
                        Log.e(TAG, "Failed to update profile. Status code: " + statusCode +
                                ", Body: " + body, error);

                        Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + statusCode + " " + body, Toast.LENGTH_LONG).show();
                    }
            );

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON Exception forming update request", e);
            Toast.makeText(this, "Error forming update request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
