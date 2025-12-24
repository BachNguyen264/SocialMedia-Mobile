package api;

import models.*;
import models.TimelineData;
import models.auth.AuthResponse;
import models.auth.LoginRequest;
import models.post.CreatePostRequest;
import models.post.PostResponse;
import models.auth.RegisterRequest;
import models.user.UpdateUserRequest;
import models.user.UserResponse;
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

    @POST("/api/posts")
    Call<ApiResponse<PostResponse>> createPost(@Body CreatePostRequest request);

    //-------------- USER ----------
    @GET("/api/users/me")
    Call<ApiResponse<UserResponse>> getProfile();
    @PATCH("/api/users/me")
    Call<ApiResponse<UserResponse>> updateProfile(@Body UpdateUserRequest request);
}
