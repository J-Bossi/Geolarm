package de.jbossi.geolarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;


public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private MapFragment mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mlocationManager;
    private Marker mAlarmMarker;
    private ImageButton mFloatingActionButton;
    private EditText editLocation;
    private Place place;
    int REQUEST_PLACE_PICKER = 1;


    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.SECOND, 5);
                        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Activity.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
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

}
