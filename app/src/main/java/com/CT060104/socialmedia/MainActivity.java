package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import repository.AuthRepository;
import utils.TokenManager;

public class MainActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button createAccountButton;
    private TextView loginText;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize repository and token manager
        authRepository = new AuthRepository(this);

        // Check if user is already logged in
        if (authRepository.isLoggedIn()) {
            navigateToHome();
            return;
        }

        // Initialize views
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        createAccountButton = findViewById(R.id.create_account);
        loginText = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        createAccountButton.setOnClickListener(v -> {
            if (validateInputs()) {
                performRegistration();
            }
        });

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInputs() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

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

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Please confirm your password");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords don't match");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void performRegistration() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Show loading state
        setLoading(true);

        authRepository.register(firstName, lastName, email, password).observe(this, new Observer<AuthRepository.AuthResult>() {
            @Override
            public void onChanged(AuthRepository.AuthResult authResult) {
                setLoading(false);

                if (authResult.isSuccess()) {
                    SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);
                    prefs.edit()
                            .putString("display_name",
                                    authResult.getUser().getFirstName() + " " + authResult.getUser().getLastName())
                            .apply();
                    Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    Toast.makeText(MainActivity.this, authResult.getMessage(), Toast.LENGTH_LONG).show();
                    // If the error is about email being used, focus on email field
                    if (authResult.getMessage().toLowerCase().contains("email") &&
                            authResult.getMessage().toLowerCase().contains("used")) {
                        emailEditText.setError("This email has been used before");
                        emailEditText.requestFocus();
                    }
                }
            }
        });
    }

    private void setLoading(boolean isLoading) {
        createAccountButton.setEnabled(!isLoading);
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}