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
import android.util.Log;
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Broadcasters.IRegisterListener;
import com.levemus.gliderhud.FlightData.FlightDataChannel;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Listener;
import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerID;
import com.levemus.gliderhud.FlightData.Listeners.Factory.ListenerFactory;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class GroundSpeedDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();

    // Listeners
    private Listener mGroundSpeed = ListenerFactory.build(ListenerID.GROUNDSPEED, this);

    // Displays
    private TextView mGroundSpeedDisplay = null;

    // Initialization/registration
    @Override
    public void init(Activity activity)
    {
        mGroundSpeedDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.speedDisplay);
    }

    @Override
    public void registerWith(IRegisterListener broadcaster) {
        broadcaster.register(mGroundSpeed, mGroundSpeed);
    }

    // Operation
    @Override
    public void display() {
        try {
            mGroundSpeedDisplay.setText(Integer.toString((int)(mGroundSpeed.value() * 3.6)));
        }catch (Exception e){
            mGroundSpeedDisplay.setText("---");
        }
    }
}
