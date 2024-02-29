package com.example.drops.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

import retrofit2.Call;

// This class is to send to the server when creating a comment.
// It has the same class variables as a comment, except it has no ID since the database assigns that.
public class NewComment {
    @SerializedName("text")
    private String text; // Text content of the comment

    @SerializedName("note_id")
    private int note_id; // Id of the note that the comment belong to

    @SerializedName("user_id")
    private int user_id; // Id of the user that posted the comment

    @SerializedName("username")
    private String username;

    @SerializedName("reports")
    private int reports; // Amount times the comment has been reported

    public NewComment(String text, int noteId, int userId, String username, int reports) {

        // Set instance variables
        this.text = text;
        this.note_id = noteId;
        this.username = username;
        this.user_id = userId;
        this.reports = reports;
    }

    // This method attempts to create a comment in the database.
    // After a successful completion is runs the code in the 'successBehaviour' function.
    public void createComment(Context context, Consumer<Void> successBehaviour) {

        // Make an instance of the API
        CommentApi api = RetrofitClient.getInstance(context).create(CommentApi.class);

        // Instantiate call to create the comment in the database
        Call<Void> makeComment = api.createComment(this);

        // Make the asynchronous call to the database
        makeComment.enqueue(RetrofitClient.getCallback(context, "Failed to post comment",
                successBehaviour, () -> {}));
    }
}
