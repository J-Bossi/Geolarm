package de.jbossi.geolarm.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import de.jbossi.geolarm.R;

public class SplashScreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_REQUEST = 1;

    private static final String TAG = "splash_screen";

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        checkAndRequestPermissions();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void checkAndRequestPermissions() {
        boolean needToRequestPerms = false;
        for (String permission : LOCATION_PERMS) {

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                needToRequestPerms = true;
            }
        }
        if (needToRequestPerms) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, PERMISSION_REQUEST);
        } else {
            mGoogleApiClient.connect();
        }
    }

    private void startMapsActivity() {
        Intent mapsIntent = new Intent(SplashScreenActivity.this, MapsActivity.class);
        startActivity(mapsIntent);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient.connect();
                } else {
                    //TODO: show Message witch says Permissions are needed
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startMapsActivity();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
