package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import adapters.PostAdapter;
import repository.PostRepository;
import utils.TokenManager;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private PostRepository postRepository;
    private EditText editPostContent;
    private Button btnPost;
    private ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Kiểm tra login
        if (!TokenManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Init views
        recyclerView = findViewById(R.id.recycler_posts);
        editPostContent = findViewById(R.id.edit_post_content);
        btnPost = findViewById(R.id.btn_post);
        btnSettings = findViewById(R.id.btn_settings);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter();
        recyclerView.setAdapter(postAdapter);

        // Init Repository
        postRepository = new PostRepository(this);

        // Load data
        loadTimeline();

        // Handle Post button
        btnPost.setOnClickListener(v -> {
            String content = editPostContent.getText().toString().trim();
            if (!content.isEmpty()) {
                createNewPost(content);
            } else {
                Toast.makeText(this, "Please write something!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Settings button
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadTimeline() {
        // Lấy 20 bài viết đầu tiên
        postRepository.getTimeline(1, 20).observe(this, result -> {
            if (result.isSuccess()) {
                postAdapter.setPosts(result.getPosts());
            } else {
                Log.e("Home Activity", "Error loading timeline: " + result.getMessage());
                Toast.makeText(this, "Error: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createNewPost(String content) {
        btnPost.setEnabled(false); // Disable nút để tránh spam
        postRepository.createPost(content).observe(this, success -> {
            btnPost.setEnabled(true);
            if (success) {
                Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                editPostContent.setText(""); // Xóa nội dung cũ
                loadTimeline(); // Tải lại danh sách để thấy bài mới
            } else {
                Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        loadTimeline();
    }
}