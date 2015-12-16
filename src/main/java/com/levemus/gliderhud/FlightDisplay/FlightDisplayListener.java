package com.levemus.gliderhud.FlightDisplay;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;
import com.levemus.gliderhud.FlightDisplay.IFlightDisplay;

/**
 * Created by mark@levemus on 15-12-03.
 */
public abstract class FlightDisplayListener implements IFlightDisplay, IFlightDataListener {
    @Override
    public void init(Activity activity) {}

    @Override
    public void display() {}

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster) {}

    @Override
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {}

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, BroadcasterStatus status) {}
}
