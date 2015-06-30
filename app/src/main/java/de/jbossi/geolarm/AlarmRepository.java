package de.jbossi.geolarm;

import android.content.SharedPreferences;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;



public class AlarmRepository {
    private List<Alarm> mAlarms;
    private SharedPreferences mPreferences;
    private List<Geofence> mGeofenceList = new ArrayList<>();

    public AlarmRepository() {
        loadAlarms();

    }

    private void loadAlarms() {
        mAlarms = new ArrayList<>();
    }

    public List<Alarm> getmAlarms() {
        return mAlarms;
    }

    public void setmAlarms(List<Alarm> mAlarms) {
        this.mAlarms = mAlarms;
    }

    public void addAlarm(Alarm alarm) {
        mAlarms.add(alarm);

    }

    public void removeAlarm(Alarm alarm) {
        mAlarms.remove(alarm);
    }


}
