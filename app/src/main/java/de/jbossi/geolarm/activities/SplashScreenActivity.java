package de.jbossi.geolarm.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import de.jbossi.geolarm.R;

public class SplashScreenActivity extends Activity {

    private static final int PERMISSION_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        String [] permissions = new String[2];
        permissions [0] = Manifest.permission.ACCESS_COARSE_LOCATION;
        permissions [1] = Manifest.permission.ACCESS_FINE_LOCATION;
        //permissions [2] = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Intent mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Start the MapActivity

                } else {
                    //TODO: show Message witch says Permissions are needed
                }

            }

        }
    }

}
