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

    public PostResponse() {}

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getFormattedDate() {
        if (createdAt == null) return "";

        try {
            // Parse ISO 8601 format and format for display
            java.time.format.DateTimeFormatter inputFormatter =
                    java.time.format.DateTimeFormatter.ISO_DATE_TIME;
            java.time.format.DateTimeFormatter outputFormatter =
                    java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");

            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(createdAt, inputFormatter);
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            // Fallback to simple string manipulation if parsing fails
            return createdAt.substring(0, Math.min(createdAt.length(), 16));
        }
    }

    @Override
    public String toString() {
        return "PostResponse{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", user=" + user +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", likesCount=" + likesCount +
                ", commentsCount=" + commentsCount +
                ", isLiked=" + isLiked +
                '}';
    }
}