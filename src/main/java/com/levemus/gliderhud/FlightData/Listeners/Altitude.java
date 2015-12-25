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
public class Altitude implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    HashSet<UUID> mRequiredChannels = new HashSet(Arrays.asList(
            FlightDataID.ALTITUDE
    ));

    @Override
    public List<HashSet<UUID>> requiredChannels() {
        return Arrays.asList(mRequiredChannels);
    }

    @Override
    public long notificationInterval() { return 100; }

    private final double INVALID =  Double.MIN_VALUE;
    private double mAltitude = INVALID;

    public double value() throws java.lang.UnsupportedOperationException {
        if(mAltitude == INVALID)
            throw new java.lang.UnsupportedOperationException();
        return mAltitude;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {

        try {
            mAltitude = data.get(FlightDataID.ALTITUDE);
        }
        catch(java.lang.UnsupportedOperationException e){}

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {
        if(status.containsKey(FlightDataID.ALTITUDE)
                && status.get(FlightDataID.ALTITUDE) == BroadcasterStatus.Status.OFFLINE) {
            mAltitude = INVALID;
            for(IFlightDataClient client : mClients)
                client.onDataReady();
        }
    }

    @Override
    public UUID id() { return UUID.fromString("1646c2a9-a550-4590-aa76-a184e6ad3770");}
}
