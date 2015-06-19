package de.jbossi.geolarm;

import com.google.android.gms.location.places.Place;

/**
 * Created by Johannes on 26.05.2015.
 */
public class Alarm {

    private String name;
    private boolean armed;
    private Place mPlace;
    private float mDistance;


    public Place getPlace() {
        return mPlace;
    }

    public void setPlace(Place mPlace) {

        this.mPlace = mPlace;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float mDistance) {

        this.mDistance = mDistance;
    }


    public boolean isArmed() {
        return armed;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Alarm(String name, boolean armed, Place mPlace, float mDistance) {
        this.name = name;
        this.armed = armed;
        this.mPlace = mPlace;
        this.mDistance = mDistance;
    }
}
