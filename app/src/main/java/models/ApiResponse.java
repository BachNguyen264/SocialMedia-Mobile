package models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private T data;

    @SerializedName("error")
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
