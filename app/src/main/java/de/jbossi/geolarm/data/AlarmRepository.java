package de.jbossi.geolarm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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


public class AlarmRepository implements Observer {

    private static AlarmRepository mInstance = null;
    private List<Alarm> mAlarms;
    private ObjectMapper mapper = new ObjectMapper();
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private GeofenceHandler mGeofenceHandler;
    private Stack<Geofence> mPendingGeofencesToAdd = new Stack<>();
    private Stack<Geofence> mPendingGeofencesToRemove = new Stack<>();

    public AlarmRepository(Context ctx) {
        mContext = ctx;
        mGeofenceHandler = new GeofenceHandler(mContext);
        mGeofenceHandler.addObserver(this);
        mAlarms = new ArrayList<>();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        mapper.addMixIn(LatLng.class, LatLngMixIn.class);
        mAlarms = getObjectsFromFile();
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

    private void saveObjectsToFile(List<Alarm> alarms) {

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
            if (mGeofenceHandler.getState() == GeofenceHandler.State.CONNECTED) {
                mGeofenceHandler.addGeofence(buildGeofence(alarm));
            } else {
                mPendingGeofencesToAdd.push(buildGeofence(alarm));
            }
        }
        saveObjectsToFile(mAlarms);
    }

    public void removeAlarm(Alarm alarm) {
        mAlarms.remove(alarm);
        if (mGeofenceHandler.getState() == GeofenceHandler.State.CONNECTED) {
            mGeofenceHandler.removeGeofence(alarm.getId());
        } else {
            mPendingGeofencesToRemove.push(buildGeofence(alarm));
        }
        saveObjectsToFile(mAlarms);
    }

    public void removeAlarm(String alarmId) {
        for (Alarm alarm : mAlarms) {
            if (alarm.getId() == alarmId) {
                mAlarms.remove(alarm);
                if (mGeofenceHandler.getState() == GeofenceHandler.State.CONNECTED) {
                    mGeofenceHandler.removeGeofence(alarmId);
                } else {
                    mPendingGeofencesToRemove.push(buildGeofence(alarm));
                }
                saveObjectsToFile(mAlarms);
            }
        }
    }

    public void removeAlarms() {
        mAlarms.clear();
        saveObjectsToFile(mAlarms);
    }

    public void save() {
        saveObjectsToFile(mAlarms);
    }

    public void disarmAlarm(String id) {
        for (Alarm alarm : mAlarms) {
            if (alarm.getId().equals(id)) {
                alarm.setArmed(false);
                if (mGeofenceHandler.getState() == GeofenceHandler.State.CONNECTED) {
                    mGeofenceHandler.removeGeofence(alarm.getId());
                } else {
                    mPendingGeofencesToRemove.push(buildGeofence(alarm));
                }
            }
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
