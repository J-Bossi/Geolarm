package de.jbossi.geolarm;

import com.google.android.gms.location.places.Place;

/**
 * Created by Johannes on 26.05.2015.
 */
public class Alarm {
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

    public Alarm(Place mPlace, float mDistance) {
        this.mPlace = mPlace;
        this.mDistance = mDistance;
    }


}
