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

import models.post.PostResponse;
import repository.AuthRepository;
import repository.UserRepository;
import utils.TokenManager;
import Model.PostsAdapter;

public class UserPosts extends AppCompatActivity {

    private TextView title;
    private ListView posts_list;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private TokenManager tokenManager;
    private PostsAdapter postsAdapter;
    private ArrayList<PostResponse> userPosts;
    private int currentUserId;

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

        // Get current user ID
        currentUserId = tokenManager.getUserId();

        // Initialize views
        title = findViewById(R.id.title);
        posts_list = findViewById(R.id.posts_list);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        title.setText("My Posts");
        userPosts = new ArrayList<>();

        // Load user posts
        loadUserPosts();
    }

    private void loadUserPosts() {
        setLoading(true);

        userRepository.getUserPosts(currentUserId).observe(this, new Observer<UserRepository.ApiResult<List<PostResponse>>>() {
            @Override
            public void onChanged(UserRepository.ApiResult<List<PostResponse>> result) {
                setLoading(false);

                if (result.isSuccess()) {
                    userPosts = new ArrayList<>(result.getData());
                    updatePostsAdapter();
                } else {
                    Toast.makeText(UserPosts.this,
                            "Failed to load posts: " + result.getErrorMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updatePostsAdapter() {
        if (postsAdapter == null) {
            postsAdapter = new PostsAdapter(userPosts, this);
            posts_list.setAdapter(postsAdapter);
        } else {
            postsAdapter.updatePosts(userPosts);
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(UserPosts.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status again and refresh if still logged in
        if (authRepository.isLoggedIn()) {
            loadUserPosts();
        } else {
            navigateToLogin();
        }
    }
}