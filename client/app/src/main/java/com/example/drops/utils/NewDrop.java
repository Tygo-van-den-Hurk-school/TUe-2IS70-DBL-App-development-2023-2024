package com.example.drops.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

import retrofit2.Call;

// This class is to send to the server when creating a drop.
// It has the same class variables as a drop, except it has no ID since the database assigns that.
public class NewDrop {
    @SerializedName("title")
    private String title; // Title of the drop

    @SerializedName("message")
    private String message; // Text content of the drop

    @SerializedName("latitude")
    private double latitude; // Latitude of the location of the drop

    @SerializedName("longitude")
    private double longitude; // Longitude of the location of the drop

    @SerializedName("anonymous")
    private int anonymous; // Whether the drop is anonymous

    @SerializedName("time")
    private long time; // Time the drop was posted in milliseconds since UNIX epoch

    @SerializedName("user_id")
    private int userId; // ID of the user that posted the drop

    @SerializedName("group_id")
    private int group; // ID of the group that the drop belongs to

    @SerializedName("reports")
    private int reported; // Amount of times the drop was reported

    @SerializedName("upvotes")
    private int upvotes; // Net amount of upvotes and downvotes the drop has

    @SerializedName("username")
    private String username; // Username of the account that posted the drop

    public NewDrop(
            String title, String message, double latitude, double longitude, int anonymous,
            long time, int userId, String username, int groupId, int reported, int upvotes) {

        // Set instance variables
        this.title = title;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.anonymous = anonymous;
        this.time = time;
        this.userId = userId;
        this.username = username;
        this.group = groupId;
        this.reported = reported;
        this.upvotes = upvotes;
    }

    // This method attempts to create a drop in the database.
    // After a successful completion is runs the code in the 'successBehaviour' function.
    public void createDrop(Context context, Consumer<Void> successBehaviour) {

        // Make an instance of the API
        DropApi api = RetrofitClient.getInstance(context).create(DropApi.class);

        // Instantiate call to create the drop in the database
        Call<Void> makeDrop = api.createDrop(this);

        // Make the asynchronous call to the database
        makeDrop.enqueue(RetrofitClient.getCallback(context, "Failed to create drop",
                successBehaviour, () -> {}));
    }
}
