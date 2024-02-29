package com.example.drops;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.drops.utils.Drop;
import com.google.android.gms.maps.model.Marker;

import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.drops.utils.Account;
import com.example.drops.utils.AccountApi;
import com.example.drops.utils.CheckPassword;
import com.example.drops.utils.CreateAccountAttempt;
import com.example.drops.utils.GroupApi;
import com.example.drops.utils.LoginResponse;
import com.example.drops.utils.RetrofitClient;
import com.example.drops.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.drops.databinding.ActivityBottomNavigationBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.LocalDateTime;

// The bottom navigation activity is the activity the displays the taskbar at the bottom of the screen
// it allows the user to switch between the dashboard, map and settings fragments
public class BottomNavigationActivity extends AppCompatActivity {
    BottomNavigationView navigationView; // View of the activity
    SessionManager sessionManager; // Session manager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_navigation);

        // Initialize session manager
        sessionManager = new SessionManager(getApplicationContext());

        // Load the navigation bar
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(selectedListener);

        // Check whether we have an instance saved (i.e. loaded the main screen before)
        if (savedInstanceState != null) {
            // If we have a saved instance, the previously visited fragment should be shown
            navigationView.getMenu().findItem(savedInstanceState.getInt("FRAGMENT")).setChecked(true);
            setFragment(savedInstanceState.getInt("FRAGMENT"));
        } else {
            // If we are opening the main screen for the first time, we got to the map screen
            navigationView.getMenu().findItem(R.id.nav_map).setChecked(true);
            MapFragment fragment = new MapFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.drop_content, fragment, "");
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // When saving an instance we store the last visited fragment
        outState.putInt("FRAGMENT", navigationView.getSelectedItemId());
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnItemSelectedListener selectedListener
            = menuItem -> setFragment(menuItem.getItemId());

    // Method to set the fragment selected by the navigation bar
    private boolean setFragment(int id) {

        // Switch statement which selects the correct fragment based on the ID
        switch (id) {
            case R.id.nav_map:
                // Load map fragment
                MapFragment fragment = new MapFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.drop_content, fragment);
                fragmentTransaction.commit();
                return true;

            case R.id.nav_dashboard:
                // Load dashboard fragment
                DashboardFragment fragment1 = new DashboardFragment();
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.drop_content, fragment1);
                fragmentTransaction1.commit();
                return true;

            case R.id.nav_settings:
                // Load settings fragment
                SettingsFragment fragment2 = new SettingsFragment();
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.drop_content, fragment2);
                fragmentTransaction2.commit();
                return true;
        }

        // Return false in case of invalid ID
        return false;
    }

    // Listener for Log out button on the settings screen
    public void logOut(View view) {
        // Make an alert to ask for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to log out?");

        // Set log out functionality to 'Confirm' button
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            sessionManager.clearPreferences(); // Clear user data
            goToLogin(); // Go to start screen
        });

        // Do nothing when canceling
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        // Create and show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Listener for Delete account button on the settings screen
    public void deleteAccount(View view) {
        // Make an alert to ask for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete your account?");

        // Delete the account when action is confirmed
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            Account.deleteAccount(getApplicationContext(),
                    // Success behaviour when account is deleted
                    (n) -> {
                        sessionManager.clearPreferences(); // Clear user data
                        goToHome(); // Go to start screen
                    });
        });

        // Do nothing when canceling
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        // Build and show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to go to start screen
    private void goToHome() {
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(intent);
    }

    // Method to go to login screen
    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    // Listener for Delete account button on the settings screen
    public void goToChangePassword(View view) {

        // Make an alert to ask for confirmation and to type new password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Change password");
        builder.setMessage("Enter your new password: ");

        // Make a text field for new password
        final EditText edittext = new EditText(this);
        builder.setView(edittext);

        // Set buttons
        builder.setPositiveButton("Confirm", (dialog, which) -> {
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        // Create dialog
        AlertDialog dialog = builder.create();

        // Set listener for confirmation button
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                String newPassword = edittext.getText().toString();
                if (!CheckPassword.checkPassword(
                        getApplicationContext(),
                        newPassword,
                        sessionManager.getAccount().getName())
                ) {
                    // Do nothing if the password is invalid
                    // Note that the dialog is also not closed, so a new attempt can be made
                    return;
                }

                // When password is valid, the password is changed and the dialog closes
                changePassword(newPassword);
                dialog.dismiss();
            });
        });

        // Show the dialog
        dialog.show();
    }

    // Listener for Moderator mode button on the settings screen
    public void toggleModerator(View view) {
        // Get the current state of the moderator mode
        boolean currMode = sessionManager.getModeratorMode();

        // Switch the other state
        sessionManager.setModeratorMode(!currMode);

        // Set the button color based on the current mode
        Button modButton = findViewById(R.id.button3);
        if (currMode) {
            // If not in moderator mode, use standard colors
            modButton.setBackground(getDrawable(R.drawable.standard_button));
        } else {
            // When in moderator mode, use a lighter color
            modButton.setBackground(getDrawable(R.drawable.toggled_button));
        }
    }

    // Method to change the password
    private void changePassword(String newPassword) {
        // Delete the current account and when that is successful, we make a new account
        // with the new password
        Account.deleteAccount(getApplicationContext(), (n) -> createNewAccount(newPassword));
    }

    // Method to make a new account with a new password
    private void createNewAccount(String newPassword) {
        // Get the current account state
        Account currAccount = sessionManager.getAccount();

        // Make a new account attempt with the new password
        CreateAccountAttempt newAccount = new CreateAccountAttempt(currAccount.getName(),
                currAccount.getEmail(), newPassword, currAccount.getIsModerator());

        // Create the new account
        newAccount.createAccount(getApplicationContext(), (n) -> {
        }, () -> {
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        System.out.println("PERMISSION RESULT RECEIVED");
        System.out.println(permissions.length);
        if (permissions.length == 2 && (grantResults[0] == PackageManager.PERMISSION_DENIED
                || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Location Permission");
            builder.setMessage("Location permission is required to use the app, " +
                    "please enable it in your phone's Settings.");

            // Set buttons
            builder.setPositiveButton("Ok", (dialog, which) -> {
            });

            // Create dialog
            AlertDialog dialog = builder.create();

            // Set listener for confirmation button
            dialog.setOnShowListener(dialogInterface -> {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view1 -> {
                    finishAffinity();
                });
            });

            dialog.show();
        }

    }
}