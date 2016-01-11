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
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class AltitudeDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_ALTITUDE = 0.0;

    // Listeners
    private Processor<Double> mAltitude;

    // Displays
    private TextView mAltiDisplay = null;

    // Initialization/registration
    @Override
    public void init(Activity activity)
    {
        mAltiDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.altiDisplay);
        super.init(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider) {
        mAltitude = ProcessorFactory.build(ProcessorID.ALTITUDE);
        mAltitude.registerProvider(provider);
        mAltitude.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {
        mAltitude.stop();
        mAltitude.deRegisterProvider(provider);
        mAltitude = null;
    }

    // Operation
    @Override
    public void display(Activity activity) {
        try {
            if(!mAltitude.isValid())
                mAltiDisplay.setText("---");
            else
                mAltiDisplay.setText(Integer.toString((int) Math.max(mAltitude.value(), MIN_ALTITUDE)));
        }catch (Exception e){
            mAltiDisplay.setText("---");
        }
    }
}
