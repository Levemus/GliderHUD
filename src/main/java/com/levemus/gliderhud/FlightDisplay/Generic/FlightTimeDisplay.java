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
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class FlightTimeDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final Double MIN_GROUNDSPEED = 10.0 / 3.6; // m/s

    // Listeners
    private Processor<Double> mGroundSpeed;

    // Displays
    private TextView mFlightTimeDisplay = null;

    // Initialization/registration
    @Override
    public void init(Activity activity) {
        mFlightTimeDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.flight_time);
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

    // Operation
    // start time
    private long mStartTime = 0;

    @Override
    public void display(Activity activity) {

        try {
            long currentTime = new Date().getTime();
            if (mStartTime == 0 && mGroundSpeed.value() > MIN_GROUNDSPEED)
                mStartTime = currentTime;

            if (mStartTime == 0) {
                mFlightTimeDisplay.setText("00h 00m");
            }
            else {
                long deltaTime = currentTime - mStartTime;
                long hours = TimeUnit.MILLISECONDS.toHours(deltaTime);
                deltaTime -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(deltaTime);
                mFlightTimeDisplay.setText(String.format("%02dh %02dm", hours, minutes));
            }

        } catch (Exception e){}
    }
}
