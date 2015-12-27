package com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Listener;
import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerID;
import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerFactory;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-18.
 */
public class HeightAbvLaunchDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_HEIGHT_ABVL = 10.0; // meters
    private final double MAX_DISTANCE_FROM_LAUNCH = 5000; // meters
    private final double MAX_TURN_RATE = 15;

    // Listeners
    private Listener mDistanceFr = ListenerFactory.build(ListenerID.DISTANCEFR, this);
    private Listener mHeightAbv = ListenerFactory.build(ListenerID.HEIGHTABV, this);
    private Listener mTurnRate = ListenerFactory.build(ListenerID.TURNRATE, this);

    // Initialization/registration
    public HeightAbvLaunchDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster)
    {
        broadcaster.registerForData(mDistanceFr, mDistanceFr);
        broadcaster.registerForData(mHeightAbv, mHeightAbv);
        broadcaster.registerForData(mTurnRate, mTurnRate);
    }

    // Operation
    protected String title() {return "Height ABL (m)";}

    @Override
    protected String value() {
        double height = 0;
        if(mHeightAbv.value() > MIN_HEIGHT_ABVL) {
            height = Math.round(mHeightAbv.value() * 10) / 10;
        }
        return Double.toString(height);
    }

    @Override
    public DisplayPriority displayPriority() {
        try {
            if(mHeightAbv.value() < MIN_HEIGHT_ABVL || mDistanceFr.value() > MAX_DISTANCE_FROM_LAUNCH)
                return DisplayPriority.NONE;
            else if(mTurnRate.value() > MAX_TURN_RATE)
                return DisplayPriority.LOW;
            else
                return DisplayPriority.MEDIUM;
        }catch(Exception e) {
            return DisplayPriority.NONE;
        }
    }
}
