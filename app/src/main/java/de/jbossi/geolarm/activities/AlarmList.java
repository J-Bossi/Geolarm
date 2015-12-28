package de.jbossi.geolarm.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.adapter.AlarmAdapter;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.services.GeofenceTransitionsIntentService;


/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmList extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "ACTIVITY AlarmList";
    public static final String GEOFENCE_ENTERED = "GEOFENCE_ENTERED";

    private RecyclerView mAlarmListRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private AlarmRepository mAlarmRepository;

    private GoogleApiClient mGoogleApiClient;

    private BroadcastReceiver mAlarmChangeReceiver;

    public AlarmList() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mAlarmChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String requestId = intent.getExtras().getString("REQUEST_ID");
                //removeGeofence(requestId);
                mAlarmRepository.disarmAlarm(requestId);
                mAlarmListRecyclerView.getAdapter().notifyItemChanged(mAlarmRepository.getPositionFromID(requestId));
                Log.i(TAG, "Geofence Entered. Trying to disarm geofence with ID: " + requestId);
            }
        };

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        broadcastManager.registerReceiver(mAlarmChangeReceiver, new IntentFilter(GEOFENCE_ENTERED));

        mAlarmRepository = AlarmRepository.getInstance(this);

        setContentView(R.layout.activity_list);
        mAlarmListRecyclerView = (RecyclerView) findViewById(R.id.alarm_list);

        mLayoutManager = new LinearLayoutManager(this);
        mAlarmListRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AlarmAdapter(mAlarmRepository.getmAlarms());

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                removeGeofence(mAlarmRepository.getmAlarms().get(position).getId());
                mAlarmRepository.getmAlarms().remove(position);
                mAlarmListRecyclerView.getAdapter().notifyItemRemoved(position);

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mAlarmListRecyclerView);

        mAlarmListRecyclerView.setAdapter(mAdapter);
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onConnected(Bundle bundle) {

    }

    public void onConnectionSuspended(int i) {

    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GooglePlayServices Connection Failed: Status: " + connectionResult.getErrorCode());
        if (connectionResult.hasResolution() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
            Log.i(TAG, "Trying again to connect");
        }
    }

    private void removeGeofence(String geofenceId) {
        List<String> geofenceList = new ArrayList<>();
        geofenceList.add(geofenceId);
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceList);
    }

    private void updateGeofence(Geofence geofence) {
        List<String> geofenceList = new ArrayList<>();
        geofenceList.add(geofence.getRequestId());
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceList);
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(geofence), getGeofencePendingIntent());
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

}
