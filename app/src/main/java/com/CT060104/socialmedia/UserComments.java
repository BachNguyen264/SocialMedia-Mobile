package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import repository.AuthRepository;
import utils.TokenManager;

public class UserComments extends AppCompatActivity {

    private ListView listView;
    //private TextView titleText;
    //private ProgressBar progressBar;

    private AuthRepository authRepository;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);

        // Initialize repositories and token manager
        authRepository = new AuthRepository(this);
        tokenManager = TokenManager.getInstance(this);

        // Check if user is logged in
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Initialize views
        listView = findViewById(R.id.list);
        //titleText = findViewById(R.id.title);
        //progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        //titleText.setText("My Comments");

        // TODO: Implement comment API endpoints and models
        // For now, show placeholder message
        showPlaceholderMessage();
    }

    private void showPlaceholderMessage() {
        // Create a simple adapter with placeholder text
        ArrayList<String> placeholderList = new ArrayList<>();
        placeholderList.add("Comments functionality will be available soon");
        placeholderList.add("This feature requires additional API endpoints for comments");

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                placeholderList
        );
        listView.setAdapter(adapter);

        Toast.makeText(this, "Comments feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(UserComments.this, LoginActivity.class);
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