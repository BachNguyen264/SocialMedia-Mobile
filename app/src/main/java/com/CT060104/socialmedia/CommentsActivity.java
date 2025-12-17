package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import java.util.ArrayList;

import models.post.PostResponse;
import repository.AuthRepository;
import repository.PostRepository;
import utils.TokenManager;

public class CommentsActivity extends AppCompatActivity {

    private TextView post_author;
    private TextView post_date;
    private TextView post_content;
    private ImageView post_like;
    private TextView post_likes;
    private TextView post_comments;
    private EditText comment_edittext;
    private Button post_button;
    private ListView comments_list;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private PostRepository postRepository;
    private TokenManager tokenManager;
    private PostResponse currentPost;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Initialize repositories and token manager
        authRepository = new AuthRepository(this);
        postRepository = new PostRepository(this);
        tokenManager = TokenManager.getInstance(this);

        // Check if user is logged in
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Get post ID from intent
        postId = getIntent().getIntExtra("PostID", -1);
        if (postId == -1) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initializeViews();

        // Load post details
        loadPostDetails();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        post_author = findViewById(R.id.post_author);
        post_date = findViewById(R.id.post_date);
        post_content = findViewById(R.id.post_content);
        post_like = findViewById(R.id.post_like);
        post_likes = findViewById(R.id.post_likes);
        post_comments = findViewById(R.id.post_comments);
        comment_edittext = findViewById(R.id.comment_edittext);
        post_button = findViewById(R.id.post);
        comments_list = findViewById(R.id.comments_list);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present
    }

    private void setupClickListeners() {
        post_like.setOnClickListener(v -> {
            if (currentPost != null) {
                toggleLike();
            }
        });

        post_button.setOnClickListener(v -> {
            String commentText = comment_edittext.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(this, "Cannot publish empty comment", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Implement comment creation API
            Toast.makeText(this, "Comments feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadPostDetails() {
        setLoading(true);

        postRepository.getPostById(postId).observe(this, new Observer<PostRepository.ApiResult<PostResponse>>() {
            @Override
            public void onChanged(PostRepository.ApiResult<PostResponse> result) {
                setLoading(false);

                if (result.isSuccess() && result.getData() != null) {
                    currentPost = result.getData();
                    populatePostDetails();
                } else {
                    Toast.makeText(CommentsActivity.this,
                            "Failed to load post: " + result.getErrorMessage(),
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void populatePostDetails() {
        if (currentPost == null) return;

        // Set post content
        if (currentPost.getUser() != null) {
            post_author.setText(currentPost.getUser().getFirstName() + " " + currentPost.getUser().getLastName());
        }
        post_date.setText(currentPost.getFormattedDate());
        post_content.setText(currentPost.getContent());

        // Set like status and count
        updateLikeUI();

        // Set comments count
        updateCommentsCount();

        // Show placeholder for comments
        showCommentsPlaceholder();
    }

    private void updateLikeUI() {
        if (currentPost.isLiked()) {
            post_like.setImageResource(R.drawable.liked);
        } else {
            post_like.setImageResource(R.drawable.like);
        }

        post_likes.setText(getLikesStatus(currentPost.getLikesCount()));
    }

    private void updateCommentsCount() {
        int commentsCount = currentPost.getCommentsCount();
        if (commentsCount < 2) {
            post_comments.setText(commentsCount + " Comment");
        } else {
            post_comments.setText(commentsCount + " Comments");
        }
    }

    private void toggleLike() {
        if (currentPost.isLiked()) {
            // Unlike the post
            postRepository.unlikePost(currentPost.getId()).observe(this, result -> {
                if (result.isSuccess()) {
                    currentPost.setLiked(false);
                    currentPost.setLikesCount(Math.max(0, currentPost.getLikesCount() - 1));
                    updateLikeUI();
                } else {
                    Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Like the post
            postRepository.likePost(currentPost.getId()).observe(this, result -> {
                if (result.isSuccess()) {
                    currentPost.setLiked(true);
                    currentPost.setLikesCount(currentPost.getLikesCount() + 1);
                    updateLikeUI();
                } else {
                    Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showCommentsPlaceholder() {
        // Show placeholder message for comments
        ArrayList<String> placeholderList = new ArrayList<>();
        placeholderList.add("Comments feature will be available soon");
        placeholderList.add("This will show all comments for this post");

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                placeholderList
        );
        comments_list.setAdapter(adapter);
    }

    private String getLikesStatus(int likesCount) {
        if (likesCount < 2) {
            return likesCount + " Like";
        } else {
            return likesCount + " Likes";
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(CommentsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status again and refresh if still logged in
        if (authRepository.isLoggedIn()) {
            if (currentPost == null) {
                loadPostDetails();
            }
        } else {
            navigateToLogin();
        }
    }
}