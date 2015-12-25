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

import android.app.Activity;
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Listeners.ClimbRate;
import com.levemus.gliderhud.FlightData.Listeners.TurnRate;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-17.
 */
public class ClimbRateDisplay extends MFDTextElement {

    private final String TAG = this.getClass().getSimpleName();

    public ClimbRateDisplay(FlightDisplay parent) {
        super(parent);
    }

    private ClimbRate mClimbRate = new ClimbRate(this);
    private TurnRate mTurnRate = new TurnRate(this);

    protected String title() {return "Climb (m/s)";}
    protected String value() {
        double displayVario = 0;
        if (Math.abs(mClimbRate.value()) > MIN_VARIO) {
            displayVario = Math.round(mClimbRate.value() * 100);
            displayVario /= 100;
        }
        return Double.toString(displayVario);
    }

    @Override
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster)
    {
        HashSet<UUID> result = new HashSet<>();
        result.addAll(mTurnRate.registerWith(broadcaster));
        result.addAll(mClimbRate.registerWith(broadcaster));
        return result;
    }

    private double MIN_VARIO = 0.10;
    private double MIN_CLIMB_TURN_RATE = 10;
    private double HIGH_CLIMB_RATE = 3.0;
    @Override
    public DisplayPriority displayPriority() {
        try {
            if (mTurnRate.value() < MIN_CLIMB_TURN_RATE && mClimbRate.value() < 0)
                return DisplayPriority.LOW;
            else if (Math.abs(mTurnRate.value()) > HIGH_CLIMB_RATE)
                return DisplayPriority.HIGH;
            else
                return DisplayPriority.MEDIUM;
        }catch(Exception e) {
            return DisplayPriority.NONE;
        }
    }
}
