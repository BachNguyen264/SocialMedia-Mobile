package models.post;

import com.google.gson.annotations.SerializedName;

import models.user.UserResponse;

public class PostResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("content")
    private String content;

    @SerializedName("userId")
    private int userId;

    @SerializedName("user")
    private UserResponse user;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("likesCount")
    private int likesCount;

    @SerializedName("commentsCount")
    private int commentsCount;

    @SerializedName("isLiked")
    private boolean isLiked;

    public PostResponse(int id, String content, int userId, UserResponse user, String createdAt, String updatedAt) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likesCount = 0;
        this.commentsCount = 0;
        this.isLiked = false;
    }

}