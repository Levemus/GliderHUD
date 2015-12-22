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
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-18.
 */
public class DistanceFrLaunchDisplay extends MFDElement {
    private final String TAG = this.getClass().getSimpleName();

    public DistanceFrLaunchDisplay(FlightDisplay parent) {
        super(parent);
    }

    DistanceFr mDistanceFr = new DistanceFr(this);

    @Override
    protected String title() {return "Dist Fr Lnch (km)";}

    @Override
    protected String value() {return Double.toString((Math.round(mDistanceFr.value() / 3600) * 10) / 10);}

    @Override
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
        return(mDistanceFr.registerWith(broadcaster));
    }

    private double MIN_DISTANCE_FROM_LAUNCH = 5000; // meters

    @Override
    public DisplayPriority displayPriority() {
        try {
            DisplayPriority priority = DisplayPriority.NONE;
            if(mDistanceFr.value() > MIN_DISTANCE_FROM_LAUNCH)
                priority = DisplayPriority.MEDIUM;
            return priority;
        }catch(Exception e) {
            return DisplayPriority.NONE;
        }
    }
}
