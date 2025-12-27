package models.post;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import models.Pagination;

public class TimelineData {
    @SerializedName("posts")
    private List<PostResponse> posts;

    @SerializedName("pagination")
    private Pagination pagination;

    public List<PostResponse> getPosts() { return posts; }
    public Pagination getPagination() { return pagination; }
}