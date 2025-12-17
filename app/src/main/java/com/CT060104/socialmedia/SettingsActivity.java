package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import models.user.UserResponse;
import repository.AuthRepository;
import repository.UserRepository;
import utils.TokenManager;

public class SettingsActivity extends AppCompatActivity {

    private TextView user_name;
    private LinearLayout profile;
    private LinearLayout posts;
    private LinearLayout comments;
    private LinearLayout likes;
    private LinearLayout friends;
    private LinearLayout password;
    private LinearLayout logout;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize repositories and token manager
        authRepository = new AuthRepository(this);
        userRepository = new UserRepository(this);
        tokenManager = TokenManager.getInstance(this);

        // Check if user is logged in
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Initialize views
        initializeViews();

        // Load user profile
        loadUserProfile();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        user_name = findViewById(R.id.user_name);
        profile = findViewById(R.id.profile);
        posts = findViewById(R.id.posts);
        comments = findViewById(R.id.post_comments);
        likes = findViewById(R.id.post_likes);
        friends = findViewById(R.id.friends);
        password = findViewById(R.id.password);
        logout = findViewById(R.id.logout);
    }

    private void setupClickListeners() {
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ModifyProfile.class);
            startActivity(intent);
        });

        posts.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UserPosts.class);
            startActivity(intent);
        });

        comments.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UserComments.class);
            startActivity(intent);
        });

        likes.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UserLikes.class);
            startActivity(intent);
        });

        friends.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, FriendsActivity.class);
            startActivity(intent);
        });

        password.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePassword.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            performLogout();
        });
    }

    private void loadUserProfile() {
        userRepository.getProfile().observe(this, new Observer<UserRepository.ApiResult<UserResponse>>() {
            @Override
            public void onChanged(UserRepository.ApiResult<UserResponse> result) {
                if (result.isSuccess() && result.getData() != null) {
                    UserResponse user = result.getData();
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    user_name.setText(fullName);
                } else {
                    user_name.setText("User"); // Fallback display name
                    Toast.makeText(SettingsActivity.this,
                            "Failed to load profile: " + result.getErrorMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performLogout() {
        // Clear token and user data from secure storage
        authRepository.logout();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status again and refresh profile if still logged in
        if (authRepository.isLoggedIn()) {
            loadUserProfile();
        } else {
            navigateToLogin();
        }
    }
}