package de.jbossi.geolarm;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

public class MockLocationProvider {

    Context ctx;
    private LocationManager locationManager;
    public static final String TAG = MockLocationProvider.class.getSimpleName();

    public MockLocationProvider(Context ctx) {

        this.ctx = ctx;

        Log.i(TAG, "Setup MOCK Location Providers");
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        Log.i(TAG, "GPS Provider");
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false, false, false, false, false, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        Log.i(TAG, "Network Provider");
        locationManager.addTestProvider(LocationManager.NETWORK_PROVIDER, true, false, true, false, false, false, false, Criteria.POWER_MEDIUM, Criteria.ACCURACY_FINE);
        locationManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, true);



    }

    public void pushLocation(double lat, double lon, float acc) {


        Location mockNetworkProvider = new Location(LocationManager.NETWORK_PROVIDER);
        mockNetworkProvider.setLatitude(lat);
        mockNetworkProvider.setLongitude(lon);
        mockNetworkProvider.setAltitude(0);
        mockNetworkProvider.setAccuracy(acc);
        mockNetworkProvider.setTime(System.currentTimeMillis());
        mockNetworkProvider.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, mockNetworkProvider);

        Location mockGpsLocation = new Location(LocationManager.GPS_PROVIDER);
        mockGpsLocation.setLatitude(lat);
        mockGpsLocation.setLongitude(lon);
        mockGpsLocation.setAltitude(0);
        mockGpsLocation.setAccuracy(acc);
        mockGpsLocation.setTime(System.currentTimeMillis());
        mockGpsLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockGpsLocation);
        Log.i("MockLoc", "new Mock Location");
    }

    public void shutdown() {

        locationManager.removeTestProvider(LocationManager.NETWORK_PROVIDER);
        locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
    }
}