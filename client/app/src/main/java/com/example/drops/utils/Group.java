package com.example.drops.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;

// This class stores the data of a group.
public class Group {
    @SerializedName("id")
    private int id; // The ID of a group

    @SerializedName("name")
    private String name; // The name of a group


    /** Getters and setters **/
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // This method gets all the groups associated with an account from the database.
    // After a successful completion is runs the code in the 'successBehaviour' function, which takes
    // in the list of groups as a parameter.
    public static void getGroups(Context context, int userId, Consumer<List<Group>> successBehaviour) {

        // Make an instance of the API
        AccountApi api = RetrofitClient.getInstance(context).create(AccountApi.class);

        // Instantiate call to get the list of groups from the database
        Call<List<Group>> getGroups = api.getGroups(userId);

        // Make the asynchronous call to the database
        getGroups.enqueue(RetrofitClient.getCallback(context, "Failed to get groups",
                successBehaviour, () -> {}));
    }

    // This method removes an account from a group.
    // After a successful completion is runs the code in the 'successBehaviour' function.
    public static void removeMember(Context context, int groupId, int userId, Consumer<Void> successBehaviour) {

        // Make an instance of the API
        GroupApi api = RetrofitClient.getInstance(context).create(GroupApi.class);

        // Instantiate call to get the list of groups from the database
        Call<Void> deleteMember = api.deleteMember(groupId, userId);

        // Make the asynchronous call to the database
        deleteMember.enqueue(RetrofitClient.getCallback(context, "Failed to leave group",
                successBehaviour, () -> {}));
    }
}
