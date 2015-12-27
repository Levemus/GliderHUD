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
 * Created by mark@levemus on 15-12-17.
 */
public class ClimbRateDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_VARIO = 0.10;
    private final double MIN_CLIMB_TURN_RATE = 10;
    private final double HIGH_CLIMB_RATE = 3.0;

    // Listeners
    private Listener mClimbRate = ListenerFactory.build(ListenerID.VARIO, this);
    private Listener mTurnRate = ListenerFactory.build(ListenerID.TURNRATE, this);

    // Initialization/registration
    public ClimbRateDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster)
    {
        broadcaster.registerForData(mTurnRate, mTurnRate);
        broadcaster.registerForData(mClimbRate, mClimbRate);
    }

    // Operation
    @Override
    protected String title() {return "Climb (m/s)";}

    @Override
    protected String value() {
        double displayVario = 0;
        if (Math.abs(mClimbRate.value()) > MIN_VARIO) {
            displayVario = Math.round(mClimbRate.value() * 100);
            displayVario /= 100;
        }
        return Double.toString(displayVario);
    }

    @Override
    public MFDElement.DisplayPriority displayPriority() {
        try {
            if (mTurnRate.value() < MIN_CLIMB_TURN_RATE && mClimbRate.value() < 0)
                return MFDElement.DisplayPriority.LOW;
            else if (Math.abs(mTurnRate.value()) > HIGH_CLIMB_RATE)
                return MFDElement.DisplayPriority.HIGH;
            else
                return MFDElement.DisplayPriority.MEDIUM;
        }catch(Exception e) {
            return MFDElement.DisplayPriority.NONE;
        }
    }
}
