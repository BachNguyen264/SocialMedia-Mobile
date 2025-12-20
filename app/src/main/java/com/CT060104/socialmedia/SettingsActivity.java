package com.CT060104.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import repository.AuthRepository;
import android.content.Intent;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout btnLogout = findViewById(R.id.logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AuthRepository(this).logout();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}