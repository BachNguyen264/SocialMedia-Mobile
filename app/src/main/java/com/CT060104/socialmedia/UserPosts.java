package com.CT060104.socialmedia;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adapters.PostAdapter;
import repository.PostRepository;

public class UserPosts extends AppCompatActivity {

    private RecyclerView posts_list;
    private PostRepository postRepository;
    private PostAdapter postAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_user_list);
        progressBar = findViewById(R.id.progressBar);
        TextView title = findViewById(R.id.title);
        title.setText("Posts");
        posts_list = findViewById(R.id.posts_list);
        posts_list.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter();
        posts_list.setAdapter(postAdapter);

        postRepository = new PostRepository(this);
        loadPosts();
    }

    private void loadPosts() {
        setLoading(true);
        postRepository.getMyPosts(1, 20).observe(this, result -> {
            setLoading(false);
            if (result.isSuccess()) {
                postAdapter.setPosts(result.getPosts());
            }else {
                Log.e("User Posts Activity", "Error loading posts: " + result.getMessage());
                Toast.makeText(this, "Error: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();
        loadPosts();
    }
}