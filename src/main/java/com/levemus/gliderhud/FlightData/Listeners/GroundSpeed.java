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
import com.levemus.gliderhud.FlightData.FlightDataID;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class GroundSpeed implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    HashSet<UUID> mRequiredChannels = new HashSet(Arrays.asList(
            FlightDataID.GROUNDSPEED
    ));

    @Override
    public List<HashSet<UUID>> requiredChannels() {
        return Arrays.asList(mRequiredChannels);
    }

    @Override
    public long notificationInterval() {return 100; }

    private final double INVALID =  Double.MIN_VALUE;
    private double mGroundSpeed = INVALID;

    public double value() throws java.lang.UnsupportedOperationException {
        if(mGroundSpeed == INVALID)
            throw new java.lang.UnsupportedOperationException();
        return mGroundSpeed;
    }

    private int AVG_GROUNDSPEED_WEIGHT = 5;
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        try {
            double value = data.get(FlightDataID.GROUNDSPEED);
            mGroundSpeed = (AVG_GROUNDSPEED_WEIGHT - 1) * mGroundSpeed;
            mGroundSpeed += value;
            mGroundSpeed /= AVG_GROUNDSPEED_WEIGHT;
        }
        catch(java.lang.UnsupportedOperationException e){}

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {
        if(status.containsKey(FlightDataID.GROUNDSPEED)
                && status.get(FlightDataID.GROUNDSPEED) == BroadcasterStatus.Status.OFFLINE) {
            mGroundSpeed = INVALID;
            for(IFlightDataClient client : mClients)
                client.onDataReady();
        }
    }

    @Override
    public UUID id() { return UUID.fromString("b75ae181-3229-4954-9915-690ef468b519");}
}

