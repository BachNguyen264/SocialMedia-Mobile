package api;

import java.util.List;

import models.auth.AuthResponse;
import models.auth.LoginRequest;
import models.post.PostResponse;
import models.auth.RegisterRequest;
import models.user.UserResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface SocialMediaApi {

    // Authentication endpoints
    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    // User endpoints
    @GET("/api/users/me")
    Call<UserResponse> getProfile();

    @PUT("/api/users/me")
    Call<UserResponse> updateProfile(@Body UserResponse user);

    @GET("/api/users/{userId}/posts")
    Call<List<PostResponse>> getUserPosts(@Path("userId") int userId);

    // Post endpoints
    @GET("/api/posts")
    Call<List<PostResponse>> getPosts();

    @POST("/api/posts")
    Call<PostResponse> createPost(@Body CreatePostRequest request);

    @GET("/api/posts/{postId}")
    Call<PostResponse> getPostById(@Path("postId") int postId);

    @DELETE("/api/posts/{postId}")
    Call<Void> deletePost(@Path("postId") int postId);

    // Like endpoints
    @POST("/api/likes")
    Call<Void> likePost(@Body LikeRequest request);

    @DELETE("/api/likes/{postId}")
    Call<Void> unlikePost(@Path("postId") int postId);

    // Friend endpoints
    @GET("/api/friends")
    Call<List<UserResponse>> getFriends();

    @POST("/api/friends/{userId}")
    Call<Void> addFriend(@Path("userId") int userId);

    @DELETE("/api/friends/{userId}")
    Call<Void> removeFriend(@Path("userId") int userId);

    // Timeline endpoint
    @GET("/api/timeline")
    Call<List<PostResponse>> getTimeline();

    // Request models for nested objects
    class CreatePostRequest {
        public String content;

        public CreatePostRequest(String content) {
            this.content = content;
        }
    }

    class LikeRequest {
        public int postId;

        public LikeRequest(int postId) {
            this.postId = postId;
        }
    }
}