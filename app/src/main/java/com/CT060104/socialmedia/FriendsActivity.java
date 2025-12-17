package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import models.user.UserResponse;
import repository.AuthRepository;
import repository.UserRepository;
import utils.TokenManager;
import Model.FriendsAdapter;

public class FriendsActivity extends AppCompatActivity {

    private TextView title;
    private ListView listView;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private TokenManager tokenManager;
    private FriendsAdapter friendsAdapter;
    private ArrayList<UserResponse> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_user_list);

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
        title = findViewById(R.id.title);
        listView = findViewById(R.id.posts_list);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        title.setText("Friends");
        friends = new ArrayList<>();

        // Load friends data
        loadFriends();
    }

    private void loadFriends() {
        setLoading(true);

        userRepository.getFriends().observe(this, new Observer<UserRepository.ApiResult<List<UserResponse>>>() {
            @Override
            public void onChanged(UserRepository.ApiResult<List<UserResponse>> result) {
                setLoading(false);

                if (result.isSuccess()) {
                    friends = new ArrayList<>(result.getData());
                    updateFriendsAdapter();
                } else {
                    Toast.makeText(FriendsActivity.this,
                            "Failed to load friends: " + result.getErrorMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateFriendsAdapter() {
        if (friendsAdapter == null) {
            friendsAdapter = new FriendsAdapter(friends, this);
            listView.setAdapter(friendsAdapter);
        } else {
            friendsAdapter.updateFriends(friends);
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(FriendsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status again and refresh if still logged in
        if (authRepository.isLoggedIn()) {
            loadFriends();
        } else {
            navigateToLogin();
        }
    }
}