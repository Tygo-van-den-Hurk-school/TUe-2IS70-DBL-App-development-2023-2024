package com.example.drops.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

import retrofit2.Call;

// This class is to send to the server when logging into an account.
// It has the same class variables as account, except it has no ID or email since those
// are not needed to login and the password is a different hash as it is the user input.
public class LoginAttempt {
    @SerializedName("name")
    private String name; // Name of the account the user wants to log into

    @SerializedName("password")
    private String hash; // Hash of the password the user typed

    public LoginAttempt(String name, String password) {
        // Set instance variables
        this.name = name;

        // Prevent sending password string over network
        this.hash = Integer.toString(password.hashCode());
    }

    // This method attempts to login an account by querying the database.
    // The database will then return the associated account ID with an authentication token
    // if the login was successful
    // After a successful completion is runs the code in the 'successBehaviour' function.
    // After (possible unsuccessful) completion it runs the 'endBehaviour' function.
    public void login(Context context, Consumer<LoginResponse> successBehaviour, Runnable endBehaviour) {

        // Get instance of the session manager
        SessionManager sessionManager = new SessionManager(context);

        // Make an instance of the API
        AccountApi api = RetrofitClient.getInstance(context).create(AccountApi.class);

        // Instantiate call to login to the account
        Call<LoginResponse> makeAccount = api.login(this);

        // Make the asynchronous call to the database
        makeAccount.enqueue(RetrofitClient.getCallback(context, "Failed login",
                (loginResponse) -> {
                    // Success behaviour:

                    // Save the returned account
                    loginResponse.saveUserData(sessionManager);

                    // Success and end behaviour are run.
                    successBehaviour.accept(loginResponse);
                }, endBehaviour));
    }
}
