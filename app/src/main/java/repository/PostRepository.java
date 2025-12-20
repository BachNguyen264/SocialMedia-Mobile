package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import api.ApiClient;
import models.ApiResponse;
import models.TimelineData;
import models.post.CreatePostRequest;
import models.post.PostResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {
    private static final String TAG = "PostRepository";
    private final Context context;
    private final Gson gson;

    public PostRepository(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public LiveData<PostResult> getTimeline(int page, int limit) {
        MutableLiveData<PostResult> result = new MutableLiveData<>();

        ApiClient.getClient(context).getTimeline(page, limit).enqueue(new Callback<ApiResponse<TimelineData>>() {
            @Override
            public void onResponse(Call<ApiResponse<TimelineData>> call, Response<ApiResponse<TimelineData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TimelineData> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        result.setValue(new PostResult(true, "Success", apiResponse.getData().getPosts()));
                    } else {
                        result.setValue(new PostResult(false, apiResponse.getError(), null));
                    }
                } else {
                    result.setValue(new PostResult(false, "Failed to load timeline", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TimelineData>> call, Throwable t) {
                result.setValue(new PostResult(false, t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Boolean> createPost(String content) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        CreatePostRequest request = new CreatePostRequest(content);
        ApiClient.getClient(context).createPost(request).enqueue(new Callback<ApiResponse<PostResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostResponse>> call, Response<ApiResponse<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(true);
                } else {
                    result.setValue(false);
                    Log.e(TAG, "Create post failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostResponse>> call, Throwable t) {
                result.setValue(false);
                Log.e(TAG, "Create post network error", t);
            }
        });

        return result;
    }

    public static class PostResult {
        private final boolean success;
        private final String message;
        private final List<PostResponse> posts;

        public PostResult(boolean success, String message, List<PostResponse> posts) {
            this.success = success;
            this.message = message;
            this.posts = posts;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<PostResponse> getPosts() { return posts; }
    }
}