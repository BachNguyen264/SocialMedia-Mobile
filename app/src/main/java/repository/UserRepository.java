package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import api.ApiClient;
import models.user.UserResponse;
import models.post.PostResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final Context context;

    public UserRepository(Context context) {
        this.context = context;
    }

    public LiveData<ApiResult<UserResponse>> getProfile() {
        MutableLiveData<ApiResult<UserResponse>> result = new MutableLiveData<>();

        ApiClient.getClient(context).getProfile().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult<>(true, response.body(), null));
                    Log.d(TAG, "User profile loaded successfully");
                } else {
                    String errorMessage = "Failed to load user profile";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Get profile API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Get profile network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<UserResponse>> updateProfile(UserResponse user) {
        MutableLiveData<ApiResult<UserResponse>> result = new MutableLiveData<>();

        ApiClient.getClient(context).updateProfile(user).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult<>(true, response.body(), null));
                    Log.d(TAG, "User profile updated successfully");
                } else {
                    String errorMessage = "Failed to update user profile";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Update profile API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Update profile network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<List<UserResponse>>> getFriends() {
        MutableLiveData<ApiResult<List<UserResponse>>> result = new MutableLiveData<>();

        ApiClient.getClient(context).getFriends().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult<>(true, response.body(), null));
                    Log.d(TAG, "Friends loaded successfully: " + response.body().size() + " friends");
                } else {
                    String errorMessage = "Failed to load friends";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Get friends API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Get friends network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<Void>> addFriend(int userId) {
        MutableLiveData<ApiResult<Void>> result = new MutableLiveData<>();

        ApiClient.getClient(context).addFriend(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResult<>(true, null, null));
                    Log.d(TAG, "Friend added successfully: " + userId);
                } else {
                    String errorMessage = "Failed to add friend";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Add friend API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Add friend network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<Void>> removeFriend(int userId) {
        MutableLiveData<ApiResult<Void>> result = new MutableLiveData<>();

        ApiClient.getClient(context).removeFriend(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResult<>(true, null, null));
                    Log.d(TAG, "Friend removed successfully: " + userId);
                } else {
                    String errorMessage = "Failed to remove friend";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Remove friend API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Remove friend network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<List<PostResponse>>> getUserPosts(int userId) {
        MutableLiveData<ApiResult<List<PostResponse>>> result = new MutableLiveData<>();

        ApiClient.getClient(context).getUserPosts(userId).enqueue(new Callback<List<PostResponse>>() {
            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult<>(true, response.body(), null));
                    Log.d(TAG, "User posts loaded successfully: " + response.body().size() + " posts");
                } else {
                    String errorMessage = "Failed to load user posts";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Get user posts API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<PostResponse>> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Get user posts network failure", t);
            }
        });

        return result;
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

    // Generic API result wrapper (reusing from PostRepository)
    public static class ApiResult<T> {
        private final boolean success;
        private final T data;
        private final String errorMessage;

        public ApiResult(boolean success, T data, String errorMessage) {
            this.success = success;
            this.data = data;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}