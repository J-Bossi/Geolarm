package de.jbossi.geolarm;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ImageButton;

import com.robotium.solo.Solo;


public class MapsActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    private Activity activityUnderTest;
    private MockLocationProvider mock;
    private static final String TAG = "Maps_Activity_Test";

    @SuppressWarnings("unchecked")
    public MapsActivityTest() throws ClassNotFoundException {
        super(MapsActivity.class);
    }


    protected void tearDown() throws Exception {

        solo.finishOpenedActivities();
        mock.shutdown();
        activityUnderTest = null;
        Log.i(TAG, "Finish Test");
        super.tearDown();
    }


    public void setUp() throws Exception {
        super.setUp();

        mock = new MockLocationProvider("locationTestProvider29", getActivity());
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
        solo = new Solo(getInstrumentation(), getActivity());

        Log.i(TAG, "Wait for activity");
        solo.waitForActivity("MapsActivity");
        Log.i(TAG, "Has Activity");
        //    solo.clickOnImageButton(0);
    }


    public void testRun() {
        Log.i(TAG, "Run Actual Test");
        solo.waitForView(ImageButton.class);
        Log.i(TAG, "Has View Image Button");
        ImageButton imageButton = (ImageButton) solo.getView(R.id.floatingActionButton);
        Log.i(TAG, "Attempt to clock on" + imageButton.getTransitionName());
        solo.clickOnView(imageButton);

        //solo.assertCurrentActivity("act", MapsActivity.class);
//        solo.clickOnImageButton(0);
//        Log.i(TAG, "Clicked On imagebuttom");
        assertTrue(solo.waitForDialogToOpen());


    }
}