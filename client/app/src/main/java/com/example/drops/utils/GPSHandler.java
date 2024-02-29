package com.example.drops.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class GPSHandler {

    // The location provider to use for finding the user's location
    public static final String locationProvider = LocationManager.GPS_PROVIDER;

    /**
     * Constructs a query area based on a given user view of the map and a scaling factor.
     */
    public LatLngBounds queryFromView(LatLngBounds viewBounds, float queryScale){
        // Calculate northeast coordinate by scaling northeast corner of user's view by the query scale
        double neLat = viewBounds.getCenter().latitude + queryScale *
                Math.abs(viewBounds.northeast.latitude - viewBounds.getCenter().latitude);
        double neLng = viewBounds.getCenter().longitude + queryScale *
                Math.abs(viewBounds.northeast.longitude - viewBounds.getCenter().longitude);

        // Calculate southwest coordinate by scaling southwest corner of user's view by the query scale
        double swLat = viewBounds.getCenter().latitude - queryScale *
                Math.abs(viewBounds.southwest.latitude - viewBounds.getCenter().latitude);
        double swLng = viewBounds.getCenter().longitude - queryScale *
                Math.abs(viewBounds.southwest.longitude - viewBounds.getCenter().longitude);

        // Construct and return query bounds from southwest and northeast points
        return new LatLngBounds( new LatLng(swLat, swLng), new LatLng(neLat, neLng));
    }

    /**
     * Constructs a rectangular query area based on an origin point and a range in meters.
     */
    public LatLngBounds queryByRange(LatLng userPosition, float range){
        // Create northeast and southwest points by converting range from meters to degrees and
        // adding or subtracting from the origin
        double neLat = userPosition.latitude + range/111139;
        double neLng = userPosition.longitude + range/111139;
        double swLat = userPosition.latitude - range/111139;
        double swLng = userPosition.longitude - range/111139;

        // Construct and return query bounds from southwest and northeast points
        return new LatLngBounds( new LatLng(swLat, swLng), new LatLng(neLat, neLng));
    }

    /**
     * Filter a batch of location readings to return a single location.
     */
    public LatLng filterLocation(List<Location> locations) {
        // Currently averaging is used as the filtering method

        // Set initial latitude and longitude
        double lat = 0;
        double lng = 0;

        // Iterate through locations and sum up their latitude and longitude
        for (Location loc : locations){
            lat += loc.getLatitude();
            lng += loc.getLongitude();
        }
        // Calculate average by dividing latitude and longitude by number of locations
        lat = lat / locations.size();
        lng = lng / locations.size();
        LatLng pos = new LatLng(lat, lng);

        // Return filtered location
        return new LatLng(pos.latitude, pos.longitude);
    }

    /**
     * Check whether a given user's view is close to the borders of a query area.
     */
    public boolean checkBoundary(LatLngBounds v, LatLngBounds q, double fac) {

        // Calculate the height and width of the query area
        double verticalSize = q.northeast.latitude - q.southwest.latitude;
        double horizontalSize = q.northeast.longitude - q.southwest.longitude;

        // Check whether the view area is within some fraction of the height and width of the query
        // area to the query area's border
        if (Math.abs(q.northeast.latitude - v.northeast.latitude) < verticalSize * fac ||
                Math.abs(q.northeast.longitude - v.northeast.longitude) < horizontalSize * fac ||
                Math.abs(v.southwest.latitude - q.southwest.latitude) < verticalSize * fac ||
                Math.abs(v.southwest.longitude - q.southwest.longitude) < horizontalSize * fac) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether the user has given the app permission to use location services, and request
     * permission if not.
     */
    public boolean checkPermission(Context context, Activity activity) {
        // Check whether permissions are already available
        String[] permissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION};
        if (!(context.checkCallingOrSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED)
                && !(context.checkCallingOrSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED)){

            // If the permission has not been granted, request it
           activity.requestPermissions(permissions, 112);
        }

        // Create a boolean with whether the permission has been granted
        boolean permission = (context.checkCallingOrSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED)
                || (context.checkCallingOrSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED);

        // Print permission status for debugging
        System.out.println("Check permission returned result: " + permission);

        // Return whether permission is granted
        return permission;
    }
}
