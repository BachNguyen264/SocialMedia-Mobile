package api;

import models.*;
import models.post.TimelineData;
import models.auth.AuthResponse;
import models.auth.LoginRequest;
import models.comment.CommentResponse;
import models.comment.CreateCommentRequest;
import models.post.CreatePostRequest;
import models.post.PostResponse;
import models.auth.RegisterRequest;
import models.user.GetMyPostResponse;
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
