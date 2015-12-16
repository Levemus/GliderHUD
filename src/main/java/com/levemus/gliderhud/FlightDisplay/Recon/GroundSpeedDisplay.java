package com.levemus.gliderhud.FlightDisplay.Recon;

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
import com.levemus.gliderhud.FlightDisplay.FlightDisplayListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class GroundSpeedDisplay extends FlightDisplayListener {
    private double mGroundSpeed = 0;
    private TextView mGroundSpeedDisplay = null;

    private static final double MIN_SPEED = 1.0;
    private int UPDATE_INTERVAl_MS = 500;

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataType.GROUNDSPEED));

    @Override
    public void init(Activity activity)
    {
        mGroundSpeedDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.speedDisplay);
    }

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster) {
        if(!mSubscriptionFlags.isEmpty()) {
            HashSet<UUID> result = broadcaster.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.removeAll(result);
        }
    }

    @Override
    public void display() {}

    @Override
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        try {
            mGroundSpeed = data.get(FlightDataType.GROUNDSPEED);
            mGroundSpeedDisplay.setText(Integer.toString((int)(Math.max(mGroundSpeed, MIN_SPEED))));
        }
        catch(java.lang.UnsupportedOperationException e){}
    }
}
