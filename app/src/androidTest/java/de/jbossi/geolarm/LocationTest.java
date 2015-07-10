package de.jbossi.geolarm;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;



public class LocationTest extends AndroidTestCase {
    private Context context;
    private LocationManager locManager;
    private MockLocationProvider mockLocation;
    private LocationListener locationListener;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getContext();


        locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.i("TAG",locManager.toString());
        mockLocation = new MockLocationProvider(context);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }
    public void testCurrentLocation() {
        try {
        mockLocation.pushLocation(52.218887, 21.024797, 1.0f);
       // locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Thread.sleep(100);

        assertEquals(21.024797, locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
        assertEquals(52.218887, locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());

        mockLocation.pushLocation(52.218887, 25.024797, 1.0f);


            Thread.sleep(100);


        assertEquals(25.024797, locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
        assertEquals(52.218887, locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mockLocation.shutdown();
    }
}