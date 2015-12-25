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
public class Orientation implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    HashSet<UUID> mRequiredChannels = new HashSet(Arrays.asList(
            FlightDataID.YAW
    ));

    @Override
    public List<HashSet<UUID>> requiredChannels() {
        return Arrays.asList(mRequiredChannels);
    }

    @Override
    public long notificationInterval() {return 100; }

    private final double INVALID =  Double.MIN_VALUE;
    private double mYaw = INVALID;

    public double yaw() {
        return mYaw;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {

        try {
            mYaw = data.get(FlightDataID.YAW);
        }
        catch(java.lang.UnsupportedOperationException e){}

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}

    @Override
    public UUID id() { return UUID.fromString("d45f0ed7-6e75-4bdf-b9c3-a559b1709e43");}
}
