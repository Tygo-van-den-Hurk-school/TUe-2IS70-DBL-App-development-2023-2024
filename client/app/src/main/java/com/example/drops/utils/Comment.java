package com.example.drops.utils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;

public class Comment {
    @SerializedName("id")
    private int id; // Id of the comment

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

    // Method to get comments of a drop from the database
    // If the database call is successful, the 'successBehaviour' function is ran.
    public static void getComments(Context context, int dropId, Consumer<List<Comment>> successBehaviour) {

        // Make an instance of the API
        DropApi api = RetrofitClient.getInstance(context).create(DropApi.class);

        // Instantiate call to get the account from the database
        Call<List<Comment>> getComments = api.getDropComments(dropId);

        // Make the asynchronous call to the database
        getComments.enqueue(RetrofitClient.getCallback(context, "Failed to get comments", successBehaviour, () -> {}));
    }

    // This method updates a comment in the database with a certain ID, with another comment
    private static void updateComment(Context context, int oldCommentId, Comment newComment) {

        // Make an instance of the API
        CommentApi api = RetrofitClient.getInstance(context).create(CommentApi.class);

        // Instantiate call to update a drop from the database
        Call<Void> updateComment = api.updateComment(oldCommentId, newComment);

        // Make the asynchronous call to the database
        updateComment.enqueue(RetrofitClient.getCallback(context, "Failed to update comment",
                (n) -> {}, () -> {}));
    }

    // This method changes the amount of reports that a drop with a certain ID
    // has received by a certain number.
    public static void updateReportCountComment(Context context, Comment comment, int diff) {
        // Change amount of reports of drop
        comment.setReports(comment.getReports() + diff);

        // Update the drop in the database
        updateComment(context, comment.getId(), comment);
    }

    // Method to gets drops from the database dependent on the
    // range of the location and on whether the user is a moderator or not.
    // If the database call is successful, the 'successBehaviour' function is ran.
    public static void getReportedComments(Context context, Consumer<List<Comment>> successBehaviour) {

        // Make an instance of the API
        CommentApi api = RetrofitClient.getInstance(context).create(CommentApi.class);

        // Instantiate call to get the account from the database
        Call<List<Comment>> getReportedComments = api.getReportedComments();

        // Make the asynchronous call to the database
        getReportedComments.enqueue(RetrofitClient.getCallback(context, "Failed to get comments",
                successBehaviour, () -> {}));
    }

    // This method deletes a comment from the database
    public static void deleteComment(Context context, int commentId, Consumer<Void> successBehaviour) {

        // Make an instance of the API
        CommentApi api = RetrofitClient.getInstance(context).create(CommentApi.class);

        // Instantiate call to delete the account from the database
        Call<Void> deleteCall = api.deleteComment(commentId);

        // Make the asynchronous call to the database
        deleteCall.enqueue(RetrofitClient.getCallback(context, "Failed to delete comment",
                successBehaviour, () -> {}));
    }


    /** Getters and setters **/

    public int getUser() {
        return user_id;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCommentMessage() {
        return text;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }
}


