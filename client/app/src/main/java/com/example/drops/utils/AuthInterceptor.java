package com.example.drops.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

// This class is used to intercept out going HTTP requests to the database
// and add the authentication tokens to the header.
public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager; // Session manager, which stores the token

    public AuthInterceptor(Context context) {
        sessionManager = new SessionManager(context); // Initialize session manager
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        // Get the request builder of the chain
        Request.Builder builder = chain.request().newBuilder();

        // Get the authentication token.
        String token = sessionManager.getAuthToken();

        if(token != null) {
            // If a token is found, we add it to the header.
            builder.addHeader("user_access_token", token);
        } else {
            // If no token is found, we log this as an error.
            // This might not necessarily be an error, e.g. for logging in or signing up
            Log.e("AUTHENTICATION", "No authentication token found");
        }

        // Return the modified chain
        return chain.proceed(builder.build());
    }
}
