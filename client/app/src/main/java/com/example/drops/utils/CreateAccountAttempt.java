package com.example.drops.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This class is to send to the server when creating an account.
// It has the same class variables as account, except it has no ID since the database assigns that
// and the password is a different hash as it is the user input.
public class CreateAccountAttempt {
    @SerializedName("name")
    private String name; // Name of the account

    @SerializedName("email")
    private String email; // Email of the account

    @SerializedName("password")
    private String password; // Password of the account after a simple hash

    @SerializedName("moderator")
    private int isModerator; // Whether the account is a moderator (0 if not, 1 if so)

    public CreateAccountAttempt(String name, String email, String password, int isModerator) {
        // Set instance variables
        this.name = name;
        this.email = email;
        this.isModerator = isModerator;

        // Prevent sending password string over network
        this.password = Integer.toString(password.hashCode());
    }

    // This method attempts to create an account in the database.
    // The database will then return the assigned account ID with an authentication token
    // if account creation was successful.
    // The database also adds new accounts automatically to the public group
    // After a successful completion is runs the code in the 'successBehaviour' function.
    // After (possible unsuccessful) completion it runs the 'endBehaviour' function.
    public void createAccount(Context context, Consumer<LoginResponse> successBehaviour, Runnable endBehaviour) {

        // Get instance of the session manager
        SessionManager sessionManager = new SessionManager(context);

        // Make an instance of the API
        AccountApi api = RetrofitClient.getInstance(context).create(AccountApi.class);

        // Instantiate call to create the account in the database
        Call<LoginResponse> makeAccount = api.createAccount(this);

        // Make the asynchronous call to the database
        makeAccount.enqueue(RetrofitClient.getCallback(context, "Name or email is in use",
                (loginResponse) -> {
                    // Success behaviour:

                    // Save the returned account
                    loginResponse.saveUserData(sessionManager);

                    // Success and end behaviour are run.
                    successBehaviour.accept(loginResponse);
                }, endBehaviour));
    }
}
