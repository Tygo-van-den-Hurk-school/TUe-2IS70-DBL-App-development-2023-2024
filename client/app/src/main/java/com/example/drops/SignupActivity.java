package com.example.drops;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.drops.utils.CheckPassword;
import com.example.drops.utils.CreateAccountAttempt;

// The sign up activity allows the user to create a new account
public class SignupActivity extends AppCompatActivity {

    private int isModerator; // Stores whether the new account should become a moderator (0 if not, 1 if so)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set up the moderator switch
        SwitchCompat isModeratorSwitch = findViewById(R.id.mod_switch);
        isModeratorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isModerator = isChecked ? 1 : 0;
            }
        });
    }

    // Method to go to sign up with a new account, used as listener for sign up button
    public void signUp (View view) {
        Button button = findViewById(R.id.signup);

        // Prevent button spam issues
        button.setEnabled(false);

        // Get user data
        String name = ((EditText)findViewById(R.id.username_signup)).getText().toString();
        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password2)).getText().toString();

        // Check whether the password is valid
        if(!CheckPassword.checkPassword(getApplicationContext(), password, name)) {
            // If not we abort signing up and set the button enabled again
            button.setEnabled(true);
            return;
        }

        // We create a new account in the database, if the database call is successful, we go to
        // the map screen. If the call is ends we enable the sign up button again (so also if the call fails)
        CreateAccountAttempt newAccount = new CreateAccountAttempt(name, email, password, isModerator);
        newAccount.createAccount(getApplicationContext(), (n) -> goToMap(), () -> button.setEnabled(true));
    }

    // Method to go to login screen, used as listener for login button
    public void goToLogin (View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Method to go to map screen, used as callback for successful sign up
    private void goToMap () {
        Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
        startActivity(intent);
    }
}