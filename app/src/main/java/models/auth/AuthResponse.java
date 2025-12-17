package models.auth;

import com.google.gson.annotations.SerializedName;

import models.user.UserResponse;

public class AuthResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Thay vì hứng token trực tiếp, ta hứng cục "data"
    @SerializedName("data")
    private DataContainer data;

    // --- Constructor & Getters ---

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // QUAN TRỌNG: Sửa getter này để lấy token từ bên trong cục data
    // Giúp code bên ngoài (AuthRepository) không cần sửa gì thêm
    public String getToken() {
        if (data != null) {
            return data.token;
        }
        return null;
    }

    public UserResponse getUser() {
        if (data != null) {
            return data.user;
        }
        return null;
    }

    // --- Class con để hứng dữ liệu bên trong "data" ---
    private static class DataContainer {
        @SerializedName("token")
        private String token;

        @SerializedName("user")
        private UserResponse user;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", data=" + (data != null ? "HAS_DATA" : "NULL") +
                '}';
    }
}