package com.levemus.gliderhud.FlightData.Providers.Android;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Configuration.ChannelEntity;
import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class InternalGPSProvider extends Provider implements LocationListener, ChannelEntity {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // meters
    private static final long MIN_TIME_BW_UPDATES = 200 * 1 * 1; // milliseconds
    private final double MAX_ACCURACY = 20.0; // meters

    private LocationManager mLocationManager;

    @Override
    public void start(Context ctx) {
        try {
            mLocationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);

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
    public void onLocationChanged(android.location.Location location) {
        if(location.hasAccuracy() && location.getAccuracy() > MAX_ACCURACY)
            return;
        HashMap<UUID, Double> values = new HashMap<>();
        values.put(Channels.LATITUDE, location.getLatitude());
        values.put(Channels.LONGITUDE, location.getLongitude());
        values.put(Channels.BEARING, (double)location.getBearing());
        values.put(Channels.GROUNDSPEED, (double)location.getSpeed());
        values.put(Channels.GPSALTITUDE, (double)location.getAltitude());
        values.put(Channels.TIME, (double)location.getTime());
        if(mClient != null)
            mClient.onMsg(new DataMessage(id(), new HashSet(values.keySet()), new Date().getTime(), values));
    }

    public UUID id() {
        return UUID.fromString("6b68b893-3bd1-48b2-84d7-de1dd2d73617");
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                Channels.LATITUDE,
                Channels.LONGITUDE,
                Channels.BEARING,
                Channels.GROUNDSPEED,
                Channels.GPSALTITUDE,
                Channels.TIME));
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
