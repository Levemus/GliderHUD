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

import com.levemus.gliderhud.FlightData.Broadcasters.IBroadcaster;
import com.levemus.gliderhud.FlightData.Listeners.Listener;
import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerID;
import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerFactory;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-18.
 */
public class GlideRatioDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final Double MIN_GLIDE = -0.01;
    private final Double CRITICAL_GLIDE = -3.0;
    private final Double MAX_TURN_RATE = 10.0;
    private final Double MAX_GLIDE_VALUE = -100.0;

    // Listeners
    private Listener<Double> mTurnRate = ListenerFactory.build(ListenerID.TURNRATE, this);
    private Listener<Double> mGlide = ListenerFactory.build(ListenerID.GLIDERATIO, this);

    // Initialization/registration
    public GlideRatioDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerWith(IBroadcaster broadcaster)
    {
        broadcaster.registerWith(mTurnRate, mTurnRate);
        broadcaster.registerWith(mGlide, mGlide);
    }

    // Operation
    @Override
    protected String title() {return "Glide";}

    @Override
    protected String value() {
        if(mGlide.value() >= MIN_GLIDE || mGlide.value() < MAX_GLIDE_VALUE)
            return "---";

        double displayGlide = Math.round(mGlide.value() * 100);
        displayGlide /= 100;
        return Double.toString(displayGlide);
    }

    @Override
    public MFDElement.DisplayPriority displayPriority() {
        try {
            if(mGlide.value() >= MIN_GLIDE || mGlide.value() < MAX_GLIDE_VALUE || mTurnRate.value() > MAX_TURN_RATE)
                return MFDElement.DisplayPriority.NONE;
            else if(mGlide.value() >= CRITICAL_GLIDE &&  mTurnRate.value() < MAX_TURN_RATE)
                return MFDElement.DisplayPriority.CRITICAL;
            else
                return MFDElement.DisplayPriority.MEDIUM;
        }catch(Exception e) {
            return DisplayPriority.NONE;
        }
    }
}
