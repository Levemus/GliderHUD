package com.levemus.gliderhud.FlightData.Listeners;

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataID;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;

import java.util.Arrays;
import java.util.HashMap;
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
            FlightDataID.BEARING
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

    private double mBearing = Double.MIN_VALUE;

    public double value() {
        if(mBearing != Double.MIN_VALUE)
            return mBearing;
        throw new java.lang.UnsupportedOperationException();
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {

        try {
            mBearing = data.get(FlightDataID.BEARING);
        }
        catch(java.lang.UnsupportedOperationException e){}

        if(mClient != null)
            mClient.onDataReady(false);
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {
        if(status.containsKey(FlightDataID.BEARING)
                && status.get(FlightDataID.BEARING) == BroadcasterStatus.Status.OFFLINE) {
            mBearing = Double.MIN_VALUE;
            if(mClient != null)
                mClient.onDataReady(true);
        }
    }
}
