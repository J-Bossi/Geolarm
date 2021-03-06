package de.jbossi.geolarm.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import de.jbossi.geolarm.helper.GeofenceHandler;
import de.jbossi.geolarm.models.Alarm;


public class AlarmRepository extends Observable implements Observer {

    protected static final String TAG = "alarm-repository";

    private static final String GEOFENCE_ENTERED = "GEOFENCE_ENTERED";

    private static AlarmRepository mInstance = null;
    private List<Alarm> mAlarms;
    private ObjectMapper mapper = new ObjectMapper();
    private SharedPreferences mSharedPreferences;
    private GeofenceHandler mGeofenceHandler;
    private Stack<Geofence> mPendingGeofencesToAdd = new Stack<>();
    private Stack<Geofence> mPendingGeofencesToRemove = new Stack<>();

    public AlarmRepository(Context context) {
        mGeofenceHandler = new GeofenceHandler(context);
        mGeofenceHandler.addObserver(this);
        mAlarms = new ArrayList<>();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        mapper.addMixIn(LatLng.class, LatLngMixIn.class);
        mAlarms = getObjectsFromFile();

        BroadcastReceiver mAlarmChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String requestId = intent.getExtras().getString("REQUEST_ID");
                disarmAlarm(requestId);
                setChanged();
                notifyObservers(getPositionFromID(requestId));
                Log.i(TAG, "Geofence Entered. Trying to disarm geofence with ID: " + requestId);
            }
        };

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        broadcastManager.registerReceiver(mAlarmChangeReceiver, new IntentFilter(GEOFENCE_ENTERED));
    }

    public static AlarmRepository getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new AlarmRepository(ctx);
        }
        return mInstance;
    }

    private List<Alarm> getObjectsFromFile() {

        String JSONObject = mSharedPreferences.getString("Alarms", "");
        if (!JSONObject.isEmpty()) {
            Log.i("GET-JSON", JSONObject);
            try {
                mAlarms = mapper.readValue(JSONObject, new TypeReference<List<Alarm>>() {
                });
            } catch (JsonParseException | JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return mAlarms;

    }

    private void saveObjectsToFile() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        StringWriter writer = new StringWriter();

        try {
            mapper.writeValue(writer, mAlarms);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.putString("Alarms", writer.toString());
        editor.apply();
    }

    public List<Alarm> getmAlarms() {
        return mAlarms;
    }

    public void addAlarm(Alarm alarm) {
        mAlarms.add(alarm);
        if (alarm.isArmed()) {
            addGeofence(alarm);
        }
        saveObjectsToFile();
    }

    public void updateAlarm(Alarm alarm) {
        removeGeofence(alarm);
        mAlarms.get(getPositionFromID(alarm.getId())).setArmed(alarm.isArmed());
        mAlarms.get(getPositionFromID(alarm.getId())).setDistance(alarm.getDistance());
        mAlarms.get(getPositionFromID(alarm.getId())).setName(alarm.getName());
        mAlarms.get(getPositionFromID(alarm.getId())).setPosition(alarm.getPosition());
        addGeofence(alarm);
    }

    public void removeAlarm(String alarmId) {
        for (Alarm alarm : mAlarms) {
            if (alarm.getId().equals(alarmId)) {
                mAlarms.remove(alarm);
                removeGeofence(alarm);
                saveObjectsToFile();
            }
        }
    }

    public void save() {
        saveObjectsToFile();
    }

    public void disarmAlarm(String id) {
        for (Alarm alarm : mAlarms) {
            if (alarm.getId().equals(id)) {
                alarm.setArmed(false);
                removeGeofence(alarm);
            }
        }
    }

    public void rearmAlarm(String id) {
        for (Alarm alarm : mAlarms) {
            if (alarm.getId().equals(id)) {
                alarm.setArmed(true);
                addGeofence(alarm);
            }
        }
    }

    private void removeGeofence(Alarm alarm) {
        if (mGeofenceHandler.getState() == GeofenceHandler.State.CONNECTED) {
            mGeofenceHandler.removeGeofence(alarm.getId());
        } else {
            mPendingGeofencesToRemove.push(buildGeofence(alarm));
        }
    }

    private void addGeofence(Alarm alarm) {
        if (mGeofenceHandler.getState() == GeofenceHandler.State.CONNECTED) {
            mGeofenceHandler.addGeofence(buildGeofence(alarm));
        } else {
            mPendingGeofencesToAdd.push(buildGeofence(alarm));
        }
    }

    public int getPositionFromID(String requestId) {
        int i = 0;
        for (Alarm alarm : mAlarms) {
            if (alarm.getId().equals(requestId)) {
                return i;
            }
            i++;
        }
        return -1;
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


    @Override
    public void update(Observable observable, Object data) {
        if (observable != mGeofenceHandler) {
            return;
        }

        switch ((GeofenceHandler.State) data) {
            case CONNECTED:
                //Try to setup all Alarms which could not be setup in mean time
                for (Geofence geofence : mPendingGeofencesToAdd) {
                    mGeofenceHandler.addGeofence(geofence);
                }
                for (Geofence geofence : mPendingGeofencesToRemove) {
                    mGeofenceHandler.removeGeofence(geofence.getRequestId());
                }
                break;
            case CONNECTING:

                break;
            case DISCONNECTED:
        }
    }
}
