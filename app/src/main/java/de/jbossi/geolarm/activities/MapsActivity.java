package de.jbossi.geolarm.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Slider;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.Util;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.helper.GeofenceHandler;
import de.jbossi.geolarm.models.Alarm;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "MapsActivity";
    private int REQUEST_PLACE_PICKER = 1;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private MapFragment mMap; // Might be null if Google Play services APK is not available.
    private Place mPlace;
    private float mDistance;

    private GeofenceHandler mGeofenceHandler;

    private GoogleMap.OnMapLongClickListener onMapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        public void onMapLongClick(LatLng latLng) {
            startPlacePicker(latLng);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGeofenceHandler = new GeofenceHandler(this);

        ImageButton mFloatingActionButton = (ImageButton) findViewById(R.id.floatingActionButton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetAlarmDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mMap = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mMap.getMapAsync(this);
        } else {
            //TODO No Location -> No Map
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GooglePlayServices Connection Failed: Status: " + connectionResult.getErrorCode());
        if (connectionResult.hasResolution() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
            Log.i(TAG, "Trying again to connect");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list:
                Intent startListIntent = new Intent(this, AlarmListActivity.class);
                startActivity(startListIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onMapReady(GoogleMap map) {
        Log.i(TAG, "Google Map is ready");
        map.setMyLocationEnabled(true);
        if (mLastLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13));
        }
        map.setOnMapLongClickListener(onMapLongClickListener);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    public void showSetAlarmDialog() {
        final Dialog setUpDialog = new Dialog(this);
        setUpDialog.title("Alarm wählen!")
                .contentView(R.layout.set_alarm_dialog)
                .positiveAction(R.string.ok).negativeAction(R.string.cancel).show();

        final Slider slider = (Slider) setUpDialog.findViewById(R.id.setAlarmDialog_Distance);
        slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                mDistance = slider.getExactValue();
            }
        });
        mDistance = slider.getExactValue();

        setUpDialog.positiveActionClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAlarm(new Alarm(mPlace.getName(), mPlace.getLatLng(), mPlace.getId(), mDistance, true));
                setUpDialog.cancel();
            }
        });

        setUpDialog.negativeActionClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setUpDialog.cancel();
            }
        });

        TextView editLocation = (TextView) setUpDialog.findViewById(R.id.setAlarmDialog_Location);
        if (mPlace != null) {
            editLocation.setText(mPlace.getName());
        }
        setUpDialog.show();
    }

    public void addAlarm(Alarm alarm) {
        AlarmRepository.getInstance(getApplicationContext()).addAlarm(alarm);
        if (alarm.isArmed()) {
            Geofence geofence = buildGeofence(alarm);

            mGeofenceHandler.addGeofence(geofence);
        }
    }

    public void removeAlarm(String alarmId) {
        //Todo: Remove Geofences
        AlarmRepository.getInstance(getApplicationContext()).removeAlarm(alarmId);
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

            mPlace = PlacePicker.getPlace(data, this);
            showSetAlarmDialog();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Geofence buildGeofence(Alarm alarm) {
        return new Geofence.Builder()
                .setRequestId(alarm.getId())
                .setCircularRegion(
                        alarm.getPosition().latitude,
                        alarm.getPosition().longitude,
                        alarm.getDistance()//Distance in meters
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(10)
                .build();
    }
}
