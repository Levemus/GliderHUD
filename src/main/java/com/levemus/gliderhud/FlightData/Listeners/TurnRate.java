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
import com.levemus.gliderhud.Utils.Angle;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class TurnRate implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    private double mStartBearing;
    private long mStartTime;

    public TurnRate() {
        mStartBearing = Double.MIN_VALUE;
        mStartTime = -1;
    }

    public TurnRate(IFlightDataClient client, double startBearing) {
        mStartBearing = startBearing;
        mStartTime = -1;
    }

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
    private double mTurnRate = INVALID;

    public double value() {
        return Math.abs(mTurnRate);
    }

    private int AVG_TURNRATE_WEIGHT = 5;
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        try {
            long currentTime = new Date().getTime();

            if(mStartTime == -1)
                mStartTime = currentTime;

            if(mStartTime != currentTime) {
                double value = data.get(FlightDataID.BEARING);

                if(mStartBearing == Double.MIN_VALUE)
                    mStartBearing = value;

                mTurnRate = (AVG_TURNRATE_WEIGHT - 1) * mTurnRate;
                long deltaTime = (currentTime - mStartTime);
                mTurnRate += (Angle.delta(value, mStartBearing) * 1000) / deltaTime;
                mStartBearing = value;
                mTurnRate /= AVG_TURNRATE_WEIGHT;
                mStartTime = currentTime;
            }
        }
        catch(java.lang.UnsupportedOperationException e){}

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}

    @Override
    public UUID id() { return UUID.fromString("dfd91983-1528-4a6a-abb5-1a9ff05e4de7");}
}

