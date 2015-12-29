package de.jbossi.geolarm.helper;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import de.jbossi.geolarm.services.GeofenceTransitionsIntentService;

public class GeofenceHandler extends Observable implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "repository-service";
    private static final String GEOFENCE_ENTERED = "GEOFENCE_ENTERED";

    public enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTED
    }

    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver mAlarmChangeReceiver;
    private Context mContext;
    private State mState;

    public GeofenceHandler(Context context) {

        mContext = context;

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mState = State.CONNECTING;

        mAlarmChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String requestId = intent.getExtras().getString("REQUEST_ID");
                removeGeofence(requestId);
//                mAlarmRepository.disarmAlarm(requestId);
//
                Log.i(TAG, "Geofence Entered. Trying to disarm geofence with ID: " + requestId);
            }
        };

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        broadcastManager.registerReceiver(mAlarmChangeReceiver, new IntentFilter(GEOFENCE_ENTERED));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mState = State.CONNECTED;
        notifyObservers(mState);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mState = State.DISCONNECTED;
        notifyObservers(mState);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mState = State.DISCONNECTED;
        notifyObservers(mState);
        Log.i(TAG, "GooglePlayServices Connection Failed: Status: " + connectionResult.getErrorCode());
        if (connectionResult.hasResolution() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
            mState = State.CONNECTING;
            notifyObservers(mState);
            Log.i(TAG, "Trying again to connect");
        }
    }

    public void removeGeofence(String geofenceId) {
        List<String> geofenceList = new ArrayList<>();
        geofenceList.add(geofenceId);
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceList);
    }

    public void removeAllAlarms() {
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent());
    }

    public void updateGeofence(Geofence geofence) {
        List<String> geofenceList = new ArrayList<>();
        geofenceList.add(geofence.getRequestId());
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceList);
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(geofence), getGeofencePendingIntent());
    }

    public void addGeofence(Geofence geofence) {
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(geofence),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
        Log.i(TAG, "Geofence Intent Result came back " + status.getStatusCode());
        try {
            Log.i(TAG, "Settings Location Mode Code: " + Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE));
        } catch (Settings.SettingNotFoundException e) {
            Log.i(TAG, "SettingNotFoundException");
            e.printStackTrace();
        }
    }

    public State getState() {
        return mState;
    }
}
