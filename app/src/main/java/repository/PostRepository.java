package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import api.ApiClient;
import api.SocialMediaApi;
import models.post.PostResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {
    private static final String TAG = "PostRepository";
    private final Context context;

    public PostRepository(Context context) {
        this.context = context;
    }

    public LiveData<ApiResult<List<PostResponse>>> getPosts() {
        MutableLiveData<ApiResult<List<PostResponse>>> result = new MutableLiveData<>();

        ApiClient.getClient(context).getPosts().enqueue(new Callback<List<PostResponse>>() {
            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult<>(true, response.body(), null));
                    Log.d(TAG, "Posts loaded successfully: " + response.body().size() + " posts");
                } else {
                    String errorMessage = "Failed to load posts";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Get posts API error: " + response.code() + " - " + errorMessage);
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
                Log.e(TAG, "Get posts network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<PostResponse>> createPost(String content) {
        MutableLiveData<ApiResult<PostResponse>> result = new MutableLiveData<>();

        SocialMediaApi.CreatePostRequest request = new SocialMediaApi.CreatePostRequest(content);
        ApiClient.getClient(context).createPost(request).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult<>(true, response.body(), null));
                    Log.d(TAG, "Post created successfully");
                } else {
                    String errorMessage = "Failed to create post";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Create post API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Create post network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<PostResponse>> getPostById(int postId) {
        MutableLiveData<ApiResult<PostResponse>> result = new MutableLiveData<>();

        ApiClient.getClient(context).getPostById(postId).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult<>(true, response.body(), null));
                    Log.d(TAG, "Post loaded successfully: " + postId);
                } else {
                    String errorMessage = "Failed to load post";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Get post API error: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                String errorMessage = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout")) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else if (t.getMessage().contains("UnknownHost")) {
                        errorMessage = "Server not reachable. Please check your internet connection.";
                    }
                }
                result.setValue(new ApiResult<>(false, null, errorMessage));
                Log.e(TAG, "Get post network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<Void>> deletePost(int postId) {
        MutableLiveData<ApiResult<Void>> result = new MutableLiveData<>();

        ApiClient.getClient(context).deletePost(postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResult<>(true, null, null));
                    Log.d(TAG, "Post deleted successfully: " + postId);
                } else {
                    String errorMessage = "Failed to delete post";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Delete post API error: " + response.code() + " - " + errorMessage);
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
                Log.e(TAG, "Delete post network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<Void>> likePost(int postId) {
        MutableLiveData<ApiResult<Void>> result = new MutableLiveData<>();

        SocialMediaApi.LikeRequest request = new SocialMediaApi.LikeRequest(postId);
        ApiClient.getClient(context).likePost(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResult<>(true, null, null));
                    Log.d(TAG, "Post liked successfully: " + postId);
                } else {
                    String errorMessage = "Failed to like post";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Like post API error: " + response.code() + " - " + errorMessage);
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
                Log.e(TAG, "Like post network failure", t);
            }
        });

        return result;
    }

    public LiveData<ApiResult<Void>> unlikePost(int postId) {
        MutableLiveData<ApiResult<Void>> result = new MutableLiveData<>();

        ApiClient.getClient(context).unlikePost(postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResult<>(true, null, null));
                    Log.d(TAG, "Post unliked successfully: " + postId);
                } else {
                    String errorMessage = "Failed to unlike post";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage = extractErrorMessage(errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    result.setValue(new ApiResult<>(false, null, errorMessage));
                    Log.e(TAG, "Unlike post API error: " + response.code() + " - " + errorMessage);
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
                Log.e(TAG, "Unlike post network failure", t);
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

    // Generic API result wrapper
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