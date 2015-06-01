package de.jbossi.geolarm;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;


public class MapsActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    private MockLocationProvider mock;
    private static final String TAG = "Maps_Activity_Test";

    @SuppressWarnings("unchecked")
    public MapsActivityTest() throws ClassNotFoundException {
        super(MapsActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();

        mock = new MockLocationProvider("locationTestProvider2", getActivity());
        Log.i(TAG, "Setup MOCK Location Providers");
        //Set test location
        mock.pushLocation(-12.34, 23.45, 1.0f);

        LocationManager locMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener lis = new LocationListener() {
            public void onLocationChanged(Location location) {
                //You will get the mock location
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };

        locMgr.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 1, lis);
        Log.i(TAG, "Requested Location Update");
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
        mock.shutdown();
    }

    public void testRun() {
        //Wait for activity: 'com.example.ExampleActivty'
        Log.i(TAG, "Wait for activity");
        solo.waitForActivity("MapsActivity");
        Log.i(TAG, "Has Activity");
        solo.clickOnImageButton(0);

    }
}