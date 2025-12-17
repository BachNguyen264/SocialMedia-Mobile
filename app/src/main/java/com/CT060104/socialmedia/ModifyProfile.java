package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import models.user.UserResponse;
import repository.AuthRepository;
import repository.UserRepository;
import utils.TokenManager;
import Model.User;

public class ModifyProfile extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private Button submitButton;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private TokenManager tokenManager;
    private UserResponse currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

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
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        submitButton = findViewById(R.id.submit);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        // Load current user profile
        loadUserProfile();

        // Set up click listener
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                updateProfile();
            }
        });
    }

    private void loadUserProfile() {
        setLoading(true);

        userRepository.getProfile().observe(this, new Observer<UserRepository.ApiResult<UserResponse>>() {
            @Override
            public void onChanged(UserRepository.ApiResult<UserResponse> result) {
                setLoading(false);

                if (result.isSuccess() && result.getData() != null) {
                    currentUser = result.getData();
                    populateFields();
                } else {
                    Toast.makeText(ModifyProfile.this,
                            "Failed to load profile: " + result.getErrorMessage(),
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void populateFields() {
        if (currentUser != null) {
            firstNameEditText.setText(currentUser.getFirstName());
            lastNameEditText.setText(currentUser.getLastName());
            emailEditText.setText(currentUser.getEmail());
        }
    }

    private boolean validateInputs() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (firstName.isEmpty()) {
            firstNameEditText.setError("First Name cannot be empty");
            firstNameEditText.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last Name cannot be empty");
            lastNameEditText.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            emailEditText.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void updateProfile() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Create updated user object
        UserResponse updatedUser = new UserResponse();
        updatedUser.setId(currentUser.getId());
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        updatedUser.setEmail(email);
        updatedUser.setCreatedAt(currentUser.getCreatedAt());
        updatedUser.setUpdatedAt(currentUser.getUpdatedAt());

        setLoading(true);
        submitButton.setEnabled(false);

        userRepository.updateProfile(updatedUser).observe(this, new Observer<UserRepository.ApiResult<UserResponse>>() {
            @Override
            public void onChanged(UserRepository.ApiResult<UserResponse> result) {
                setLoading(false);
                submitButton.setEnabled(true);

                if (result.isSuccess()) {
                    Toast.makeText(ModifyProfile.this,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = result.getErrorMessage();
                    Toast.makeText(ModifyProfile.this,
                            "Failed to update profile: " + errorMessage,
                            Toast.LENGTH_LONG).show();

                    // If the error is about email being used, show error on email field
                    if (errorMessage.toLowerCase().contains("email") &&
                            errorMessage.toLowerCase().contains("used")) {
                        emailEditText.setError("This email has been used before");
                        emailEditText.requestFocus();
                    }
                }
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ModifyProfile.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}