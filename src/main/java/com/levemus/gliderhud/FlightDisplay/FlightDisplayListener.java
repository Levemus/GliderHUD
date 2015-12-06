package com.levemus.gliderhud.FlightDisplay;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;
import com.levemus.gliderhud.FlightDisplay.IFlightDisplay;

/**
 * Created by mark@levemus on 15-12-03.
 */
public abstract class FlightDisplayListener implements IFlightDisplay, IFlightDataListener {
    @Override
    public abstract void init(Activity activity);

    @Override
    public abstract void display();

    @Override
    public abstract void registerWith(IFlightDataBroadcaster broadcaster);

    @Override
    public abstract void onData(IFlightData data);
}
