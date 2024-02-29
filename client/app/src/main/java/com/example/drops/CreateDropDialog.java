package com.example.drops;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.drops.utils.Drop;
import com.example.drops.utils.GPSHandler;
import com.example.drops.utils.Group;
import com.google.android.gms.maps.model.LatLng;

import com.example.drops.utils.NewDrop;
import com.example.drops.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 *  This dialog shows up when the user wants to create a new drop and post it
**/
public class CreateDropDialog extends AppCompatDialogFragment {
    private EditText editDropTitle; // Text box for filling in the drop title
    private EditText editDropMessage; // Text box for filling in the drop contents
    private FloatingActionButton sendButton; // Button to create the new drop
    private Switch anonymousSwitch; // Switch to indicate whether the drop is anonymous
    private int anonymous = 0; // tracks anonymous property of a drop
    private LocationManager locationManager; // location manager
    private LatLng userPosition; // location of the user
    private ImageButton closeButton; // button the close the popup
    private Spinner shareableGroupsSpinner; // drop-down menu of available groups
    private MapFragment mapFragment; // reference to the map fragment

    /**
     * The constructor sets the map variable
     */
    public CreateDropDialog(MapFragment map) {
        this.mapFragment = map;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Inflate create drop dialog with custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_new_drop, null);
        builder.setView(view);

        // Initialize buttons, textViews available in the create drop dialog view.
        closeButton = (ImageButton) view.findViewById(R.id.close_dialog_button);
        closeButton.setOnClickListener(v -> getDialog().dismiss());
        editDropTitle = view.findViewById(R.id.input_drop_title);
        editDropMessage = view.findViewById(R.id.input_drop_content);
        sendButton = (FloatingActionButton) view.findViewById(R.id.send_button);

        //Add onClickListener for the send drop button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendButtonClick(view);
            }
        });

        setUpAnonymousDropSwitch(view); // Sets up the anonymous switch
        setUpGroupSpinner(view); // Sets up the group drop-down menu
        setUpLocationManager(); // Sets up the location facilities
        return builder.create(); // Creates the dialog
    }

    /**
     * Sets up the functionality of the anonymous switch for drop posting.
     */
    private void setUpAnonymousDropSwitch(View view) {
        anonymousSwitch = view.findViewById(R.id.anonymous_button);
        anonymousSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                anonymous = isChecked ? 1 : 0;
            }
        });
    }

    /**
     * Sets up dropdown with available groups to share drop with
     */
    private void setUpGroupSpinner(View view) {
        shareableGroupsSpinner = (Spinner) view.findViewById(R.id.group_visibility_spinner);
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());

        // Get the groups from the database
        sessionManager.setGroups(() -> {

            // When successful, put the groups in the adapter and display them using the spinner
            List<Group> groups = sessionManager.getGroups();
            GroupSpinnerAdapter spinnerAdapter = new GroupSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, groups);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shareableGroupsSpinner.setAdapter(spinnerAdapter);
        });
    }

    /**
     * Create location manager, check for permissions and read user location
      */
    private void setUpLocationManager() {
        GPSHandler gpsHandler = new GPSHandler();
        locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Check permissions
        if (gpsHandler.checkPermission(getActivity().getApplicationContext(), getActivity())) {

            // When we have permissions, we read the users location
            System.out.println("Location permission granted");
            Location location = locationManager.getLastKnownLocation(GPSHandler.locationProvider);
            if (location == null) {
                location = new Location(GPSHandler.locationProvider);
                location.setLatitude(51.44868332980898);
                location.setLongitude(5.490704069399196);
            }
            userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            System.out.println("Location permission denied");
        }
    }

    /**
     * Functionality of the send drop button, creates new drop
     */
    private void onSendButtonClick(View view) {
        String dropTitle = editDropTitle.getText().toString();
        String dropMessage = editDropMessage.getText().toString();
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        sessionManager.setGroups(() -> {});

        // Set the user ID and name according to whether the drop is anonymous or not
        // if the drop is anonymous we set the ID to 1 and the name to "Anonymous"
        int userID = (anonymous == 1) ? 1 : sessionManager.getAccount().getId();
        String username = (anonymous == 1) ? "Anonymous" : sessionManager.getAccount().getName();

        // Create and send the new drop to the database
        NewDrop drop = new NewDrop(dropTitle, dropMessage,
                userPosition.latitude, userPosition.longitude, anonymous,
                System.currentTimeMillis(),
                userID, username,
                ((Group) shareableGroupsSpinner.getSelectedItem()).getId(), 0, 0);
        drop.createDrop(getActivity().getApplicationContext(), (n) -> {});

        // Reload the map to show the new drop
        mapFragment.queryArea();
        getDialog().dismiss();
    }
}
