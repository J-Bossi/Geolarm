package de.jbossi.geolarm;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import de.jbossi.geolarm.activities.AlarmReceiver;
import de.jbossi.geolarm.activities.MapsActivity;
import de.jbossi.geolarm.models.Alarm;



public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    public static final int AWAIT_TIMEOUT_IN_MILLISECONDS = 2000;
    public static final String TAG = MapsActivityTest.class.getSimpleName();

    private Solo solo;
    private MapsActivity activityUnderTest;

    public MapsActivityTest() throws ClassNotFoundException {
        super(MapsActivity.class);
    }


    protected void tearDown() throws Exception {

        solo.finishOpenedActivities();


        activityUnderTest = null;
        Log.i(TAG, "Finish Test");
        super.tearDown();
    }


    public void setUp() throws Exception {
        super.setUp();
        activityUnderTest = getActivity();
        ensureGoogleApiClientConnection();

        Log.i(TAG, "Setup MOCK Location Providers");
        //Set test location
        pushLocation(-12.34, 23.45, 1.0f);

       //locMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.502238, 13.484788), "1", 2000, true));

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


    }

    private void ensureGoogleApiClientConnection() {
        if (!activityUnderTest.mGoogleApiClient.isConnected()) {
            activityUnderTest.mGoogleApiClient.blockingConnect();
        }
    }



    public void testIsConncted() {
        assertEquals(true, activityUnderTest.mGoogleApiClient.isConnected());
    }

    public void testPathAlarm() throws InterruptedException {

        for (int i = 0; i < 30; i++){
            Log.i(TAG, String.format("Iterating over the location ... (%1$d)", i));
            pushLocation(52.499238 + (i * 0.0001f), 13.481788 + (i * 0.0001f), 1.0f);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }

        }
        solo.waitForActivity(AlarmReceiver.class);
        solo.assertCurrentActivity("alarm", AlarmReceiver.class);
    }

    public void pushLocation(final double lat,final double lon, final float acc) {
        // We use a CountDownLatch to ensure that all asynchronous tasks complete within setUp. We
        // set the CountDownLatch count to 1 and decrement this count only when we are certain that
        // mock location has been set.
        final CountDownLatch lock = new CountDownLatch(1);

        // First, ensure that the location provider is in mock mode. Using setMockMode() ensures
        // that only locations specified in setMockLocation(GoogleApiClient, Location) are used.
        LocationServices.FusedLocationApi.setMockMode(activityUnderTest.mGoogleApiClient, true).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.v(TAG, "Mock mode set");
                    Location mockLocation = new Location("MockLocation");
                    mockLocation.setLatitude(lat);
                    mockLocation.setLongitude(lon);
                    mockLocation.setAltitude(0);
                    mockLocation.setAccuracy(acc);
                    mockLocation.setTime(System.currentTimeMillis());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                    }
                    mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                    LocationServices.FusedLocationApi.setMockLocation(
                            activityUnderTest.mGoogleApiClient, mockLocation).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.v(TAG, "Mock location set");
                                // Decrement the count of the latch, releasing the waiting
                                // thread. This permits lock.await() to return.
                                Log.v(TAG, "Decrementing latch count");
                                lock.countDown();
                            } else {
                                Log.e(TAG, "Mock location not set");
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Mock mode not set");
                }
            }
        });

        try {
            // Make the current thread wait until the latch has counted down to zero.
            Log.v(TAG, "Waiting until the latch has counted down to zero");
            lock.await(AWAIT_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            Log.i(TAG, "Waiting thread awakened prematurely", exception);
        }
    }


}