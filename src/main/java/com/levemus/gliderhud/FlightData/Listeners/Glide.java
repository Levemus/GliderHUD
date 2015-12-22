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

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class Glide implements IFlightDataListener {

    IFlightDataClient mClient;

    public Glide(IFlightDataClient client) {
        mClient = client;
        mGroundSpeed = new GroundSpeed(mClient);
        mClimbRate = new ClimbRate(mClient);
    }

    private int UPDATE_INTERVAl_MS = 100;
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
        HashSet<UUID> result = new HashSet<>();
        result.addAll(mClimbRate.registerWith(broadcaster));
        result.addAll(mGroundSpeed.registerWith(broadcaster));
        broadcaster.addListener(this, UPDATE_INTERVAl_MS, result); /* notify me when climb
        and groundspeed components are updated */
        return result;
    }

    private GroundSpeed mGroundSpeed;
    private ClimbRate mClimbRate;

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

        if(mClient != null)
            mClient.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}
}

