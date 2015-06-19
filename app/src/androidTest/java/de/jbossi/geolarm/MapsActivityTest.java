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

import com.robotium.solo.Solo;


public class MapsActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    private Activity activityUnderTest;
    private MockLocationProvider mockLocation;
    private MockAlarmProvider mockAlarm;
    private static final String TAG = "Maps_Activity_Test";


    public MapsActivityTest() throws ClassNotFoundException {
        super(MapsActivity.class);
    }


    protected void tearDown() throws Exception {

        solo.finishOpenedActivities();
        mockLocation.shutdown();
        mockAlarm.removeAllAlarms();

        activityUnderTest = null;
        Log.i(TAG, "Finish Test");
        super.tearDown();
    }


    public void setUp() throws Exception {
        super.setUp();

        mockLocation = new MockLocationProvider("locationTestProvider1", getActivity());
        Log.i(TAG, "Setup MOCK Location Providers");
        //Set test location
        mockLocation.pushLocation(-12.34, 23.45, 1.0f);

        LocationManager locMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener lis = new LocationListener() {
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


        };

        locMgr.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 1, lis);
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

        mockLocation.pushLocation(52.502237, 13.484729, 1.0f);
        // solo.clickLongOnScreen(801.f, 801.f);


        solo.sleep(5000);
        // solo.assertCurrentActivity("pp", PlacePicker.class);
        //solo.clickInList(0); Does not work because external Activity, Robotium only uses internal
        //solo.clickOnScreen(50.f, 1000);
        mockLocation.pushLocation(52.502237, 13.484729, 1.0f);
        solo.assertCurrentActivity("ma", MapsActivity.class);
        //solo.assertCurrentActivity("alarm", AlarmReceiver.class);
    }

    public void testAlarmReceiver() {
        // Set up an ActivityMonitor
        Instrumentation.ActivityMonitor alarmReceiverActivityMonitor =
                getInstrumentation().addMonitor(AlarmReceiver.class.getName(),
                        null, false);


        // set up a new Alarm with new Place

        // push new place

        // Validate that ReceiverActivity is started
        AlarmReceiver alarmReceiver = (AlarmReceiver) alarmReceiverActivityMonitor.waitForActivity();
        assertNotNull("AlarmReceiver is null!", alarmReceiver);
        assertEquals("Monitor for ReceiverActivity has not been called",
                1, alarmReceiverActivityMonitor.getHits());

        // Remove the ActivityMonitor
        getInstrumentation().removeMonitor(alarmReceiverActivityMonitor);
    }
}