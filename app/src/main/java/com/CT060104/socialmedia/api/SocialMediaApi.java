package com.CT060104.socialmedia.api;

import com.CT060104.socialmedia.models.ApiResponse;
import com.CT060104.socialmedia.models.post.TimelineData;
import com.CT060104.socialmedia.models.auth.AuthResponse;
import com.CT060104.socialmedia.models.auth.LoginRequest;
import com.CT060104.socialmedia.models.comment.CommentResponse;
import com.CT060104.socialmedia.models.comment.CreateCommentRequest;
import com.CT060104.socialmedia.models.post.CreatePostRequest;
import com.CT060104.socialmedia.models.post.PostResponse;
import com.CT060104.socialmedia.models.auth.RegisterRequest;
import com.CT060104.socialmedia.models.user.GetMyPostResponse;
import com.CT060104.socialmedia.models.user.UpdateUserRequest;
import com.CT060104.socialmedia.models.user.UserResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface SocialMediaApi {

    // ---------- AUTH ----------
    @POST("/api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    // ---------- POSTS & TIMELINE ----------
    @GET("/api/timeline")
    Call<ApiResponse<TimelineData>> getTimeline(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("/api/posts/{postId}")
    Call<ApiResponse<PostResponse>> getPostById(@Path("postId") int postId);

    @POST("/api/posts")
    Call<ApiResponse<PostResponse>> createPost(@Body CreatePostRequest request);

    //-------------- USER ----------
    @GET("/api/users/me")
    Call<ApiResponse<UserResponse>> getProfile();

    @PATCH("/api/users/me")
    Call<ApiResponse<UserResponse>> updateProfile(@Body UpdateUserRequest request);

    @GET("/api/users/me/posts")
    Call<ApiResponse<GetMyPostResponse>> getMyPosts(
            @Query("page") int page,
            @Query("limit") int limit
    );

    //-----------LIKE--------
    @POST("/api/posts/{postId}/like")
    Call<ApiResponse> likePost(@Path("postId") int postId);

    @DELETE("/api/posts/{postId}/like")
    Call<ApiResponse> unlikePost(@Path("postId") int postId);

    //---------COMMENT--------
    @GET("/api/posts/{postId}/comments")
    Call<ApiResponse<TimelineData>> getPostComments(
            @Path("postId") int postId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/api/posts/{postId}/comments")
    Call<ApiResponse<CommentResponse>> createComment(
            @Path("postId") int postId,
            @Body CreateCommentRequest request
    );
}
