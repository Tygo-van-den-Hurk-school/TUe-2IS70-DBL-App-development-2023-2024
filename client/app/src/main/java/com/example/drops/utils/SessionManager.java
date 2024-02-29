package com.example.drops.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.drops.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

// This class stores all the relevant variables for the app to remember in between sessions.
public class SessionManager {

    // Preferences instances which can store variables between different app runs.
    private SharedPreferences preferences;
    private Context context; // Context of the app
    private Gson gson; // JSON converter instance


    /** Constants, keys for the SharedPreferences instances **/
    private final String USER_TOKEN = "user_token";
    private final String ACCOUNT = "account";
    private final String DROPS = "drops";
    private final String GROUPS = "groups";
    private final String GROUP = "group";
    private final String MODERATOR_MODE = "mod_mode";

    public SessionManager(Context context) {
        // Set instance variables
        preferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        this.context = context;
        gson = new Gson();
    }

    // Returns the group that is currently selected in the dashboard
    public int getGroup() {
        return preferences.getInt(GROUP, 1);
    }

    // Method to save the groupId of the group selected in the dashboard
    public void setGroup(int groupId) {
        SharedPreferences.Editor editor = preferences.edit(); // Prepare to edit preferences
        editor.putInt(GROUP, groupId); // Save the token
        editor.apply(); // Apply changes
    }

    // Method to save the authentication token
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = preferences.edit(); // Prepare to edit preferences
        editor.putString(USER_TOKEN, token); // Save the token
        editor.apply(); // Apply changes
    }

    // Method that first gets the account from the database and then saves in JSON format
    public void setAccount(int userId, Runnable successBehaviour) {
        // Get the account from the database
        Account.getAccountById(context, userId, (account) -> {
            // Success behaviour
            setAccount(account);
            successBehaviour.run();
        });
    }

    // Method to save an account to preferences in JSON format
    public void setAccount(Account account) {
        SharedPreferences.Editor editor = preferences.edit(); // Prepare to edit preferences
        editor.putString(ACCOUNT, gson.toJson(account)); // Save account in JSON format
        editor.apply(); // Apply changes
    }

    // Method to load the drops into the app, the apps that are retrieved are dependent on the
    // range of the location and on whether the user is a moderator or not.
    // If the database call is successful, the 'successBehaviour' function is ran.
    public void setDrops(double minLat, double minLong, double maxLat, double maxLong, int groupId,
                         boolean isModerator, Function<List<Drop>, Void> successBehaviour) {
        // Make a database call to get the groups
        Drop.getDrops(context, minLat, minLong, maxLat, maxLong, groupId, isModerator, (drops) -> {
            // Behaviour for successful call:
            setDrops(drops);
            successBehaviour.apply(drops); // Run success behaviour
        });
    }

    // Method to save a list of drops to preferences in JSON format
    public void setDrops(List<Drop> drops) {
        SharedPreferences.Editor editor = preferences.edit(); // Prepare to edit preferences
        editor.putString(DROPS, gson.toJson(drops)); // Put the drops in JSON format
        editor.apply(); // Apply changes
    }

    // Gets the groups associated with the logged in account from the database and saves them.
    // After a successful database call, success behaviour will be ran.
    public void setGroups(Runnable successBehaviour) {
        // Make a database call to get the groups
        Group.getGroups(context, getAccount().getId(), (groups) -> {
            // Behaviour for successful call:
            setGroups(groups);
            successBehaviour.run(); // Run success behaviour
        });
    }

    // Method to save a list of groups to preferences in JSON format
    public void setGroups(List<Group> groups) {
        SharedPreferences.Editor editor = preferences.edit(); // Prepare to edit preferences
        editor.putString(GROUPS, gson.toJson(groups)); // Put the groups in JSON format
        editor.apply(); // Apply changes
    }

    // Sets whether the user is currently in moderator mode
    public void setModeratorMode(boolean isModerator) {
        SharedPreferences.Editor editor = preferences.edit(); // Prepare to edit preferences
        editor.putBoolean(MODERATOR_MODE, isModerator); // Change the variable
        editor.apply(); // Apply the changes
    }

    // Gets the authentication token of the current session
    public String getAuthToken() {
        return preferences.getString(USER_TOKEN, null);
    }

    // Gets the account that is currently logged in
    public Account getAccount() {
        // Get account in JSON format
        String jsonAccount = preferences.getString(ACCOUNT, "");

        // Convert account from JSON to Account
        return gson.fromJson(jsonAccount, Account.class);
    }

    // Method that gets the drops that are currently loaded by the app
    public List<Drop> getDrops() {
        // Get list of drops in JSON format
        String jsonDrops = preferences.getString(DROPS, "");

        // Convert from JSON to list of drops
        Type dropListType = new TypeToken<List<Drop>>() {}.getType();
        return gson.fromJson(jsonDrops, dropListType);
    }

    // Gets the list of groups that the user is associated with
    public List<Group> getGroups() {
        // Get list of groups in JSON format
        String jsonGroups = preferences.getString(GROUPS, "");

        // Convert from JSON to list of groups
        Type groupListType = new TypeToken<List<Group>>() {}.getType();
        return gson.fromJson(jsonGroups, groupListType);
    }

    // Method to clear preferences, i.e. clear user data
    public void clearPreferences() {
        SharedPreferences.Editor editor = preferences.edit(); // Prepare to edit preferences
        editor.clear(); // Clear everything
        editor.apply(); // Apply changes
    }

    // Gets with the user is currently in moderator mode
    public boolean getModeratorMode() {
        return preferences.getBoolean(MODERATOR_MODE, false);
    }
}
