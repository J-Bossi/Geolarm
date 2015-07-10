package de.jbossi.geolarm;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

import de.greenrobot.event.EventBus;
import de.jbossi.geolarm.activities.AlarmReceiver;
import de.jbossi.geolarm.activities.MapsActivity;
import de.jbossi.geolarm.events.GeoFenceUpdateEvent;
import de.jbossi.geolarm.events.LocationUpdateEvent;
import de.jbossi.geolarm.models.Alarm;
import de.jbossi.geolarm.services.GeofenceTransitionsIntentService;


public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private Solo solo;

    private MapsActivity activityUnderTest;
    private MockLocationProvider mockLocation;
    private LocationManager locMgr;
    // private MockAlarmProvider mockAlarm;
    private static final String TAG = "Maps_Activity_Test";
    private int geofenceHits = 0;
    private int locationHits = 0;


    public MapsActivityTest() throws ClassNotFoundException {
        super(MapsActivity.class);
    }


    protected void tearDown() throws Exception {

        solo.finishOpenedActivities();
        mockLocation.shutdown();
        //   mockAlarm.removeAllAlarms();
        EventBus.getDefault().unregister(this);
        activityUnderTest = null;
        Log.i(TAG, "Finish Test");
        super.tearDown();
    }


    public void setUp() throws Exception {
        super.setUp();
        activityUnderTest = getActivity();

        mockLocation = new MockLocationProvider(getActivity());
        Log.i(TAG, "Setup MOCK Location Providers");
        //Set test location
        mockLocation.pushLocation(-12.34, 23.45, 1.0f);

        locMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.502238, 13.484788), "1", 2000, true));
      /*  LocationListener lis = new LocationListener() {
            public void onLocationChanged(Location location) {
                //You will get the mockLocation location
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


        };*/

     //   locMgr.requestLocationUpdates(
   //            LocationManager.NETWORK_PROVIDER, 1000, 1, lis);
        Log.i(TAG, "Requested Location Update");

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });

        solo = new Solo(getInstrumentation(), getActivity());
        Log.i(TAG, "Wait for activity");
        solo.waitForActivity("MapsActivity");
        Log.i(TAG, "Has Activity");
        EventBus.getDefault().register(this);
        //    solo.clickOnImageButton(0);
    }

    public void onEvent(final GeoFenceUpdateEvent event) {
        geofenceHits++;
        Log.i("Geofence","Geofence got hit");
    }

    public void onEvent(final LocationUpdateEvent event) {
       locationHits++;
    }


/*    public void testRun() {
        Log.i(TAG, "Run Actual Test");
        solo.waitForView(ImageButton.class);
        Log.i(TAG, "Has View Image Button");
        ImageButton imageButton = (ImageButton) solo.getView(R.id.floatingActionButton);
        Log.i(TAG, "Attempt to click on" + imageButton.getTransitionName());
        solo.clickOnView(imageButton);
        assertTrue(solo.waitForText("Entfernung"));

        //solo.assertCurrentActivity("act", MapsActivity.class);
//        solo.clickOnImageButton(0);
//        Log.i(TAG, "Clicked On imagebuttom");
        //assertTrue(solo.waitForDialogToOpen());
    }*/

    public void testExistingGeofence () {

    }

    public void testIsConncted() {
        assertEquals(true, activityUnderTest.mGoogleApiClient.isConnected());
    }

    public void testPathAlarm() throws InterruptedException {

        for (int i = 0; i < 30; i++){
            Log.i(TAG, String.format("Iterating over the location ... (%1$d)", i));
            mockLocation.pushLocation(52.499238+(i*0.0001f), 13.481788+(i*0.0001f), 1.0f);
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        mockLocation.pushLocation(1.0, 1.0, 1.0f);
        mockLocation.pushLocation(52.499238, 13.481788, 1.0f);

        assertTrue(geofenceHits > 0);
        solo.waitForActivity(AlarmReceiver.class);
        //solo.assertCurrentActivity("ma", MapsActivity.class);
        solo.assertCurrentActivity("alarm", AlarmReceiver.class);
    }


}