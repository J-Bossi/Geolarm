package de.jbossi.geolarm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class AlarmRepository {
    private List<Alarm> mAlarms;
    private static AlarmRepository mInstance = null;
    private Context m_Context;
    private static ObjectMapper m_ObjectMapper = new ObjectMapper();
    private Gson gson = new Gson();
    private String toSave = new String();

    public AlarmRepository(Context ctx) {
        mAlarms = new ArrayList<>();
        m_Context = ctx;
        //Todo Load from file

        mAlarms = GetObjectsFromFile();

    }




    public static AlarmRepository getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new AlarmRepository(ctx);
        }
        return mInstance;
    }





    public List<Alarm> GetObjectsFromFile() {
        Type datasetListType = new TypeToken<Collection<Alarm>>() {}.getType();

        if (!toSave.isEmpty()) {
            mAlarms = gson.fromJson(toSave, datasetListType);
            Log.i("Get", toSave);
        }
            return mAlarms;



    }

    public void SaveObjectsToFile(List<Alarm> alarms) {
        Type datasetListType = new TypeToken<Collection<Alarm>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
       toSave = gson.toJson(alarms,datasetListType );
        Log.i("Save", toSave);

    }





    public List<Alarm> getmAlarms() {
        return mAlarms;
    }

    public void setmAlarms(List<Alarm> mAlarms) {
        this.mAlarms = mAlarms;
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
