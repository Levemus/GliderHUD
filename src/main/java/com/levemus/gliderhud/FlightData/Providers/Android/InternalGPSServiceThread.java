package com.levemus.gliderhud.FlightData.Providers.Android;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public class InternalGPSServiceThread extends ServiceProviderThread implements LocationListener, IChannelized, IIdentifiable {

    private final String TAG = this.getClass().getSimpleName();

    public InternalGPSServiceThread(String id) {
        super(id);
    }

    @Override
    public void run() {
        Looper.prepare();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = (Bundle)msg.obj;
                SerializablePayloadMessage message = (SerializablePayloadMessage) bundle.getSerializable("MSG");
                onRequest(message);
            }
        };

        try {
            mLocationManager = (LocationManager)mParent.getSystemService(Context.LOCATION_SERVICE);

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
        Looper.loop();
    }

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // meters
    private static final long MIN_TIME_BW_UPDATES = 200 * 1 * 1; // milliseconds
    private final double MAX_ACCURACY = 20.0; // meters

    private LocationManager mLocationManager;

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
        sendResponse(new DataMessage(id(), new HashSet(values.keySet()), location.getTime(), values));
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
