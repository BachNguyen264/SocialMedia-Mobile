package models.user;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;

    public UpdateUserRequest(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
