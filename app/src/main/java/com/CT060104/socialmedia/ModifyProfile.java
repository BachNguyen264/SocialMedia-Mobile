package com.CT060104.socialmedia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import repository.UserRepository;

public class ModifyProfile extends AppCompatActivity {

    private UserRepository userRepository;
    private EditText first_name;
    private EditText last_name;
    private TextView email;
    private Button submit;
    private String first_name_old;
    private String last_name_old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);

        userRepository = new UserRepository(this);
        loadUserProfile();
        submit.setOnClickListener(v -> handleUpdateProfile());
    }

    private void loadUserProfile() {
        userRepository.getProfile().observe(this, userResult -> {
            if (userResult.isSuccess() && userResult.getUser() != null) {
                first_name_old = userResult.getUser().getFirstName();
                last_name_old = userResult.getUser().getLastName();
                first_name.setText(first_name_old);
                last_name.setText(last_name_old);
                email.setText(userResult.getUser().getEmail());
            } else {
                Toast.makeText(this, "Không thể tải thông tin: " + userResult.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateProfile() {
        String firstName = first_name.getText().toString().trim();
        String lastName = last_name.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Vui lòng không để trống tên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (firstName.equals(first_name_old) && lastName.equals(last_name_old)){
            Toast.makeText(this, "Tên cũ giống với tên mới", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable nút để tránh click nhiều lần
        submit.setEnabled(false);
        submit.setText("Updating...");

        userRepository.updateProfile(firstName, lastName).observe(this, result -> {
            submit.setEnabled(true);
            submit.setText("Submit"); // Hoặc text gốc của nút

            if (result.isSuccess()) {
                SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);
                prefs.edit()
                        .putString("display_name",
                                result.getUser().getFirstName() + " " + result.getUser().getLastName())
                        .apply();
                Toast.makeText(ModifyProfile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng màn hình này để quay lại màn hình trước (ví dụ: Settings)
            } else {
                Toast.makeText(ModifyProfile.this, "Lỗi: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}