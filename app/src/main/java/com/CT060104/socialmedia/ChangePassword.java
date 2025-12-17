package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import repository.AuthRepository;
import utils.TokenManager;

public class ChangePassword extends AppCompatActivity {

    private EditText old_password;
    private EditText password;
    private EditText confirm_password;
    private Button submit;

    private AuthRepository authRepository;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize repositories and token manager
        authRepository = new AuthRepository(this);
        tokenManager = TokenManager.getInstance(this);

        // Check if user is logged in
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Initialize views
        old_password = findViewById(R.id.old_password);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(v-> {
            if (validateInputs()) {
                changePassword();
            }
        });
    }

    private boolean validateInputs() {
        String oldPass = old_password.getText().toString().trim();
        String newPass = password.getText().toString().trim();
        String confirmPass = confirm_password.getText().toString().trim();

        if (oldPass.isEmpty()) {
            old_password.setError("Please enter your old password");
            old_password.requestFocus();
            return false;
        }

        if (newPass.isEmpty()) {
            password.setError("Please enter your new password");
            password.requestFocus();
            return false;
        }

        if (newPass.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return false;
        }

        if (confirmPass.isEmpty()) {
            confirm_password.setError("Please confirm your password");
            confirm_password.requestFocus();
            return false;
        }

        if (!newPass.equals(confirmPass)) {
            confirm_password.setError("Passwords don't match");
            confirm_password.requestFocus();
            return false;
        }

        return true;
    }

    private void changePassword() {
        String oldPass = old_password.getText().toString().trim();
        String newPass = password.getText().toString().trim();

        // Disable button to prevent multiple clicks
        submit.setEnabled(false);
        submit.setText("Changing...");

        // TODO: Implement password change API endpoint
        // For now, show a message that the feature is coming soon
        Toast.makeText(this, "Password change feature will be available soon", Toast.LENGTH_LONG).show();

        // Re-enable button
        submit.setEnabled(true);
        submit.setText("Change Password");

        // For now, we'll just validate and show a success message
        // In a real implementation, you would call the API:
        /*
        authRepository.changePassword(oldPass, newPass).observe(this, result -> {
            submit.setEnabled(true);
            submit.setText("Change Password");

            if (result.isSuccess()) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to change password: " + result.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });
        */
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status again
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
        }
    }
}