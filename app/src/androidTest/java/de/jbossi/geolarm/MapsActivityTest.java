package de.jbossi.geolarm;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

import de.jbossi.geolarm.activities.AlarmReceiver;
import de.jbossi.geolarm.activities.MapsActivity;
import de.jbossi.geolarm.models.Alarm;


public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    private Solo solo;

    private MapsActivity activityUnderTest;
    private MockLocationProvider mockLocation;
    private LocationManager locMgr;
    // private MockAlarmProvider mockAlarm;
    private static final String TAG = "Maps_Activity_Test";


    public MapsActivityTest() throws ClassNotFoundException {
        super(MapsActivity.class);
    }


    protected void tearDown() throws Exception {

        solo.finishOpenedActivities();
        mockLocation.shutdown();
        //   mockAlarm.removeAllAlarms();

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
        //    solo.clickOnImageButton(0);
    }


    public void testRun() {
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
    }

    public void testAlarm() throws InterruptedException {

        mockLocation.pushLocation(51.502237, 12.484729, 1.0f);
        solo.sleep(1000);
        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.502238, 13.484788), "1", 100, true));

        for (int i = 0; i < 30; i++){
            Log.i(TAG, String.format("Iterating over our location ... (%1$d)", i));
            mockLocation.pushLocation(52.499238+(i*0.001f), 13.481788+(i*0.001f), 1.0f);
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }


        solo.waitForActivity(AlarmReceiver.class);
        //solo.assertCurrentActivity("ma", MapsActivity.class);
        solo.assertCurrentActivity("alarm", AlarmReceiver.class);
    }

    public void testAlarmReceiver() {
        // Set up an ActivityMonitor
        // Instrumentation.ActivityMonitor alarmReceiverActivityMonitor =
        //        getInstrumentation().addMonitor(AlarmReceiver.class.getName(),
        //                null, false);


        // set up a new Alarm with new Place

        // push new place

        // Validate that ReceiverActivity is started
        //AlarmReceiver alarmReceiver = (AlarmReceiver) alarmReceiverActivityMonitor.waitForActivity();
        // assertNotNull("AlarmReceiver is null!", alarmReceiver);
        //assertEquals("Monitor for ReceiverActivity has not been called",
        //        1, alarmReceiverActivityMonitor.getHits());

        // Remove the ActivityMonitor
        // getInstrumentation().removeMonitor(alarmReceiverActivityMonitor);
    }
}