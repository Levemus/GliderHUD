package com.levemus.gliderhud.FlightDisplay;

import com.levemus.gliderhud.FlightData.Broadcasters.IBroadcaster;

import java.util.Date;

/**
 * Created by mark@levemus on 15-12-20.
 */
public abstract class FlightDisplay implements IFlightDisplay, IClient {

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

    public abstract void registerWith(IBroadcaster broadcaster);
    public long getUpdateInterval() { return 500; } // milliseconds

    @Override
    public void hide() {}

}
