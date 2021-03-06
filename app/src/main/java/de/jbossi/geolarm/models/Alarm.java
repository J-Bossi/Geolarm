package de.jbossi.geolarm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Johannes on 26.05.2015.
 */
public class Alarm {

    private String name;
    private boolean armed;
    @JsonProperty("position")
    private LatLng position;
    private String id;
    private float distance;

    public Alarm() {
    }

    public Alarm(String name, LatLng position, String id, float distance, boolean armed) {

        this.armed = armed;
        this.name = name;
        this.position = position;
        this.id = id;
        this.distance = distance;
    }

    public String getName() {

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


}
