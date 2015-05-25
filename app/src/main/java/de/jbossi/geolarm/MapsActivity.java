package de.jbossi.geolarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private MapFragment mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mlocationManager;
    private Marker mAlarmMarker;
    private ImageButton mFloatingActionButton;
    private EditText editLocation;


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
/*        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am =
                (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);*/
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


                ;
            } else {
                mAlarmMarker.setPosition(latLng);

            }
            onPickButtonClick(getCurrentFocus());
        }
    };

    public void showSetAlarmDialog() {

        MaterialDialog setUpDialog = new MaterialDialog.Builder(this).title("Alarm wählen!")
                .customView(R.layout.set_alarm_dialog, true)
                .positiveColor(R.color.primary_dark)
                .build();

        editLocation = (EditText) setUpDialog.getCustomView().findViewById(R.id.locationText);
        editLocation.setText(mAlarmMarker.getTitle());
        setUpDialog.show();
    }

    public void onPickButtonClick(View v) {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();

            editLocation.setText(name);


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
