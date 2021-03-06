/*
package de.jbossi.geolarm;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.robotium.solo.Solo;

import de.jbossi.geolarm.activities.MapsActivity;


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
        Thread.sleep(10000);
        Log.i(TAG, "Finish Test");
        super.tearDown();
    }


    public void setUp() throws Exception {
        super.setUp();
        activityUnderTest = getActivity();
        ensureInstalledDependencies();
        ensurePermissionsAreSet();
        ensureNetworkIsAvailable();
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });
        solo = new Solo(getInstrumentation(), getActivity());

        assertTrue(solo.waitForActivity(MapsActivity.class));
        Thread.sleep(10000);
    }

    private void ensurePermissionsAreSet() {
        PackageManager pm = activityUnderTest.getPackageManager();

        assertEquals(PackageManager.PERMISSION_GRANTED, pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, activityUnderTest.getPackageName()));
        assertEquals(PackageManager.PERMISSION_GRANTED, pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, activityUnderTest.getPackageName()));
    }

    private void ensureNetworkIsAvailable() {
        LocationManager lm = (LocationManager) activityUnderTest.getSystemService(Context.LOCATION_SERVICE);
        assertTrue(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void ensureInstalledDependencies() {
        int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activityUnderTest);
        assertEquals(result, ConnectionResult.SUCCESS);
    }

    //positive
    public void testEndInFence() throws InterruptedException {
*/
/*        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));
        for (int i = 0; i < 30; i++) {
            Log.i(TAG, String.format("Iterating over the location ... (%1$d)", i));

            pushLocation(52.45400 + (i * 0.0001f), 13.52300 + (i * 0.0001f), 1.0f);
            Thread.sleep(750);

            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
        }

        assertTrue(solo.waitForActivity(AlarmReceiverActivity.class));*//*

    }

    //positive
    public void testStartInFence() throws InterruptedException {
*/
/*        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));
        for (int i = 0; i < 30; i++) {
            Log.i(TAG, String.format("Iterating over the location ... (%1$d)", i));

            pushLocation(52.45700 + (i * 0.0001f), 13.52600 + (i * 0.0001f), 1.0f);
            Thread.sleep(750);

            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
        }

        assertTrue(solo.waitForActivity(AlarmReceiverActivity.class));*//*

    }

    //positive
    public void testEnteringBeeline() throws InterruptedException {

 */
/*       activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));
        //100m n , 13.526000
        while (true) {
            pushLocation(52.45750, 13.52400, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52450, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52500, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52550, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52600, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52650, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52700, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52750, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
            pushLocation(52.45750, 13.52800, 1.0f);
            Thread.sleep(750);
            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
        }

        assertTrue(solo.waitForActivity(AlarmReceiverActivity.class));

*//*

    }


    //negative
    public void testMissingBeeline() throws InterruptedException {
*/
/*        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));
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
        if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
            fail();
        }*//*


    }

    //negative
    public void testUncertainLocations() throws InterruptedException {
*/
/*
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
        if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
            fail();
        }
*//*


    }

    //positive
    public void testFastSpeed() throws InterruptedException {
*/
/*        activityUnderTest.addAlarm(new Alarm("Test", new LatLng(52.45700, 13.52600), "1", 100, true));

        for (int i = 0; i < 30; i++) {
            Log.i(TAG, String.format("Iterating over the location ... (%1$d)", i));

            pushLocation(52.45400 + (i * 0.0002f), 13.52300 + (i * 0.0002f), 1.0f);
            Thread.sleep(10);

            if (solo.getCurrentActivity().getClass() == AlarmReceiverActivity.class) {
                break;
            }
        }
        assertTrue(solo.waitForActivity(AlarmReceiverActivity.class));*//*

    }

    public void pushLocation(final double lat, final double lon, final float acc) {

    */
/*    final CountDownLatch lock = new CountDownLatch(1);

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
        }*//*

    }


}*/
