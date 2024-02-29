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
// to the database regarding accounts.
public interface AccountApi {

    // Gets all the accounts from the database
    @GET("accounts/")
    Call<List<Account>> getAccounts();

    // Attempts the login an account with a username and password
    @POST("accounts/login")
    Call<LoginResponse> login(@Body LoginAttempt attempt);

    // Gets an account by their ID
    @GET("accounts/{id}")
    Call<Account> getAccountById(@Path("id") int id);

    // Gets an account by the name
    @GET("accounts/name/{name}")
    Call<Account> getAccountByName(@Path("name") String name);

    // Attempts to create an account with a username, password and email
    @POST("accounts/create")
    Call<LoginResponse> createAccount(@Body CreateAccountAttempt account);

    // Updates an account with a certain ID, with the account that is put in the body
    @PUT("accounts/{id}")
    Call<Account> updateAccount(@Path("id") int id, @Body Account account);

    // Deletes an account with a certain ID
    @DELETE("accounts/{id}")
    Call<Void> deleteAccount(@Path("id") int id);

    // Gets all of the groups that an account belongs to
    @GET("accounts/{id}/groups")
    Call<List<Group>> getGroups(@Path("id") int id);
}
