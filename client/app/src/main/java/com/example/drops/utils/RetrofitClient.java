package com.example.drops.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// This class is used by utility classes to create instances of the API interfaces.
// Using retrofit we can adapt the interfaces to HTTP calls.
// This class is an application of the Singleton design pattern
public class RetrofitClient {
    private static Retrofit instance; // Unique instance of the Retrofit class

    // The base url of our server that all of our calls will start with
    // There is currently a list of IP's that we commonly used, if you want to test
    // on the online server, then comment in the top one. Be warned, the server times out a lot
    private static final String BASE_URL = "https://appdevdatabase.onrender.com/api/";
//    private static final String BASE_URL = "http://localhost:5000/api/";
//    private static final String BASE_URL = "http://10.0.2.2:5000/api/";
//    private static final String BASE_URL = "http://192.168.137.104:5000/api/";
//    private static final String BASE_URL = "http://192.168.137.48:5000/api/";

    // The singleton constructor of the Retrofit instance
    public static synchronized Retrofit getInstance(Context context) {
        // If instance is null create a new one
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Set base url
                    .addConverterFactory(GsonConverterFactory.create()) // Add JSON converter
                    .client(okHttpClient(context)) // Add custom HTTP client
                    .build(); // Build the result
        }

        // Return the instance
        return instance;
    }

    // Constructor of the Retrofit instance.
    // This is with the baseURL, this should only be used for testing purposes
    public static synchronized void setInstance(Context context, String baseURL) {
        instance = new Retrofit.Builder()
                .baseUrl(baseURL) // Set base url
                .addConverterFactory(GsonConverterFactory.create()) // Add JSON converter
                .client(okHttpClient(context)) // Add custom HTTP client
                .build(); // Build the result
    }

    // This method builds a custom HTTP client, with custom interceptors.
    // These interceptor will either add extra thing to our HTTP calls, or add
    // extra logging.
    private static OkHttpClient okHttpClient(Context context) {
        // Create a logging interceptor which logs all calls made and all responses.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create an authentication interceptor which adds the authentication token to the headers
        AuthInterceptor authInterceptor = new AuthInterceptor(context);

        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor) // Add authentication interceptor
                .addInterceptor(logging) // Add logging interceptor
                .build();
    }

    // This method creates a callback for asynchronous calls to the database to use when they
    // get a response from the server.
    // After a successful completion the callback runs the code in the 'successBehaviour' function
    // on the retrieved data.
    // After (possibly unsuccessful) completion it runs the 'endBehaviour' function.
    public static <T> Callback<T> getCallback(Context context, String errorString,
                                              Consumer<T> successBehaviour, Runnable endBehaviour) {
        return (new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    // When call is successful, the success behaviour is ran on the retrieved data.
                    successBehaviour.accept(response.body());

                    // Run end behaviour
                    endBehaviour.run();
                } else {
                    // When the call is not successful, we log the response
                    // and let the user know the action failed.
                    Log.e("DATABASE", response.raw().toString());
                    Thread.dumpStack();
                    Toast.makeText(context, errorString, Toast.LENGTH_LONG).show();

                    // Run end behaviour
                    endBehaviour.run();
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork == null) {
                    // When the call fails we log the error message and let the user know the action failed.
                    Log.e("DATABASE", t.getLocalizedMessage());
                    Thread.dumpStack();
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                } else {
                    // When the call fails we log the error message and let the user know the action failed.
                    Log.e("DATABASE", t.getLocalizedMessage());
                    Thread.dumpStack();
                    Toast.makeText(context, errorString, Toast.LENGTH_LONG).show();
                }

                endBehaviour.run();
            }
        });
    }
}
