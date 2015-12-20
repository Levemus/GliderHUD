package com.levemus.gliderhud.FlightDisplay;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightDataClient;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 15-12-20.
 */
public abstract class FlightDisplay implements IFlightDisplay, IFlightDataClient {

    // IFlightDisplay
    @Override
    public abstract void init(Activity activity);
    @Override
    public abstract void display();

    // IFlightDataNotification
    private long mTimeOfLastUpdate = -1;
    @Override
    public void onDataReady() {
        long currentTime = new Date().getTime();
        if(mTimeOfLastUpdate == -1)
            mTimeOfLastUpdate = currentTime;

        if(currentTime - mTimeOfLastUpdate > getUpdateInterval()) {
            display();
            mTimeOfLastUpdate = currentTime;
        }
    }

    public abstract HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster);
    public long getUpdateInterval() { return 500; } // milliseconds


}
