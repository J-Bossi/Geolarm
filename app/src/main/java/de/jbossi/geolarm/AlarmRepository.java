package de.jbossi.geolarm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.util.ArrayList;
import java.util.List;



public class AlarmRepository {
    private List<Alarm> mAlarms = new ArrayList<>();
    ;
    private static AlarmRepository mInstance = null;
    private Context m_Context;
    private static ObjectMapper m_ObjectMapper = new ObjectMapper();

    public AlarmRepository(Context ctx) {

        m_Context = ctx;
        //Todo Load from file

        mAlarms = GetObjectsFromFile(Alarm.class, "Alarms");

    }


    private void SaveObjectsToFile() {

        SaveObjectsToFile(mAlarms, "Alarms");
    }

    public static AlarmRepository getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new AlarmRepository(ctx);
        }
        return mInstance;
    }

    public File getAppRootDir() {
        File appRootDir;
        boolean externalStorageAvailable;
        boolean externalStorageWriteable;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }

        if (externalStorageAvailable && externalStorageWriteable) {
            appRootDir = m_Context.getExternalFilesDir(null);
        } else {
            appRootDir = m_Context.getDir("appRootDir", Context.MODE_PRIVATE);
        }

        if (!appRootDir.exists()) {
            appRootDir.mkdir();
        }

        return appRootDir;
    }

    private String LoadFromExternal(String fileName) {
        String res = null;
        File file = new File(getAppRootDir(), fileName);

        if (!file.exists()) {
            Log.e("", "file " + file.getAbsolutePath() + " not found");
            return null;
        }

        FileInputStream fis = null;
        BufferedReader inputReader = null;

        try {
            fis = new FileInputStream(file);
            inputReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder strBuilder = new StringBuilder();
            String line;

            while ((line = inputReader.readLine()) != null) {
                strBuilder.append(line + "\n");
            }

            res = strBuilder.toString();
        } catch (Throwable e) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {

                }
            }

            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException ignored) {

                }
            }
        }
        return res;
    }

    public <T> List<T> GetObjectsFromFile(Class<T> classType, String fileName) {
        ArrayList<T> objects = new ArrayList<T>();
        String objectJSON = LoadFromExternal(fileName + ".JSON");

        if (objectJSON != null) {
            try {
                JSONArray objectJSONArray = new JSONArray(objectJSON);

                for (int i = 0; i < objectJSONArray.length(); i++) {
                    JSONObject plantObject = objectJSONArray.getJSONObject(i);
                    T object = m_ObjectMapper.readValue(plantObject.toString(), classType);
                    objects.add(object);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return objects;
    }

    public <T> void SaveObjectsToFile(List<T> objects, String fileName) {
        SaveToExternal(SerializeObject(objects), fileName + ".JSON");
    }

    private <T> String SerializeObject(List<T> objects) {
        String str = null;
        try {
            str = m_ObjectMapper.writerWithType(new TypeReference<List<T>>() {
            }).writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void SaveToExternal(String content, String fileName) {
        FileOutputStream fos = null;
        Writer out = null;

        try {
            File file = new File(getAppRootDir(), fileName);
            fos = new FileOutputStream(file);
            out = new OutputStreamWriter(fos, "UTF-8");

            out.write(content);
            out.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    public List<Alarm> getmAlarms() {
        return mAlarms;
    }

    public void setmAlarms(List<Alarm> mAlarms) {
        this.mAlarms = mAlarms;
    }

    public void addAlarm(Alarm alarm) {
        mAlarms.add(alarm);
        SaveObjectsToFile();

    }

    public void removeAlarm(Alarm alarm) {
        mAlarms.remove(alarm);
        SaveObjectsToFile();
    }


    // read the object from file
    // save the object to file



}
