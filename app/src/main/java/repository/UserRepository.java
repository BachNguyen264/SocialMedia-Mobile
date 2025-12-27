package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import api.ApiClient;
import models.ApiResponse;

import models.user.UserResponse;
import models.user.UpdateUserRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final Context context;
    private final MutableLiveData<UserResult> profileLiveData = new MutableLiveData<>();

    public UserRepository(Context context) {
        this.context = context;
    }

    public LiveData<UserResult> getProfile() {
        fetchProfile();
        return profileLiveData;
    }

    private void fetchProfile() {
        ApiClient.getClient(context).getProfile().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call,
                                   Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> api = response.body();
                    if (api.isSuccess() && api.getData() != null) {
                        profileLiveData.postValue(
                                new UserResult(true, "Success", api.getData())
                        );
                    } else {
                        profileLiveData.postValue(
                                new UserResult(false, api.getError(), null)
                        );
                    }
                } else {
                    profileLiveData.postValue(
                            new UserResult(false, "Failed to get profile", null)
                    );
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                profileLiveData.postValue(
                        new UserResult(false, t.getMessage(), null)
                );
            }
        });
    }

    public LiveData<UserResult> updateProfile(String firstName, String lastName) {
        MutableLiveData<UserResult> updateResult = new MutableLiveData<>();

        UpdateUserRequest request = new UpdateUserRequest(firstName, lastName);

        ApiClient.getClient(context).updateProfile(request).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> api = response.body();
                    if (api.isSuccess() && api.getData() != null) {
                        updateResult.setValue(new UserResult(true, "Update successful", api.getData()));
                        Log.d(TAG, "Profile updated: " + api.getData().getFirstName());
                    } else {
                        String msg = api.getError() != null ? api.getError() : "Update failed";
                        updateResult.setValue(new UserResult(false, msg, null));
                        Log.e(TAG, "Update failed: " + msg);
                    }
                } else {
                    updateResult.setValue(new UserResult(false, "Server error: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                updateResult.setValue(new UserResult(false, t.getMessage(), null));
                Log.e(TAG, "Network error", t);
            }
        });

        return updateResult;
    }

    public static class UserResult {
        private final boolean success;
        private final String message;
        private final UserResponse user;

        public UserResult(boolean success, String message, UserResponse user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserResponse getUser() {
            return user;
        }
    }
}
