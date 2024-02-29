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
// to the database regarding drops.
public interface DropApi {

    // Gets a drop with a certain ID from the database
    @GET("drops/{id}")
    Call<Drop> getDrop(@Path("id") int id);

    // Attempts to create a new drop in the database
    @POST("drops/")
    Call<Void> createDrop(@Body NewDrop drop);

    // Updates a drop with a certain ID, by a new drop
    @PUT("drops/{id}")
    Call<Void> updateDrop(@Path("id") int id, @Body Drop drop);

    // Deletes a drop from the database
    @DELETE("drops/{id}")
    Call<Void> deleteDrop(@Path("id") int id);

    // Gets all the drops with in a square denote by the minimum and maximum
    // longitude and latitude.
    @GET("drops/{minLat}/{minLong}/{maxLat}/{maxLong}")
    Call<List<Drop>> getDropInRange(@Path("minLat") double minLat,
                                   @Path("minLong") double minLong,
                                   @Path("maxLat") double maxLat,
                                   @Path("maxLong") double maxLong);

    // Gets all the drops with in a square denote by the minimum and maximum
    // longitude and latitude from a certain group.
    @GET("groups/{groupId}/notes/{minLat}/{minLong}/{maxLat}/{maxLong}")
    Call<List<Drop>> getGroupDropInRange(@Path("groupId") double groupId,
                                   @Path("minLat") double minLat,
                                   @Path("minLong") double minLong,
                                   @Path("maxLat") double maxLat,
                                   @Path("maxLong") double maxLong);

    // Gets all of the drops that were reported at least once
    @GET("drops/reported")
    Call<List<Drop>> getReportedDrops();

    // Gets all of the comments belonging to a certain drop
    @GET("drops/{id}/comments")
    Call<List<Comment>> getDropComments(@Path("id") int id);
}
