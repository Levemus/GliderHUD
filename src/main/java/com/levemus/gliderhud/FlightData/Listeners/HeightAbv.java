package com.levemus.gliderhud.FlightData.Listeners;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class HeightAbv implements IFlightDataListener {

    private double mStartAltitude;

    IFlightDataClient mClient;

    public HeightAbv(IFlightDataClient client) {
        mStartAltitude = Double.MIN_VALUE;
        mClient = client;
    }

    public HeightAbv(IFlightDataClient client, double startAltitude) {
        mStartAltitude = startAltitude;
        mClient = client;
    }

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataType.ALTITUDE
    ));

    private int UPDATE_INTERVAl_MS = 100;
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
        HashSet<UUID> result = new HashSet<>();
        if(!mSubscriptionFlags.isEmpty()) {
            result = broadcaster.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.removeAll(result);
        }

        return result;
    }

    private double mHeight = 0;

    public double value() {
        return mHeight;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {

        try {
            double altitude = data.get(FlightDataType.ALTITUDE);
            if(mStartAltitude == Double.MIN_VALUE)
                mStartAltitude = altitude;
            mHeight = altitude - mStartAltitude;
        }
        catch(java.lang.UnsupportedOperationException e){}

        if(mClient != null)
            mClient.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, BroadcasterStatus status) {}
}