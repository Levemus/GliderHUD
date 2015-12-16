package com.levemus.gliderhud.FlightData.Broadcasters.Android;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.IFlightData;

/**
 * Created by flyinorange on 15-11-23.
 */
public class InternalGPSFlightDataBroadcaster extends FlightDataBroadcaster implements LocationListener {

    private final String TAG = this.getClass().getSimpleName();

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // meters
    private static final long MIN_TIME_BW_UPDATES = 200 * 1 * 1; // milliseconds

    private LocationManager mLocationManager;

    private long mTimeOfLastUpdate = 0;
    private double mLastAltitude = 0;

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataType.VARIO));

    @Override
    public void init(Activity activity)
    {
        try {
            mLocationManager = (LocationManager) activity
                    .getSystemService(Context.LOCATION_SERVICE);

            if (mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d(TAG, "GPS Enabled");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashSet<UUID> supportedTypes()
    {
        return new GPSFlightData().supportedTypes();
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        notifyListeners(new GPSFlightData(location, mLastAltitude, mTimeOfLastUpdate));
        mTimeOfLastUpdate = location.getTime();
        mLastAltitude = location.getAltitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private class GPSFlightData implements IFlightData {

        private Location mLocation = null;

        private long mTimeOfLastUpdate = 0;
        private double mLastAltitude = 0;

        public GPSFlightData() {} // to get around lack of statics in interfaces while accessing supported types

        public GPSFlightData (Location location, double lastAltitude, long timeOfLastUpdate ) {
            mLastAltitude = lastAltitude;
            mTimeOfLastUpdate = timeOfLastUpdate;
            mLocation = location;
        }

        @Override
        public double get(UUID type) throws java.lang.UnsupportedOperationException
        {
            try {
                if (type == FlightDataType.ALTITUDE)
                    return mLocation.getAltitude();

                if (type == FlightDataType.GROUNDSPEED)
                    return Math.round((mLocation.getSpeed() * 3.6f) * 10) / 10;

                if (type == FlightDataType.BEARING)
                    return mLocation.getBearing();

                if (type == FlightDataType.VARIORAW) {
                    if(mTimeOfLastUpdate == 0 || mLocation.getTime() == mTimeOfLastUpdate)
                        new java.lang.UnsupportedOperationException(); // TODO: Need a better exception to indicate bad data
                    return(mLocation.getAltitude() - mLastAltitude) / (mLocation.getTime() - mTimeOfLastUpdate) * 1000;
                }

            }
            catch(Exception e) {}
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public HashSet<UUID> supportedTypes() {
            return new HashSet(Arrays.asList(
                    FlightDataType.ALTITUDE,
                    FlightDataType.GROUNDSPEED,
                    FlightDataType.BEARING,
                    FlightDataType.VARIORAW));
        }
    }
}
