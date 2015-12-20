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
import com.levemus.gliderhud.Utils.Angle;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class TurnRate implements IFlightDataListener {

    IFlightDataClient mClient;

    private double mStartBearing;
    private long mStartTime;

    public TurnRate(IFlightDataClient client) {
        mStartBearing = Double.MIN_VALUE;
        mStartTime = -1;
        mClient = client;
    }

    public TurnRate(IFlightDataClient client, double startBearing) {
        mStartBearing = startBearing;
        mStartTime = -1;
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

    private double mTurnRate = 0;

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
                double value = data.get(FlightDataType.BEARING);

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

        if(mClient != null)
            mClient.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, BroadcasterStatus status) {}
}

