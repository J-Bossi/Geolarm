package de.jbossi.geolarm;

import com.google.android.gms.location.places.Place;

/**
 * Created by Johannes on 26.05.2015.
 */
public class Alarm {
    private Place mPlace;
    private float mDistance;

    public Place getmPlace() {
        return mPlace;
    }

    public void setmPlace(Place mPlace) {
        this.mPlace = mPlace;
    }

    public float getmDistance() {
        return mDistance;
    }

    public void setmDistance(float mDistance) {
        this.mDistance = mDistance;
    }

    public Alarm(Place mPlace, float mDistance) {
        this.mPlace = mPlace;
        this.mDistance = mDistance;
    }
}
