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
import com.levemus.gliderhud.FlightData.Listeners.DistanceFr;
import com.levemus.gliderhud.FlightData.Listeners.HeightAbv;
import com.levemus.gliderhud.FlightData.Listeners.TurnRate;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-18.
 */
public class HeightAbvLaunchDisplay extends MFDTextElement {
    private final String TAG = this.getClass().getSimpleName();

    public HeightAbvLaunchDisplay(FlightDisplay parent) {
        super(parent);
    }

    DistanceFr mDistanceFr = new DistanceFr(this);
    HeightAbv mHeight = new HeightAbv(this);
    TurnRate mTurnRate = new TurnRate(this);

    protected String title() {return "Height ABL (m)";}
    protected String value() {
        double height = 0;
        if(mHeight.value() > MIN_HEIGHT_ABVL) {
            height = Math.round(mHeight.value() * 10) / 10;
        }
        return Double.toString(height);
    }

    @Override
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster)
    {
        HashSet<UUID> result = new HashSet<>();
        result.addAll(mDistanceFr.registerWith(broadcaster));
        result.addAll(mHeight.registerWith(broadcaster));
        result.addAll(mTurnRate.registerWith(broadcaster));
        return result;
    }

    private double MIN_HEIGHT_ABVL = 10.0; // meters
    private double MAX_DISTANCE_FROM_LAUNCH = 5000; // meters
    private double MAX_TURN_RATE = 15;

    @Override
    public DisplayPriority displayPriority() {
        try {
            if(mHeight.value() < MIN_HEIGHT_ABVL || mDistanceFr.value() > MAX_DISTANCE_FROM_LAUNCH)
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
