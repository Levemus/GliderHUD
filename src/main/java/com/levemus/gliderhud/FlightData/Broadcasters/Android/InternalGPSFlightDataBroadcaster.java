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

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataID;
import com.levemus.gliderhud.FlightData.FlightData;

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

    @Override
    public void init(Activity activity)
    {
        super.init(activity);
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
    public HashSet<UUID> supportedTypes() {
        return new HashSet(Arrays.asList(
                FlightDataID.LATITUDE,
                FlightDataID.LONGITUDE,
                FlightDataID.ALTITUDE,
                FlightDataID.GROUNDSPEED,
                FlightDataID.BEARING,
                FlightDataID.VARIO));
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        HashMap<UUID, Double> values = new HashMap<>();
        values.put(FlightDataID.LATITUDE, location.getLatitude());
        values.put(FlightDataID.LONGITUDE, location.getLongitude());
        values.put(FlightDataID.ALTITUDE, location.getAltitude());
        values.put(FlightDataID.GROUNDSPEED, location.getSpeed() * 3.6);
        values.put(FlightDataID.BEARING, (double)location.getBearing());
        if(mTimeOfLastUpdate != 0 && location.getTime() != mTimeOfLastUpdate) {
            values.put(FlightDataID.VARIO,
                    (location.getAltitude() - mLastAltitude) / (location.getTime() - mTimeOfLastUpdate) * 1000);
        }

        setOnline();
        notifyListenersOfData(new FlightData(values));
        mTimeOfLastUpdate = location.getTime();
        mLastAltitude = location.getAltitude();
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
