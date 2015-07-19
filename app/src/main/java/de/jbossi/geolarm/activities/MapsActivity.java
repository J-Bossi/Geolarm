package de.jbossi.geolarm.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.Util;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.models.Alarm;
import de.jbossi.geolarm.services.GeofenceTransitionsIntentService;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "main-activity";
    public GoogleApiClient mGoogleApiClient;
    int REQUEST_PLACE_PICKER = 1;
    private MapFragment mMap; // Might be null if Google Play services APK is not available.
    private ImageButton mFloatingActionButton;
    private EditText editLocation;
    private Place place;
    private float distance;
    private Location mLastLocation;
    private boolean mSuccess = false;
    private GoogleMap.OnMapLongClickListener onMapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        public void onMapLongClick(LatLng latLng) {
            startPlacePicker(latLng);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)

                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        try {
            Log.i(TAG, "LocationMode qwertz "+ Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
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

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list:

                Intent startListIntent = new Intent(this, AlarmList.class);
                startActivityForResult(startListIntent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onMapReady(GoogleMap map) {


        Log.i(TAG, "Startin onMapReady");
        map.setMyLocationEnabled(true);
        if (mLastLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13));
        }
        map.setOnMapLongClickListener(onMapLongClickListener);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    public void showSetAlarmDialog() {

        MaterialDialog setUpDialog = new MaterialDialog.Builder(this).title("Alarm wählen!")
                .customView(R.layout.set_alarm_dialog, true)
                .positiveText(R.string.ok).negativeText(R.string.cancel)
                .positiveColor(R.color.primary_dark).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addAlarm(new Alarm(place.getName(), place.getLatLng(), place.getId(), distance, true));


                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                .build();


        editLocation = (EditText) setUpDialog.getCustomView().findViewById(R.id.locationText);
        if (place != null) {
            editLocation.setText(place.getName());
        }
        setUpDialog.show();
    }

    public void addAlarm(Alarm alarm) {
        AlarmRepository.getInstance(getApplicationContext()).addAlarm(alarm);
        Geofence geofence = buildGeofence(alarm);

        LocationServices.GeofencingApi.addGeofences(

                mGoogleApiClient,
                getGeofencingRequest(geofence),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    public void startPlacePicker(LatLng latLng) {


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


    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {


            place = PlacePicker.getPlace(data, this);
            showSetAlarmDialog();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private Geofence buildGeofence(Alarm alarm) {


        return new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(alarm.getId())

                .setCircularRegion(
                        alarm.getPosition().latitude,
                        alarm.getPosition().longitude,
                        alarm.getDistance()//Distance in meters
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();


    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
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
        Log.i(TAG, "Geofence Intent Result came back " + status.getStatusCode());
        try {
            Log.i(TAG, "Settings Location Mode Code: "+ Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE));
        } catch (Settings.SettingNotFoundException e) {
            Log.i(TAG, "SettingNotFoundException");
            e.printStackTrace();
        }
        if (status.getStatusCode() == 0) {
            mSuccess = true;
        } else {
            mSuccess = false;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(TAG, "GooglePlayServices Connection Failed: Status: " +connectionResult.getErrorCode());
        if (connectionResult.hasResolution() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
            Log.i(TAG, "Trying again to connect qwertz ");
        }
    }
}
