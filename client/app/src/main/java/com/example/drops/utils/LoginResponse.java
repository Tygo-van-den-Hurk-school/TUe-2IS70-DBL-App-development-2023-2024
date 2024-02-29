package com.example.drops.utils;

import com.google.gson.annotations.SerializedName;

// This class is what the database returns when logging in or creating an account.
public class LoginResponse {
    @SerializedName("token")
    String token; // Authentication token given by the server

    @SerializedName("id")
    int id; // ID of the logged in account


    /** Getters and setters **/

    public String getToken() {
        return token;
    }

    public int getId() {
        return id;
    }

    public void saveUserData(SessionManager sessionManager) {
        // The session manager saves the returned account ID and authentication token.
        sessionManager.saveAuthToken(this.getToken());
        sessionManager.setAccount(this.getId(), () -> {});
    }
}
