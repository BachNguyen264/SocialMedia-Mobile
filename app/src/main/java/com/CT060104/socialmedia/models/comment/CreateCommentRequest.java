package com.CT060104.socialmedia.models.comment;

import com.google.gson.annotations.SerializedName;

public class CreateCommentRequest {
    @SerializedName("content")
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}
