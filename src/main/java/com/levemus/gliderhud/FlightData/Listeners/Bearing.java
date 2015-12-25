package com.levemus.gliderhud.FlightData.Listeners;

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataID;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by markcarter on 15-12-20.
 */
public class Bearing implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    HashSet<UUID> mRequiredChannels = new HashSet(Arrays.asList(
            FlightDataID.BEARING
    ));

    @Override
    public List<HashSet<UUID>> requiredChannels() {
        return Arrays.asList(mRequiredChannels);
    }

    @Override
    public long notificationInterval() { return 100; }

    private final double INVALID =  Double.MIN_VALUE;
    private double mBearing = INVALID;

    public double value() {
        if(mBearing == INVALID)
            throw new java.lang.UnsupportedOperationException();
        return mBearing;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {

        try {
            mBearing = data.get(FlightDataID.BEARING);
        }
        catch(java.lang.UnsupportedOperationException e){}

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {
        if(status.containsKey(FlightDataID.BEARING)
                && status.get(FlightDataID.BEARING) == BroadcasterStatus.Status.OFFLINE) {
            mBearing = INVALID;
            for(IFlightDataClient client : mClients)
                client.onDataReady();
        }
    }

    @Override
    public UUID id() { return UUID.fromString("56e680e2-9f45-474a-bb10-1df4a786e1bf");}
}
