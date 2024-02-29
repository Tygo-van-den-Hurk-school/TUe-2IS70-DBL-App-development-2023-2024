package com.example.drops;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.drops.utils.LoginAttempt;
import com.example.drops.utils.SessionManager;

// The sign up activity allows the user to sign in to an existing account
public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Method for logging in, acts as listener for the login button
    public void login (View view) {
        Button button = findViewById(R.id.login);

        // Disable button to avoid button spam
        button.setEnabled(false);

        // Set user data
        String name = ((EditText)findViewById(R.id.username)).getText().toString();
        final String password = ((EditText)findViewById(R.id.password)).getText().toString();

        // Attempt to login, if it is successful we go to the map fragment
        // when the call returns, successful or not, we enable the login button again
        LoginAttempt loginAttempt = new LoginAttempt(name, password);
        loginAttempt.login(getApplicationContext(), (n) -> goToMap(), () -> button.setEnabled(true));
    }

    // Method to go to sign up screen, acts as listener for sign up button
    public void goToSignup(View view) {
        Intent intent = new Intent(getBaseContext(), SignupActivity.class);
        startActivity(intent);
    }

    // Method to go to map screen, acts as callback for the login attempt
    private void goToMap() {
        Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
        startActivity(intent);
    }
}

