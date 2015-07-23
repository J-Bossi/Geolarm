package de.jbossi.geolarm;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.jbossi.geolarm.activities.AlarmList;
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
        ensureInstalledDependencies();
        ensureNetworkIsAvailable();
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });
        solo = new Solo(getInstrumentation(), getActivity());

        assertTrue(solo.waitForActivity(MapsActivity.class));
    }

    private void ensureGoogleApiClientConnection() {
        if (!activityUnderTest.mGoogleApiClient.isConnected()) {
            activityUnderTest.mGoogleApiClient.blockingConnect();
        }
    }

    private void ensureNetworkIsAvailable() {
        LocationManager lm = (LocationManager) activityUnderTest.getSystemService(Context.LOCATION_SERVICE);
        assertTrue(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void ensureInstalledDependencies() {
        int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activityUnderTest);
        assertEquals(result, ConnectionResult.SUCCESS);
    }


    public void testMockLocation()  throws InterruptedException{
        pushLocation(10.0, 10.0, 1.0f);
        Thread.sleep(5000);
        pushLocation(10.0, 10.0, 0.5f);
        Thread.sleep(5000);
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(activityUnderTest.mGoogleApiClient);
        Log.i(TAG,lastLocation.toString());

        assertEquals("Location Wrong", 10.0,lastLocation.getLatitude());
        assertEquals("Location Wrong", 10.0,lastLocation.getLongitude());
    }


    public void testEndInFence() throws InterruptedException {
        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.50224, 13.48479), "1", 50, true));
        for (int i = 0; i < 30; i++){
            Log.i(TAG, String.format("Iterating over the location ... (%1$d)", i));

            pushLocation(52.499238 + (i * 0.0001f), 13.481788 + (i * 0.0001f), 1.0f);
            Thread.sleep(750);

            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
        }

        assertTrue(solo.waitForActivity(AlarmReceiver.class));
    }

    public void testEnteringBeeline() throws InterruptedException {

        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));
        //100m n , 13.526000
        while (true) {
            pushLocation(52.45750, 13.52400, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52450, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52500, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52550, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52600, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52650, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52700, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52750, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
            pushLocation(52.45750, 13.52800, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
                break;
            }
        }

        assertTrue(solo.waitForActivity(AlarmReceiver.class));


    }

    //HTW Ziel
    //52.457000, 13.52600


    public void testMissingBeeline() throws InterruptedException {
        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));
        pushLocation(52.45750, 13.52400, 1.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52450, 1.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52500, 1.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52700, 1.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52750, 1.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52800, 1.0f);
        Thread.sleep(750);
        //100m n 52.457000, 13.526000
        if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
            fail();
        }

    }

    public void testUncertainLocations() throws InterruptedException {
        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));
        pushLocation(52.45750, 13.52400, 100.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52450, 100.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52500, 100.0f);
        Thread.sleep(750);

        pushLocation(52.45750, 13.52550, 100.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52600, 100.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52650, 100.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52700, 100.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52750, 100.0f);
        Thread.sleep(750);
        pushLocation(52.45750, 13.52800, 100.0f);
        Thread.sleep(750);
        if (solo.getCurrentActivity().getClass() == AlarmReceiver.class) {
            fail();
        }

    }


    public void testActivityChange() {

        solo.clickOnMenuItem(solo.getString(R.string.show_list));
        solo.waitForActivity(AlarmList.class);
        solo.assertCurrentActivity("list", AlarmList.class);
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
            lock.await(AWAIT_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            Log.i(TAG, "Waiting thread awakened prematurely", exception);
        }
    }


}