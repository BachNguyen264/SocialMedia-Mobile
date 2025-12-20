package models.post;

import com.google.gson.annotations.SerializedName;

public class CreatePostRequest {
    @SerializedName("content")
    private String content;

    public CreatePostRequest(String content) {
        this.content = content;
    }
}