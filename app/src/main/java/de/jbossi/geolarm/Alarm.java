package de.jbossi.geolarm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Johannes on 26.05.2015.
 */
public class Alarm implements Serializable {

    private CharSequence name;
    private boolean armed;
    private LatLng position;
    private String id;
    private float distance;

    public Alarm() {
    }

    public Alarm(CharSequence name, LatLng position, String id, float distance, boolean armed) {

        this.armed = armed;
        this.name = name;
        this.position = position;
        this.id = id;
        this.distance = distance;
    }

    public CharSequence getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isArmed() {
        return armed;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(position.latitude);
        out.writeDouble(position.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        position = new LatLng(in.readDouble(), in.readDouble());
    }
}
