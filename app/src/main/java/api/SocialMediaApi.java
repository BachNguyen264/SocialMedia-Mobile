package api;

import java.util.List;

import models.ApiResponse;
import models.auth.AuthResponse;
import models.auth.LoginRequest;
import models.post.PostResponse;
import models.auth.RegisterRequest;
import models.user.UserResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface SocialMediaApi {

    // ---------- AUTH ----------
    @POST("/api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

}
