package de.jbossi.geolarm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks {

    private MapFragment mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mlocationManager;
    private Marker mAlarmMarker;
    private ImageButton mFloatingActionButton;
    private EditText editLocation;
    private Place place;
    private float distance;
    int REQUEST_PLACE_PICKER = 1;
    private List<Geofence> mGeofenceList;
    private List<Alarm> mAlarmList;
    protected static final String TAG = "main-activity";
    private GoogleApiClient mGoogleApiClient;


    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mMap.getMapAsync(this);
        mFloatingActionButton = (ImageButton) findViewById(R.id.floatingActionButton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetAlarmDialog();
            }
        });
        distance = 100;
        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
        mAlarmList = new ArrayList<>();
        mGeofenceList = new ArrayList<>();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)

                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */

    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "on conncetionsuspended callback???");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void onMapReady(GoogleMap map) {

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(getLastBestLocation(), 13));
        map.setOnMapLongClickListener(onMapLongClickListener);
        map.getUiSettings().setMapToolbarEnabled(false);


    }

    private GoogleMap.OnMapLongClickListener onMapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            if (mAlarmMarker == null) {
                MarkerOptions alarmMarkerOptions = new MarkerOptions().title("Standort")
                        .snippet("Hier halten Sie sich gerade auf")
                        .position(latLng);
                mAlarmMarker = mMap.getMap().addMarker(alarmMarkerOptions);



            } else {
                mAlarmMarker.setPosition(latLng);

            }
            onPickButtonClick(latLng);
        }
    };

    public void showSetAlarmDialog() {

        MaterialDialog setUpDialog = new MaterialDialog.Builder(this).title("Alarm wählen!")
                .customView(R.layout.set_alarm_dialog, true)
                .positiveText(R.string.ok).negativeText(R.string.cancel)
                .positiveColor(R.color.primary_dark).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        mAlarmList.add(new Alarm(place, distance));
                        Log.i(TAG, "Following place was set to the alarm List " + place.getName());
                        populateGeofenceList();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .build();


        editLocation = (EditText) setUpDialog.getCustomView().findViewById(R.id.locationText);
        if (place != null) editLocation.setText(place.getName());
        setUpDialog.show();
    }

    public void onPickButtonClick(LatLng latLng) {
        if (true) {

        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(Util.computeBounds(latLng));
            Intent intent = intentBuilder.build(this);

            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }

        }
    }


    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            place = PlacePicker.getPlace(data, this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }






    private LatLng getLastBestLocation() {

        Location locationGPS = mlocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mlocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude());
        } else {
            return new LatLng(locationNet.getLatitude(), locationNet.getLongitude());
        }
    }

    private void populateGeofenceList() {
        for (Alarm alarm : mAlarmList) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(alarm.getPlace().getId())

                    .setCircularRegion(
                            alarm.getPlace().getLatLng().latitude,
                            alarm.getPlace().getLatLng().longitude,
                            alarm.getDistance()//Distance in meters
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            Log.i(TAG, "new geofence " + alarm.getPlace().getName());
        }
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
        Log.i(TAG, "I have to to somethong with the onResult method " + status.getStatusMessage());
    }
}
