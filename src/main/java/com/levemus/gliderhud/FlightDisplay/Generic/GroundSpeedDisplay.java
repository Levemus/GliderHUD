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

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class GroundSpeedDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();

    // Listeners
    private Processor<Double> mGroundSpeed;

    // Displays
    private TextView mGroundSpeedDisplay = null;

    // Initialization/registration
    @Override
    public void init(Activity activity) {
        mGroundSpeedDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.speedDisplay);
        super.init(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider) {
        mGroundSpeed = ProcessorFactory.build(ProcessorID.GROUNDSPEED);
        mGroundSpeed.registerSource(provider);
        mGroundSpeed.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {
        mGroundSpeed.stop();
        mGroundSpeed.deRegisterSource(provider);
        mGroundSpeed = null;
    }

    private boolean mIsFlying = false;
    private final double MIN_GROUND_SPEED = 10.0; // kph

    // Operation
    @Override
    public void display(Activity activity) {
        try {
            if(!mGroundSpeed.isValid())
                mGroundSpeedDisplay.setText("---");
            else if(!mIsFlying) {
                mGroundSpeedDisplay.setText("0");
                if(mGroundSpeed.value() * 3.6 > MIN_GROUND_SPEED)
                    mIsFlying = true;
            }
            else
                mGroundSpeedDisplay.setText(Integer.toString((int)(mGroundSpeed.value() * 3.6)));
        }catch (Exception e){
            mGroundSpeedDisplay.setText("---");
        }
    }
}
