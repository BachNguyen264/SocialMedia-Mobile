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
import repository.PostRepository;
import utils.TokenManager;
import Model.PostsAdapter;

public class UserLikes extends AppCompatActivity {

    private TextView title;
    private ListView posts_list;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private PostRepository postRepository;
    private TokenManager tokenManager;
    private PostsAdapter postsAdapter;
    private ArrayList<PostResponse> likedPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_user_list);

        // Initialize repositories and token manager
        authRepository = new AuthRepository(this);
        postRepository = new PostRepository(this);
        tokenManager = TokenManager.getInstance(this);

        // Check if user is logged in
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Initialize views
        title = findViewById(R.id.title);
        posts_list = findViewById(R.id.posts_list);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        title.setText("Liked Posts");
        likedPosts = new ArrayList<>();

        // Load posts and filter for liked ones
        loadLikedPosts();
    }

    private void loadLikedPosts() {
        setLoading(true);

        postRepository.getPosts().observe(this, new Observer<PostRepository.ApiResult<List<PostResponse>>>() {
            @Override
            public void onChanged(PostRepository.ApiResult<List<PostResponse>> result) {
                setLoading(false);

                if (result.isSuccess()) {
                    // Filter posts for liked ones
                    likedPosts.clear();
                    for (PostResponse post : result.getData()) {
                        if (post.isLiked()) {
                            likedPosts.add(post);
                        }
                    }
                    updatePostsAdapter();

                    if (likedPosts.isEmpty()) {
                        Toast.makeText(UserLikes.this, "No liked posts found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserLikes.this,
                            "Failed to load posts: " + result.getErrorMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updatePostsAdapter() {
        if (postsAdapter == null) {
            postsAdapter = new PostsAdapter(likedPosts, this);
            posts_list.setAdapter(postsAdapter);
        } else {
            postsAdapter.updatePosts(likedPosts);
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(UserLikes.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status again and refresh if still logged in
        if (authRepository.isLoggedIn()) {
            loadLikedPosts();
        } else {
            navigateToLogin();
        }
    }
}