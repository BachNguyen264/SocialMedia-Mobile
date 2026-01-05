package com.CT060104.socialmedia.models.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.CT060104.socialmedia.models.Pagination;
import com.CT060104.socialmedia.models.post.PostResponse;

public class GetMyPostResponse {
    @SerializedName("posts")
    private List<PostResponse> posts;

    @SerializedName("pagination")
    private Pagination pagination;

    public List<PostResponse> getPosts() { return posts; }
    public Pagination getPagination() { return pagination; }
}
