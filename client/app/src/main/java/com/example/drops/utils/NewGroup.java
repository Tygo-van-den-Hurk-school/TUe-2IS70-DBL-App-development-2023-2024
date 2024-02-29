package com.example.drops.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;

// This class is to send to the server when creating a group.
// It has the same class variables as a group, except it has no ID since the database assigns that.
public class NewGroup {
    @SerializedName("name")
    private String name; // The name of a group

    public NewGroup(String name) {
        this.name = name; // Set the name
    }

    public String getName() {
        return name;
    }

    // This method attempts to create a comment in the database.
    // After a successful completion is runs the code in the 'successBehaviour' function.
    public void createGroup(Context context, List<String> usernames, Consumer<Void> successBehaviour) {

        // Make an instance of the API
        GroupApi api = RetrofitClient.getInstance(context).create(GroupApi.class);

        // Instantiate call to create the comment in the database
        Call<Group> makeGroup = api.createGroup(this);

        // Make the asynchronous call to the database to create the group
        makeGroup.enqueue(RetrofitClient.getCallback(context, "Failed to create group",
                // On success add members to group
                (Group group) -> {
                    addMembersToGroup(context, api, group, usernames);
                    successBehaviour.accept(null);
                }, () -> {}));
    }

    // Method to add a list of accounts (by account name), to a certain group
    private void addMembersToGroup(Context context, GroupApi api, Group group, List<String> usernames) {

        // Loop over all account names
        for(String name : usernames) {

            // Instantiate call to the database to add user to the group
            Call<Void> addUser = api.addMemberByName(group.getId(), name);

            // Make asynchronous call to the database
            addUser.enqueue(RetrofitClient.getCallback(context, "Failed to add " + name + " to group",
                    (n) -> {}, () -> {}));
        }
    }
}
