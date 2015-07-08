package de.jbossi.geolarm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import de.jbossi.geolarm.models.Alarm;


public class AlarmRepository {
    private static AlarmRepository mInstance = null;
    private List<Alarm> mAlarms;
    private ObjectMapper mapper = new ObjectMapper();

    private SharedPreferences pref;

    public AlarmRepository(Context ctx) {
        mAlarms = new ArrayList<>() ;
       pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        mapper.addMixIn(LatLng.class, LatLngMixIn.class);
        mAlarms = GetObjectsFromFile();

    }




    public static AlarmRepository getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new AlarmRepository(ctx);
        }
        return mInstance;
    }





    public List<Alarm> GetObjectsFromFile() {

        String JSONObject = pref.getString("Alarms",new String());
        if (!JSONObject.isEmpty()) {
            Log.i("GET-JSON", JSONObject);
            try {
                mAlarms = mapper.readValue(JSONObject, new TypeReference<List<Alarm>>() {
                });



            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return mAlarms;

    }






public void SaveObjectsToFile(List<Alarm> alarms) {

        SharedPreferences.Editor editor = pref.edit();

   StringWriter writer = new StringWriter();

    try {
        mapper.writeValue(writer, mAlarms);
    } catch (IOException e) {
        e.printStackTrace();
    }
    editor.putString("Alarms", writer.toString() );
        editor.commit();
    }

    public List<Alarm> getmAlarms() {
        return mAlarms;
    }

    public void addAlarm(Alarm alarm) {
        mAlarms.add(alarm);
        SaveObjectsToFile(mAlarms);
    }

    public void removeAlarm(Alarm alarm) {
        mAlarms.remove(alarm);
        SaveObjectsToFile(mAlarms);
    }


}
