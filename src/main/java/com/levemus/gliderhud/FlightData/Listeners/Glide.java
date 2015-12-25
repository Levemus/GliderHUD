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
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class Glide implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    @Override
    public List<HashSet<UUID>> requiredChannels() {
        return Arrays.asList(
                mGroundSpeed.requiredChannels().get(0),
                mClimbRate.requiredChannels().get(0));
    }

    @Override
    public long notificationInterval() { return 100; }

    private GroundSpeed mGroundSpeed = new GroundSpeed();
    private ClimbRate mClimbRate = new ClimbRate();

    private final double INVALID =  Double.MIN_VALUE;
    private double mGlide = INVALID;

    public double value() {
        return mGlide;
    }

    private int AVG_CLIMB_WEIGHT = 5;
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        double climbRate = mClimbRate.value();
        double groundSpeed = mGroundSpeed.value() / 3.6;

        if(climbRate != 0 ) {
            mGlide = (AVG_CLIMB_WEIGHT - 1) * mGlide;
            mGlide += groundSpeed / climbRate;
            mGlide /= AVG_CLIMB_WEIGHT;
        }

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}

    @Override
    public UUID id() { return UUID.fromString("87038a0d-d55e-4d32-aed5-ee3c390ca21f");}
}

