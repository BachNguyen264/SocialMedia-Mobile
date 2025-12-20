package models.post;

import com.google.gson.annotations.SerializedName;
import models.user.UserResponse;

public class PostResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("commentCount")
    private int commentCount;

    @SerializedName("likeCount")
    private int likeCount;

    @SerializedName("userHasLiked")
    private Boolean userHasLiked;

    @SerializedName("user")
    private UserResponse user;

    // Getters
    public int getId() { return id; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public int getCommentCount() { return commentCount; }
    public int getLikeCount() { return likeCount; }
    public boolean isUserHasLiked() { return userHasLiked; }
    public UserResponse getUser() { return user; }
}