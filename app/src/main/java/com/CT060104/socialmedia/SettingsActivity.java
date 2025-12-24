package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import repository.AuthRepository;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private TextView user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout profile = findViewById(R.id.profile);
        LinearLayout posts = findViewById(R.id.posts);
        LinearLayout comments = findViewById(R.id.post_comments);
        LinearLayout likes = findViewById(R.id.post_likes);
        LinearLayout friends = findViewById(R.id.friends);
        LinearLayout password = findViewById(R.id.password);
        LinearLayout btnLogout = findViewById(R.id.logout);

        user_name = findViewById(R.id.user_name);
        SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);
        String name = prefs.getString("display_name", "User");
        user_name.setText(name);

        profile.setOnClickListener(v-> {
            Intent intent = new Intent(SettingsActivity.this, ModifyProfile.class);
            startActivity(intent);
        });

        posts.setOnClickListener(v-> {
            Intent intent = new Intent(SettingsActivity.this, UserPosts.class);
            startActivity(intent);
        });

        comments.setOnClickListener(v-> {
            Intent intent = new Intent(SettingsActivity.this, UserComments.class);
            startActivity(intent);
        });

        likes.setOnClickListener(v-> {
            Intent intent = new Intent(SettingsActivity.this, UserLikes.class);
            startActivity(intent);
        });

        friends.setOnClickListener(v-> {
            Intent intent = new Intent(SettingsActivity.this, FriendsActivity.class);
            startActivity(intent);
        });

        password.setOnClickListener(v-> {
            Intent intent = new Intent(SettingsActivity.this, ChangePassword.class);
            startActivity(intent);
        });

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AuthRepository(this).logout();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                prefs.edit().clear().apply();
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUsername();
    }

    private void refreshUsername() {
        SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);
        String name = prefs.getString("display_name", "User");
        user_name.setText(name);
    }
}