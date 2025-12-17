package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import api.ApiClient;
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

    public AuthRepository(Context context) {
        this.context = context;
        this.tokenManager = TokenManager.getInstance(context);
    }

    public LiveData<AuthResult> login(String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiClient.getClient(context).login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        // Save token and user info
                        tokenManager.saveToken(authResponse.getToken());
                        if (authResponse.getUser() != null) {
                            tokenManager.saveUserInfo(
                                    authResponse.getUser().getId(),
                                    authResponse.getUser().getEmail()
                            );
                        }
                        result.setValue(new AuthResult(true, authResponse.getMessage(), authResponse.getUser()));
                        Log.d(TAG, "Login successful");
                    } else {
                        result.setValue(new AuthResult(false, authResponse.getMessage(), null));
                        Log.w(TAG, "Login failed: " + authResponse.getMessage());
                    }
                } else {
                    String errorMessage = "Login failed. Please check your credentials.";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            // Try to parse error message from response
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new AuthResult(false, errorMessage, null));
                    Log.e(TAG, "Login API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new AuthResult(false, errorMessage, null));
                Log.e(TAG, "Login network failure", t);
            }
        });

        return result;
    }

    public LiveData<AuthResult> register(String firstName, String lastName, String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);
        ApiClient.getClient(context).register(registerRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        // Save token and user info for auto-login after registration
                        tokenManager.saveToken(authResponse.getToken());
                        if (authResponse.getUser() != null) {
                            Log.d(TAG, "Saving User ID: " + authResponse.getUser().getId()); // Kiểm tra xem ID có > -1 không
                            tokenManager.saveUserInfo(
                                    authResponse.getUser().getId(),
                                    authResponse.getUser().getEmail()
                            );
                        } else {
                            Log.e(TAG, "User object is NULL in response!");
                        }
                        result.setValue(new AuthResult(true, authResponse.getMessage(), authResponse.getUser()));
                        Log.d(TAG, "Registration successful");
                    } else {
                        result.setValue(new AuthResult(false, authResponse.getMessage(), null));
                        Log.w(TAG, "Registration failed: " + authResponse.getMessage());
                    }
                } else {
                    String errorMessage = "Registration failed. Please try again.";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            // Try to parse error message from response
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new AuthResult(false, errorMessage, null));
                    Log.e(TAG, "Registration API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new AuthResult(false, errorMessage, null));
                Log.e(TAG, "Registration network failure", t);
            }
        });

        return result;
    }

    public void logout() {
        tokenManager.clearToken();
        Log.d(TAG, "User logged out");
    }

    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }

    private String extractErrorMessage(String errorBody) {
        try {
            // Try to extract error message from JSON response
            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.util.Map<String, Object> errorMap = gson.fromJson(errorBody, java.util.Map.class);
            if (errorMap.containsKey("message")) {
                return (String) errorMap.get("message");
            } else if (errorMap.containsKey("error")) {
                return (String) errorMap.get("error");
            }
        } catch (Exception e) {
            // If parsing fails, return a generic error message
            Log.e(TAG, "Failed to parse error message", e);
        }
        return "An error occurred. Please try again.";
    }

    // Result wrapper for authentication operations
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