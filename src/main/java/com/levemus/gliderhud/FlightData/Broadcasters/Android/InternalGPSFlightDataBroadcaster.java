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
import java.util.EnumSet;

import android.app.Activity;
import android.content.Context;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightData;

/**
 * Created by flyinorange on 15-11-23.
 */
public class InternalGPSFlightDataBroadcaster extends FlightDataBroadcaster implements LocationListener {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 1 meters
    private static final long MIN_TIME_BW_UPDATES = 500 * 1 * 1; // 0.5 seconds

    private LocationManager mLocationManager;

    private EnumSet<IFlightData.FlightDataType> mSupportedTypes = EnumSet.of(
            IFlightData.FlightDataType.VARIO);

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
    public EnumSet<IFlightData.FlightDataType> supportedTypes()
    {
        return mSupportedTypes;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        notifyListeners(new GPSFlightData(location));
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

        public GPSFlightData (Location location)
        {
            mLocation = location;
        }

        @Override
        public double get(FlightDataType type) throws java.lang.UnsupportedOperationException
        {
            try {
                if (type == FlightDataType.ALTITUDE)
                    return mLocation.getAltitude();

                if (type == FlightDataType.GROUNDSPEED)
                    return Math.round((mLocation.getSpeed() * 3.6f) * 10) / 10;

                if (type == FlightDataType.BEARING)
                    return mLocation.getBearing();
            }
            catch(Exception e) {}
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public EnumSet<FlightDataType> supportedTypes() {
            return EnumSet.of(
                    IFlightData.FlightDataType.ALTITUDE,
                    IFlightData.FlightDataType.GROUNDSPEED,
                    IFlightData.FlightDataType.BEARING);
        }
    }
}
