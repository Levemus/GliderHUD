package com.levemus.gliderhud.FlightDisplay.Generic;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import android.app.Activity;
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mark@levemus on 16-01-25.
 */

public class BatteryDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();

    // Listeners
    private Processor<Double> mBatteryLevel;

    // Displays
    private TextView mBatteryLevelDisplay = null;

    // Initialization/registration
    @Override
    public void init(Activity activity) {
        mBatteryLevelDisplay = (TextView) activity.findViewById(R.id.battery_level);
        super.init(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider) {
        mBatteryLevel = ProcessorFactory.build(ProcessorID.BATTERY);
        mBatteryLevel.registerSource(provider);
        mBatteryLevel.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {
        mBatteryLevel.stop();
        mBatteryLevel.deRegisterSource(provider);
        mBatteryLevel = null;
    }

    @Override
    public void display(Activity activity) {

        try {
            if (!mBatteryLevel.isValid())
                mBatteryLevelDisplay.setText("-- %");
            else
                mBatteryLevelDisplay.setText(Long.toString(Math.round(mBatteryLevel.value())) + " %");
        } catch (Exception e){}
    }
}
