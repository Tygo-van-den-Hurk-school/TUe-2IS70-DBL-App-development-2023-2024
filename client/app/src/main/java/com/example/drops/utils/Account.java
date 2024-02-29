package com.example.drops.utils;

import android.content.Context;
import com.google.gson.annotations.SerializedName;
import java.util.function.Consumer;
import retrofit2.Call;

// This class stores the data of a successfully logged in account.
public class Account {

    @SerializedName("id")
    private int id; // The account id

    @SerializedName("name")
    private String name; // The name of the account

    @SerializedName("email")
    private String email; // The email of the user

    @SerializedName("password")
    private String password; // The hashed password of the user

    @SerializedName("moderator")
    private int isModerator; // Whether the user is a moderator or not


    /* Getters and setters */

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public int getIsModerator() {
        return isModerator;
    }

    /* Methods that call the database */

    // This method deletes an account with a certain user id from the database.
    // After a successful completion is runs the code in the 'successBehaviour' function.
    public static void deleteAccount(Context context, Consumer<Void> successBehaviour) {

        // Get instance of the session manager
        SessionManager sessionManager = new SessionManager(context);

        // Make an instance of the API
        AccountApi api = RetrofitClient.getInstance(context).create(AccountApi.class);

        // Instantiate call to delete the account from the database
        Call<Void> deleteCall = api.deleteAccount(sessionManager.getAccount().getId());

        // Make the asynchronous call to the database
        deleteCall.enqueue(RetrofitClient.getCallback(context, "Failed to delete account",
                successBehaviour, () -> {}));
    }

    // This method get an account from the database with a certain user id.
    // After a successful completion is runs the code in the 'successBehaviour' function, which takes
    // in the account as a parameter.
    public static void getAccountById(Context context, int user_id, Consumer<Account> successBehaviour) {

        // Make an instance of the API
        AccountApi api = RetrofitClient.getInstance(context).create(AccountApi.class);

        // Instantiate call to get the account from the database
        Call<Account> getCall = api.getAccountById(user_id);

        // Make the asynchronous call to the database
        getCall.enqueue(RetrofitClient.getCallback(context, "Failed to get account",
                successBehaviour, () -> {}));
    }
}
