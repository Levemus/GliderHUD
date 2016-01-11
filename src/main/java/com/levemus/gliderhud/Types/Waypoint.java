package com.levemus.gliderhud.Types;

import android.location.Location;

import java.util.List;

/**
 * Created by markcarter on 16-01-03.
 */
public class Waypoint {
    public double mLatitude;
    public double mLongitude;
    public double mDistance;
    public Turnpoint mTurnPoint;
    public int mRadius;

    private void init(Double latitude, double longitude, Turnpoint turnpoint, int radius) {
        mLatitude = latitude;
        mLongitude = longitude;
        mTurnPoint = turnpoint;
        mRadius = radius;
        mDistance = 0;
    }

    public Waypoint(Double latitude, double longitude, Turnpoint turnpoint, int radius) {
        init(latitude, longitude, turnpoint, radius);
    }

    public Waypoint(Double latitude, double longitude, Turnpoint turnpoint, int radius, Waypoint previous) {
        init(latitude, longitude, turnpoint, radius);
        updateDistance(previous);
    }

    public Waypoint() {
        mTurnPoint = new Turnpoint();
        mLongitude = mLatitude  = mDistance = 0;
        mRadius = 0;
    }

    public void updateDistance(Waypoint previous)
    {
        if(previous == null) {
            mDistance = 0;
            return;
        }

        Location currentLocation = new Location("initial");
        currentLocation.setLatitude(previous.mLatitude);
        currentLocation.setLongitude(previous.mLongitude);
        Location targetLocation = new Location("target");
        targetLocation.setLatitude(mLatitude);
        targetLocation.setLongitude(mLongitude);
        mDistance = (double)currentLocation.distanceTo(targetLocation) + previous.mDistance;
    }

    @Override
    public String toString() {
        return this.mTurnPoint.mName + " - " + mRadius + " m (Distance: " + Math.round(mDistance / 100) / 10 + " km)";
    }

    public String serialize() {
        return (
                mTurnPoint.serialize() + "," // convert from feet
                + mRadius + ","
                + mLatitude + ","
                + mLongitude
        );
    }

    public String deserialize(String serializedLine) {

        mTurnPoint = new Turnpoint();
        serializedLine = mTurnPoint.deserialize(serializedLine);
        String[] tokens = serializedLine.split(",");
        mRadius = Integer.parseInt(tokens[0]);
        mLatitude = Double.parseDouble(tokens[1]);
        mLongitude = Double.parseDouble(tokens[2]);

        return serializedLine.replaceFirst(serialize(), "").replaceFirst("^,", "");
    }
}
