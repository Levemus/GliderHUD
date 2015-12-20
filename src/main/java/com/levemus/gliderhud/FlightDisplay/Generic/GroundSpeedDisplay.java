package com.levemus.gliderhud.FlightDisplay.Generic;

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
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.Listeners.Altitude;
import com.levemus.gliderhud.FlightData.Listeners.GroundSpeed;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class GroundSpeedDisplay extends FlightDisplay {

    private final String TAG = this.getClass().getSimpleName();

    private TextView mGroundSpeedDisplay = null;
    @Override
    public void init(Activity activity)
    {
        mGroundSpeedDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.speedDisplay);
    }

    private GroundSpeed mGroundSpeed = new GroundSpeed(this);
    private static final double MIN_SPEED = 1.0;
    @Override
    public void display() {
        mGroundSpeedDisplay.setText(Integer.toString((int)(Math.max(mGroundSpeed.value(), MIN_SPEED))));
    }

    @Override
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
        return mGroundSpeed.registerWith(broadcaster);
    }
}
