package com.example.drops.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;

// This class stores the data of a Drop.
public class Drop {
    @SerializedName("id")
    private int id; // ID of the drop

    @SerializedName("title")
    private String title; // Title of the drop

    @SerializedName("message")
    private String message; // Textual content of the drop

    @SerializedName("latitude")
    private double latitude; // Latitude of the position of the drop

    @SerializedName("longitude")
    private double longitude; // Longitude of the position of the drop

    @SerializedName("anonymous")
    private int anonymous; // Whether the drop is anonymous or not

    @SerializedName("time")
    private long time; // Time the drop was posted in milliseconds since UNIX epoch

    @SerializedName("user_id")
    private int userId; // ID of the user that posted the drop

    @SerializedName("username")
    private String username; // Name of the user that posted the drop

    @SerializedName("group_id")
    private int groupId; // ID of the group that the drop belongs to

    @SerializedName("reports")
    private int reported; // Amount of times the drop was reported

    @SerializedName("upvotes")
    private int upvotes; // Net amount of upvotes and downvotes the drop has


    // Method to gets drops from the database dependent on the
    // range of the location and on whether the user is a moderator or not.
    // If the database call is successful, the 'successBehaviour' function is ran.
    public static void getDrops(Context context,
                                double minLat, double minLong, double maxLat, double maxLong,
                                int groupId, boolean isModerator, Consumer<List<Drop>> successBehaviour) {

        // Make an instance of the API
        DropApi api = RetrofitClient.getInstance(context).create(DropApi.class);

        // Instantiate call to get the account from the database
        Call<List<Drop>> getDrops;

        if (isModerator) {
            // If the user is a moderator, get all the reported drops
            getDrops = api.getReportedDrops();
        } else {
            // For a regular user, get all drops in a certain range
            getDrops = api.getGroupDropInRange(groupId, minLat, minLong, maxLat, maxLong);
        }

        // Make the asynchronous call to the database
        getDrops.enqueue(RetrofitClient.getCallback(context, "Failed to get drops",
                // Functionality on success; we remove drops older than a day
                // and then run the success behaviour
                (dropList) -> {
                    // Remove drops that are more then a day old
                    removeDayOldDrops(context, dropList);

                    // Run the success behaviour and the remaining drops
                    successBehaviour.accept(dropList);
                }, () -> {}));
    }

    // Method that remove drops that are more than a day old from a list
    private static void removeDayOldDrops(Context context, List<Drop> dropList) {
        long currTime = System.currentTimeMillis(); // Get current time
        List<Drop> dayOldDrops = new ArrayList<>(); // Initialize list of day old drops

        // Loop through all the drops returned from the database
        for(Drop drop : dropList) {

            // If time since drop posted is more then a day, then we delete it.
            if (currTime - drop.getTime() > 86400000) {
                deleteDrop(context, drop.getId(), (n) -> {}); // Delete from database
                dayOldDrops.add(drop); // Add to list of day old drops
            }
        }

        // Remove all the drops that more than a day old
        dropList.removeAll(dayOldDrops);
    }

    // This method updates a drop in the database with a certain ID, with another drop
    private static void updateDrop(Context context, int oldDropId, Drop newDrop) {

        // Make an instance of the API
        DropApi api = RetrofitClient.getInstance(context).create(DropApi.class);

        // Instantiate call to update a drop from the database
        Call<Void> updateDrop = api.updateDrop(oldDropId, newDrop);

        // Make the asynchronous call to the database
        updateDrop.enqueue(RetrofitClient.getCallback(context, "Failed to update drop",
                (n) -> {}, () -> {}));
    }

    // This method changes the amount of reports that a drop with a certain ID
    // has received by a certain number.
    public static void updateReportCount(Context context, Drop drop, int diff) {
        // Change amount of reports of drop
        drop.setReported(drop.getReported() + diff);

        // Update the drop in the database
        updateDrop(context, drop.getId(), drop);
    }

    // This method changes the net amount of up/downvotes that a drop with a certain ID
    // has received by a certain number.
    public static void updateUpvoteCount(Context context, Drop drop, int diff) {
        // Change amount of upvotes of drop
        drop.setUpvotes(drop.getUpvotes() + diff);

        // Update the drop in the database
        updateDrop(context, drop.getId(), drop);
    }

    // This method deletes a drop from the database
    public static void deleteDrop(Context context, int dropId, Consumer<Void> successBehaviour) {

        // Make an instance of the API
        DropApi api = RetrofitClient.getInstance(context).create(DropApi.class);

        // Instantiate call to delete the account from the database
        Call<Void> deleteCall = api.deleteDrop(dropId);

        // Make the asynchronous call to the database
        deleteCall.enqueue(RetrofitClient.getCallback(context, "Failed to delete drop",
                successBehaviour, () -> {}));
    }


    /**
     * Getters and setters
     **/

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int n) {
        this.upvotes = n;
    }

    public int getId() {
        return id;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }

    public int getReported() {
        return reported;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
