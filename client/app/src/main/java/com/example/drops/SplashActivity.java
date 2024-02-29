package com.example.drops;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.example.drops.utils.SessionManager;

// The splash activity is the first screen that shows up, it allows the user to go
// either to the login screen or the sign up screen
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check whether there is an internet connection
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // If there is an internet connection, we check whether we have user data saved
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            if(sessionManager.getAuthToken() != null && sessionManager.getAccount() != null) {
                // If we have an authentication token and an account, we continue to the
                // map fragment straight away, skipping manual log in.
                Intent intent = new Intent(this, BottomNavigationActivity.class);
                startActivity(intent);
            }
        }
   }

    // Method to go to login screen, used as listener for login button
    public void goToLogin (View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Method to go to sign up screen, used as listener for sign up button
    public void goToSignup (View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}