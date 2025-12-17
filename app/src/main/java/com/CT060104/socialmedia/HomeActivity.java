package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
// import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // Commented out - will add when layout supports it

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import models.post.PostResponse;
import repository.AuthRepository;
import repository.PostRepository;
import utils.TokenManager;
import Model.PostsAdapter;

public class HomeActivity extends AppCompatActivity {

    private ListView posts_list;
    private EditText post_edittext;
    private Button postButton;
    private ImageView modifyButton;
    private ProgressBar progressBar;
    // private SwipeRefreshLayout swipeRefreshLayout; // Commented out - will add when layout supports it

    private PostsAdapter postsAdapter;
    private PostRepository postRepository;
    private AuthRepository authRepository;
    private TokenManager tokenManager;
    private ArrayList<PostResponse> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize repositories and token manager
        postRepository = new PostRepository(this);
        authRepository = new AuthRepository(this);
        tokenManager = TokenManager.getInstance(this);

        // Check if user is logged in
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Initialize views
        modifyButton = findViewById(R.id.modify);
        post_edittext = findViewById(R.id.post_edittext);
        postButton = findViewById(R.id.post);
        posts_list = findViewById(R.id.posts_list);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        // Add swipe refresh if layout supports it
        // swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        // if (swipeRefreshLayout != null) {
        //     swipeRefreshLayout.setOnRefreshListener(this::refreshTimeline);
        //     swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
        //             android.R.color.holo_green_light,
        //             android.R.color.holo_orange_light,
        //             android.R.color.holo_red_light);
        // }

        posts = new ArrayList<>();

        // Set up click listeners
        setupClickListeners();

        // Load initial data
        refreshTimeline();
    }

    private void setupClickListeners() {
        modifyButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        postButton.setOnClickListener(v -> {
            String content = post_edittext.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Cannot publish empty post", Toast.LENGTH_SHORT).show();
                return;
            }
            createPost(content);
        });
    }

    private void refreshTimeline() {
        setLoading(true);

        postRepository.getPosts().observe(this, new Observer<PostRepository.ApiResult<List<PostResponse>>>() {
            @Override
            public void onChanged(PostRepository.ApiResult<List<PostResponse>> result) {
                setLoading(false);
                // if (swipeRefreshLayout != null) {
                //     swipeRefreshLayout.setRefreshing(false);
                // }

                if (result.isSuccess()) {
                    posts = new ArrayList<>(result.getData());
                    updatePostsAdapter();
                } else {
                    Toast.makeText(HomeActivity.this,
                            "Failed to load posts: " + result.getErrorMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createPost(String content) {
        setLoading(true);
        postButton.setEnabled(false);

        postRepository.createPost(content).observe(this, new Observer<PostRepository.ApiResult<PostResponse>>() {
            @Override
            public void onChanged(PostRepository.ApiResult<PostResponse> result) {
                setLoading(false);
                postButton.setEnabled(true);

                if (result.isSuccess()) {
                    Toast.makeText(HomeActivity.this, "Posted successfully", Toast.LENGTH_SHORT).show();
                    post_edittext.setText("");

                    // Add new post to the beginning of the list
                    if (result.getData() != null) {
                        posts.add(0, result.getData());
                        updatePostsAdapter();
                    }
                } else {
                    Toast.makeText(HomeActivity.this,
                            "Failed to create post: " + result.getErrorMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updatePostsAdapter() {
        if (postsAdapter == null) {
            postsAdapter = new PostsAdapter(posts, this);
            posts_list.setAdapter(postsAdapter);
        } else {
            postsAdapter.updatePosts(posts);
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status again and refresh if still logged in
        if (authRepository.isLoggedIn()) {
            refreshTimeline();
        } else {
            navigateToLogin();
        }
    }
}