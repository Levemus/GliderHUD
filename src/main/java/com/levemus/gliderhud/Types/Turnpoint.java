package com.levemus.gliderhud.Types;

/**
 * Created by markcarter on 16-01-03.
 */
public class Turnpoint {
    String mName;
    public double mLatitude;
    public double mLongitude;
    public double mAltitude;

    public Turnpoint(String name, Double latitude, double longitude, double altitude) {
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
        mAltitude = altitude;
    }

    public Turnpoint() {
        mName = "";
        mLatitude = mLongitude = mAltitude = 0;
    }

    @Override
    public String toString() {
        return mName;
    }

    public String serialize() {
        return (
                mName + ","
                        + mLatitude + ","
                        + mLongitude + ","
                        + mAltitude
        );
    }

    public String deserialize(String serializedLine) {
        String[] tokens = serializedLine.split(",");
        mName = tokens[0];
        mLatitude = Double.parseDouble(tokens[1]);
        mLongitude =Double.parseDouble(tokens[2]);
        mAltitude = Double.parseDouble(tokens[3]);
        return serializedLine.replaceFirst(serialize(), "").replaceFirst("^,", "");
    }
}
