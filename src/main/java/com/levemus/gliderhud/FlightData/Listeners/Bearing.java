package com.levemus.gliderhud.FlightData.Listeners;

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 15-12-20.
 */
public class Bearing implements IFlightDataListener {

    IFlightDataClient mClient;

    public Bearing(IFlightDataClient client) {
        mClient = client;
    }

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataType.BEARING
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

    private double mBearing = 0;

    public double value() {
        return mBearing;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {

        try {
            mBearing = data.get(FlightDataType.BEARING);
        }
        catch(java.lang.UnsupportedOperationException e){}

        if(mClient != null)
            mClient.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, BroadcasterStatus status) {}
}
