package com.example.drops.utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

// This interface contains all the relevant API calls that can be made
// to the database regarding comments.
public interface CommentApi {

    // Gets a comment by its ID
    @GET("comments/{id}")
    Call<Comment> getComment(@Path("id") int id);

    // Creates a new comment in the database
    @POST("comments/")
    Call<Void> createComment(@Body NewComment comment);

    // Updates a comment with a certain ID, by a new comment
    @PUT("comments/{id}")
    Call<Void> updateComment(@Path("id") int id, @Body Comment comment);

    // Deletes a comment with a certain ID
    @DELETE("comments/{id}")
    Call<Void> deleteComment(@Path("id") int id);

    // Gets the comments that are reported
    @GET("comments/reported")
    Call<List<Comment>> getReportedComments();
}
