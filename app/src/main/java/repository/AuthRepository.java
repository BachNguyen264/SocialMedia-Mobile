package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import api.ApiClient;
import models.ApiResponse;
import models.auth.AuthResponse;
import models.auth.LoginRequest;
import models.auth.RegisterRequest;
import models.user.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.TokenManager;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final Context context;
    private final TokenManager tokenManager;
    private final Gson gson;

    public AuthRepository(Context context) {
        this.context = context;
        this.tokenManager = TokenManager.getInstance(context);
        this.gson = new Gson();
    }

    public LiveData<AuthResult> login(String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        LoginRequest loginRequest = new LoginRequest(email, password);

        // CHÚ Ý: Callback bây giờ trả về ApiResponse<AuthResponse> thay vì AuthResponse trực tiếp
        ApiClient.getClient(context).login(loginRequest).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                handleAuthResponse(response, result, "Login");
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                handleNetworkFailure(t, result, "Login");
            }
        });

        return result;
    }

    public LiveData<AuthResult> register(String firstName, String lastName, String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);

        // CHÚ Ý: Callback bây giờ trả về ApiResponse<AuthResponse>
        ApiClient.getClient(context).register(registerRequest).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                handleAuthResponse(response, result, "Registration");
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                handleNetworkFailure(t, result, "Registration");
            }
        });

        return result;
    }

    // Hàm xử lý chung cho cả Login và Register để tránh lặp code
    private void handleAuthResponse(Response<ApiResponse<AuthResponse>> response, MutableLiveData<AuthResult> result, String action) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<AuthResponse> apiResponse = response.body();

            // Kiểm tra field "success": true từ backend
            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                AuthResponse authData = apiResponse.getData();

                // Lưu token
                tokenManager.saveToken(authData.getToken());

                // Lưu thông tin user
                if (authData.getUser() != null) {
                    tokenManager.saveUserInfo(
                            authData.getUser().getId(),
                            authData.getUser().getEmail()
                    );
                }

                result.setValue(new AuthResult(true, "Success", authData.getUser()));
                Log.d(TAG, action + " successful");
            } else {
                // Backend trả về 200 OK nhưng success: false
                String msg = apiResponse.getError() != null ? apiResponse.getError() : "Unknown error";
                result.setValue(new AuthResult(false, msg, null));
                Log.w(TAG, action + " failed: " + msg);
            }
        } else {
            // Xử lý lỗi 4xx, 5xx
            String errorMessage = action + " failed. Please check your credentials.";
            try {
                if (response.errorBody() != null) {
                    String errorBodyString = response.errorBody().string();
                    // Parse error body theo format chuẩn của backend
                    ApiResponse<AuthResponse> errorResponse = parseErrorBody(errorBodyString);
                    if (errorResponse != null && errorResponse.getError() != null) {
                        errorMessage = errorResponse.getError();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing error response", e);
            }
            result.setValue(new AuthResult(false, errorMessage, null));
            Log.e(TAG, action + " API error: " + response.code() + " - " + errorMessage);
        }
    }

    private void handleNetworkFailure(Throwable t, MutableLiveData<AuthResult> result, String action) {
        String errorMessage = "Network error. Please check your connection.";
        if (t.getMessage() != null) {
            if (t.getMessage().contains("timeout")) {
                errorMessage = "Connection timeout. Please try again.";
            } else if (t.getMessage().contains("UnknownHost")) {
                errorMessage = "Server not reachable. Please check your internet connection.";
            }
        }
        result.setValue(new AuthResult(false, errorMessage, null));
        Log.e(TAG, action + " network failure", t);
    }

    private ApiResponse<AuthResponse> parseErrorBody(String errorBody) {
        try {
            Type type = new TypeToken<ApiResponse<AuthResponse>>() {}.getType();
            return gson.fromJson(errorBody, type);
        } catch (Exception e) {
            return null;
        }
    }

    public void logout() {
        tokenManager.clearToken();
        Log.d(TAG, "User logged out");
    }

    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final UserResponse user;

        public AuthResult(boolean success, String message, UserResponse user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public UserResponse getUser() {
            return user;
        }
    }
}