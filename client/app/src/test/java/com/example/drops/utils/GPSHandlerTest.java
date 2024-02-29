package com.example.drops.utils;

import android.content.Context;
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class GPSHandlerTest {

    private GPSHandler gpsHandler;
    private Context context;

    @Before
    public void setup(){
        gpsHandler = new GPSHandler();
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testQueryFromView1(){
        LatLngBounds view = new LatLngBounds(new LatLng(-5, -5), new LatLng(5, 5));
        LatLngBounds query = gpsHandler.queryFromView(view, 1);
        assertTrue(view.equals(query));
    }

    @Test
    public void testQueryFromView2(){
        LatLngBounds view = new LatLngBounds(new LatLng(-5, -5), new LatLng(5, 5));
        LatLngBounds query = gpsHandler.queryFromView(view, 2);
        assertEquals(10, query.northeast.longitude, 0);
        assertEquals(10, query.northeast.latitude, 0);
        assertEquals(-10, query.southwest.longitude, 0);
        assertEquals(-10, query.southwest.latitude, 0);
    }

    @Test
    public void testQueryByRange1(){
        LatLng origin = new LatLng(0, 0);
        float range = 0;
        LatLngBounds area = gpsHandler.queryByRange(origin, range);
        assertEquals(origin, area.southwest);
        assertEquals(origin, area.northeast);
    }

    @Test
    public void testQueryByRange2(){
        LatLng origin = new LatLng(0, 0);
        float range = 111139;
        LatLngBounds area = gpsHandler.queryByRange(origin, range);
        assertEquals(1, area.northeast.longitude, 0);
        assertEquals(1, area.northeast.latitude, 0);
        assertEquals(-1, area.southwest.longitude, 0);
        assertEquals(-1, area.southwest.latitude, 0);
    }

    @Test
    public void testFilterLocation(){
        List<Location> list = new ArrayList<>();
        Location loc1 = new Location("");
        loc1.setLatitude(2);
        loc1.setLongitude(2);
        Location loc2 = new Location("");
        loc2.setLatitude(4);
        loc2.setLongitude(4);
        list.add(loc1);
        list.add(loc2);
        LatLng result = gpsHandler.filterLocation(list);
        assertEquals(3, result.latitude, 0);
        assertEquals(3, result.longitude, 0);
    }

    @Test
    public void testCheckBoundary1(){
        LatLngBounds q = new LatLngBounds(new LatLng(-5, -5), new LatLng(5, 5));
        LatLngBounds v = new LatLngBounds(new LatLng(-1, 1), new LatLng(1, 1));
        double fac = 0.1;
        assertFalse(gpsHandler.checkBoundary(v, q, fac));
    }

    @Test
    public void testCheckBoundary2(){
        LatLngBounds q = new LatLngBounds(new LatLng(-5, -5), new LatLng(5, 5));
        LatLngBounds v = new LatLngBounds(new LatLng(4, 4), new LatLng(5, 5));
        double fac = 0.1;
        assertTrue(gpsHandler.checkBoundary(v, q, fac));
    }

    @Test
    public void testCheckBoundary3(){
        LatLngBounds q = new LatLngBounds(new LatLng(-5, 175), new LatLng(5, 185));
        LatLngBounds v = new LatLngBounds(new LatLng(-1, 179), new LatLng(1, 181));
        double fac = 0.1;
        assertFalse(gpsHandler.checkBoundary(v, q, fac));
    }

    @Test
    public void testCheckBoundary4(){
        LatLngBounds q = new LatLngBounds(new LatLng(-5, 175), new LatLng(5, 185));
        LatLngBounds v = new LatLngBounds(new LatLng(4, 184), new LatLng(5, 185));
        double fac = 0.1;
        assertTrue(gpsHandler.checkBoundary(v, q, fac));
    }

}
