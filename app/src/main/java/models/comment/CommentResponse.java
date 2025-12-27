package models.comment;

import com.google.gson.annotations.SerializedName;

import models.user.UserResponse;

public class CommentResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("content")
    private String content;
    @SerializedName("postId")
    private int postId;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("user")
    private UserResponse user;

    // Getters
    public int getId() { return id; }
    public String getContent() { return content; }
    public int getPostId() { return postId; }
    public String getCreatedAt() { return createdAt; }
    public UserResponse getUser() { return user; }
}
