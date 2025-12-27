package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import api.ApiClient;
import models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeRepository {
    private static final String TAG = "LikeRepository";
    private final Context context;

    public LikeRepository(Context context){
        this.context = context;
    }

    public LiveData<Boolean> likePost(int postId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        ApiClient.getClient(context).likePost(postId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(true);
                } else {
                    result.setValue(false);
                    Log.e(TAG, "Like post failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                result.setValue(false);
                Log.e(TAG, "Network error", t);
            }
        });

        return result;
    }

    public LiveData<Boolean> unlikePost(int postId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        ApiClient.getClient(context).unlikePost(postId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(true);
                } else {
                    result.setValue(false);
                    Log.e(TAG, "Unlike post failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                result.setValue(false);
                Log.e(TAG, "Network error", t);
            }
        });

        return result;
    }

}
