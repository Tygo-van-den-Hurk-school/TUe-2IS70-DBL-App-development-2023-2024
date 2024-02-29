package com.example.drops;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.drops.utils.Drop;
import com.example.drops.utils.GPSHandler;
import com.example.drops.utils.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.lang.Math;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.HEAD;

public class MapFragment extends Fragment implements OnCameraMoveListener, OnMapReadyCallback, LocationListener {

    // The variable storing the map used by the fragment
    private GoogleMap map;
    // The currently cached query area
    private LatLngBounds queriedArea;
    // The user's view of the map
    private LatLngBounds viewBounds;
    // The list containing the drops currently within the query area
    private List<Drop> dropList;
    // Hashmap associating each drop in the queried area to a marker on the map
    private HashMap<Drop, Marker> drops;
    // The current position of the user
    private LatLng userPosition;
    // The location manager used to get the user's location via GPS
    private LocationManager locationManager;
    // Instance of GPSHandler helper method to outsource calculations relevant to GPS implementation
    private GPSHandler gpsHandler;
    // The marker showing the user's position on the map
    private Marker userMarker;
    // The SessionManager instance storing the data of the current session such as the drops and
    // authentication token
    private SessionManager sessionManager;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermision")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Set up the map settings and get the initial user location
        setupMap(googleMap);

        // Move the map to the user's position and create a marker to show their position
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 20));
        userMarker = googleMap.addMarker(new MarkerOptions().position(userPosition));

        // Set on marker click listener so that when drop markers are clicked,
        // the FullDropFragment of that drop will be shown.
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Make the userMarker not clickable
                if (marker.equals(userMarker)){
                    return false;
                }
                // Get the respective drop pair of the clicked marker.
                Drop drop = getDropFromMap(drops, marker);

                // Creates a new instance of the full drop fragment of the clicked drop.
                FullDropFragment fragment = new FullDropFragment(drop, () -> {});
                FragmentTransaction transaction =
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();
                transaction.add(R.id.drop_content, fragment)
                        .commit();

                return false;
            }
        });

        // Update the user's view to match the map
        viewBounds = map.getProjection().getVisibleRegion().latLngBounds;
        queryArea();
    }

    /**
     * Basic map setup, changing the map settings and getting the initial location of the user.
     */
    private void setupMap(GoogleMap googleMap){

        // Set the map settings to remove rotation and tilt and set the listener for map interactions
        // to this.
        this.map = googleMap;
        googleMap.setOnCameraMoveListener(this::onCameraMove);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // The default user location is at the TU/e coordinates, this is done to avoid crashes when
        // the user refuses location service permissions.
        LatLng tue = new LatLng(51.44868332980898, 5.490704069399196);
        userPosition = tue;

        // Check if gps permissions have been granted, if so get and store the user's current location
        if (gpsHandler.checkPermission(getActivity().getApplicationContext(), getActivity())) {
            Location userLocation = locationManager.getLastKnownLocation(GPSHandler.locationProvider);
            if (userLocation != null) {
                userPosition = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            } else {
                System.out.println("User location null, using tu/e coordinates");
                userPosition = tue;
            }
        }
    }

    /**
     * Finds the respective drop pair of the marker in the drops hashmap.
     */
    private Drop getDropFromMap(HashMap<Drop, Marker> drops, Marker marker) {
        Drop drop = null;
        for (HashMap.Entry<Drop, Marker> entry : drops.entrySet()) {
            if (entry.getValue().equals(marker)) {
                System.out.println("found");
                drop = entry.getKey();
            }
        }

        return drop;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Create the a button for creating drops
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton createButton = view.findViewById(R.id.create_btn);

        // Set the local instances of GPSHandler and LocationManager
        this.gpsHandler = new GPSHandler();
        this.locationManager = (LocationManager) getActivity().getSystemService(
            Context.LOCATION_SERVICE
        );

        // Check whether location service permissions have been granted and if so request
        // continuous location updates
        if (gpsHandler.checkPermission(getContext(), getActivity())) {
            locationManager.requestLocationUpdates(
                    GPSHandler.locationProvider, 0, 0, this);
            System.out.println("Location permission granted");
        } else {
            System.out.println("Location permission denied");
        }

        // Initializes the create drop button which opens the CreateDropDialog.
        createButton.setOnClickListener(this::createNoteDialog);
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        return view;
    }

    /**
     * Creates a new instance of CreateDropDialog and shows it
     */
    public void createNoteDialog(View view) {
        if(userPosition == null) {
            Toast.makeText(getContext(), "Cannot get position", Toast.LENGTH_LONG).show();
            return;
        }

        if(!checkCampusBounds(userPosition.latitude, userPosition.longitude)) {
            Toast.makeText(getContext(), "Cannot make a drop outside of campus", Toast.LENGTH_LONG).show();
            return;
        }

        CreateDropDialog createDropDialog = new CreateDropDialog(this);
        createDropDialog.show(getActivity().getSupportFragmentManager(), "create drop dialog");
    }

    /**
     * Checks whether a position is within the campus boundaries
     */
    private boolean checkCampusBounds(double latitude, double longitude) {
        return latitude > 51.445155 && latitude < 51.452835
                && longitude > 5.4800933 && longitude < 5.4975333;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nav_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Called when the user's location changes and the new location readings are delivered as a single
     * reading.
     */
    public void onLocationChanged(Location location) {
        // Update the user's location
        userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        // Update the position of the marker showing the user's position
        if (userMarker != null){
            userMarker.setPosition(userPosition);
        }
        System.out.println("User location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    /**
     * Called when the user's location changes and the new location readings are delivered in a batch.
     * In this case the locations are filtered to update the user's position more smoothly.
     */
    public void onLocationChanged(List<Location> locations) {
        System.out.println("Location delivered in batch");
        // Update the user's location
        userPosition = gpsHandler.filterLocation(locations);
        // Update the position of teh marker showing the user's position
        if (userMarker != null) {
            userMarker.setPosition(userPosition);
        }
        System.out.println("User location: " + userPosition.latitude + ", " + userPosition.longitude);
    }

    /**
     * Initialize the drops hashmap with the requestedDrops list
     */
    public void initDrops(List<Drop> requestedDrops) {
        // If drops hashmap is not null, assign drops to previous drops
        HashMap<Drop, Marker> prevDrops;
        if (drops != null) {
            prevDrops = drops;
        } else {
            // If drops hashmap is null, assign new hashmaps
            // to previous drops and drops
            prevDrops = new HashMap<>();
            this.drops = new HashMap<>();
        }
        for (Drop drop : requestedDrops) {
            System.out.println("Initializing drops");
            // If the drop is not in previous drops, add the new drop in drops hashmap
            // with a null marker value.
            boolean contained = false;
            for (Map.Entry<Drop, Marker> entry : drops.entrySet()) {
                if (entry.getKey().getId() == drop.getId()) {
                    contained = true;
                }
            }
            if (!contained) {
                this.drops.put(drop, null);
            }

            System.out.println("DropTitle: " + drop.getTitle());
        }
        for (HashMap.Entry<Drop, Marker> entry : drops.entrySet()) {
            System.out.println("DropTitle: " + entry.getKey().getTitle());
        }
    }

    /**
     * Creates a single marker using drop's location
     */
    public Marker createPin(GoogleMap googleMap, Drop drop) {
        // Creates a new location object to add to the location to the map
        LatLng location = new LatLng(drop.getLatitude(), drop.getLongitude());

        // Adds marker on the drop's location in the map using the Maps-API
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(bitmapDescriptor(getActivity().getApplicationContext(), R.drawable.message_marker))
                .title("Marker"));

        return marker;
    }

    /**
     * Create markers to the map for all drops within the query area,
     * sets the value of each key in the drops hashmap as the added marker.
     **/
    public void createAllPins(GoogleMap googleMap, HashMap<Drop, Marker> drops) {
        // Adds marker on drops within the queried area
        for (HashMap.Entry<Drop, Marker> entry : drops.entrySet()) {

            // If the previous drops list does not contain the drop in the current list,
            // i.e. marker value is null, we add a new marker for that drop.
            if (entry.getValue() == null) {
                Marker marker = createPin(googleMap, entry.getKey());
                System.out.println("Marker value set");
                entry.setValue(marker);
            }

        }
    }

    /**
     * Creates BitmapDescriptor for the icons of markers in the maps API.
     */
    private BitmapDescriptor bitmapDescriptor(Context applicationContext, int vectorId) {

        Drawable drawable = ContextCompat.getDrawable(applicationContext, vectorId);
        drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth());
        Bitmap bitmap = Bitmap.createBitmap(drawable
                .getIntrinsicWidth(), drawable
                .getMinimumHeight(), Bitmap
                .Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Called when the user moves the map.
     */
    public void onCameraMove() {
        updateView();
    }

    /**
     * Check whether the user's map view is approaching the query area bounds and re-query if needed.
     */
    public void updateView() {
        // Update the variable storing the user's map view
        viewBounds = map.getProjection().getVisibleRegion().latLngBounds;

        // When the map is first created the user view is empty, if this was the case the previous
        // time the query area was calculated, re-query.
        if (queriedArea.southwest.equals(queriedArea.northeast)) {
            queryArea();
        }

        // Check whether the user's view is close to the boundary of the cached queried area, and
        // re-query if this is the case.
        if (checkBoundary(viewBounds, queriedArea, 0.1)) {
            System.out.println("re-querying area");
            queryArea();
            // Get drops within the queried area
            dropList = sessionManager.getDrops();
        }
    }

    /**
     * Queries the area surrounding the user's view of the map for drops from the database.
     * How much larger the queried area is than the map view is determined by queryScale.
     */
    public void queryArea() {
        // Set the query area size relative to the user view
        float queryScale = 2;
        // Use GPSHandler helper method to generate query bounds from the user view.
        queriedArea = gpsHandler.queryFromView(viewBounds, queryScale);
        // Request drops from database using the query bounds
        dropList = new ArrayList<>();
        sessionManager.setDrops(queriedArea.southwest.latitude, queriedArea.southwest.longitude,
                queriedArea.northeast.latitude, queriedArea.northeast.longitude, sessionManager.getGroup(),
                false, this::fillDropArray);
    }

    /**
     * Fills in the dropList and calls initDrops to initialize the drops hashmap
     */
    private Void fillDropArray(List<Drop> dropResult) {
        dropList.addAll(dropResult);
        initDrops(dropList);
        System.out.println("Queried drops");
        for (Drop d : dropList) {
            System.out.println("Drop: " + d.getTitle());
        }
        createAllPins(map, drops);
        return null;
    }

    /**
     * Check whether the user's view is close enough to the border of the currently cached query area.
     * How close is considered enough is determined by the fac parameter.
     */
    private boolean checkBoundary(LatLngBounds viewBounds, LatLngBounds q, double fac) {
        // Use the GPSHandler helper method to check whether the boundary is close to the query border
        return gpsHandler.checkBoundary(viewBounds, q, fac);
    }
}