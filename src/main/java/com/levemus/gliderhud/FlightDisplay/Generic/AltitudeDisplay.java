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

import com.levemus.gliderhud.FlightData.Broadcasters.IBroadcaster;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerFactory;
import com.levemus.gliderhud.FlightData.Listeners.Listener;
import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerID;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class AltitudeDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_ALTITUDE = 0.0;

    // Listeners
    private Listener<Double> mAltitude = ListenerFactory.build(ListenerID.ALTITUDE, this);

    // Displays
    private TextView mAltiDisplay = null;

    // Initialization/registration
    @Override
    public void init(Activity activity)
    {
        mAltiDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.altiDisplay);
    }

    @Override
    public void registerWith(IBroadcaster broadcaster) {
        broadcaster.registerWith(mAltitude, mAltitude);
    }

    // Operation
    @Override
    public void display() {
        try {
            mAltiDisplay.setText(Integer.toString((int) Math.max(mAltitude.value(), MIN_ALTITUDE)));
        }catch (Exception e){
            mAltiDisplay.setText("---");
        }
    }
}
