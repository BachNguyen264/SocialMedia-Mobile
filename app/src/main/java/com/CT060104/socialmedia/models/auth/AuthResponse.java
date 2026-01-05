package com.CT060104.socialmedia.models.auth;

import com.google.gson.annotations.SerializedName;
import com.CT060104.socialmedia.models.user.UserResponse;

public class AuthResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private UserResponse user;

    public String getToken() {
        return token;
    }

    public UserResponse getUser() {
        return user;
    }
}