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
// to the database regarding groups.
public interface GroupApi {

    // Gets a group with a certain ID from the database
    @GET("groups/{id}")
    Call<Group> getGroup(@Path("id") int id);

    // Attempts to create a new group in the database
    @POST("groups/")
    Call<Group> createGroup(@Body NewGroup group);

    // Updates a group with a certain ID, by a new group
    @PUT("groups/{id}")
    Call<Void> updateGroup(@Path("id") int id, @Body Group group);

    // Deletes a group from the database
    @DELETE("groups/{id}")
    Call<Void> deleteGroup(@Path("id") int id);

    // Adds an account to a group with their ID's
    @POST("groups/{id}/{user_id}")
    Call<Void> addMember(@Path("id") int id, @Path("user_id") int user_id);

    // Adds an account to a group with their name
    @POST("groups/{id}/name/{username}")
    Call<Void> addMemberByName(@Path("id") int id, @Path("username") String username);


    // Removes an account from a group
    @DELETE("groups/{id}/{user_id}")
    Call<Void> deleteMember(@Path("id") int id, @Path("user_id") int user_id);

    // Gets all the accounts associated with a group
    @GET("groups/{id}/members")
    Call<List<Account>> getMembers(@Path("id") int id);
}
