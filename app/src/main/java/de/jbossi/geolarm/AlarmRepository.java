package de.jbossi.geolarm;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Johannes on 30.06.2015.
 */
@Singleton
public class AlarmRepository {
    private List<Alarm> mAlarms;
    private SharedPreferences mPreferences;

    @Inject
    public AlarmRepository() {
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
