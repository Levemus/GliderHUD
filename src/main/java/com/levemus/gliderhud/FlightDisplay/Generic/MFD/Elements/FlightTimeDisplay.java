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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-17.
 */

public class FlightTimeDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final Double MIN_GROUNDSPEED = 10.0 / 3.6; // m/s
    private final int DISPLAY_INTERVAL = 5; // minutes

    // Listeners
    private Processor<Double> mGroundSpeed;

    // Initialization/registration
    public FlightTimeDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerProvider(IChannelDataProvider provider) {
        mGroundSpeed = ProcessorFactory.build(ProcessorID.GROUNDSPEED);
        mGroundSpeed.registerProvider(provider);
        mGroundSpeed.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {
        mGroundSpeed.stop();
        mGroundSpeed.deRegisterProvider(provider);
        mGroundSpeed = null;
    }

    // Operation
    // start time
    private long mStartTime = 0;

    @Override
    protected String title() {return "Flight Time";}

    @Override
    protected String value() {

        long currentTime = new Date().getTime();
        if(mStartTime == 0 && mGroundSpeed.value() > MIN_GROUNDSPEED)
            mStartTime = currentTime;

        if(mStartTime == 0)
            return "---";

        long deltaTime = currentTime - mStartTime;
        long hours = TimeUnit.MILLISECONDS.toHours(deltaTime);
        deltaTime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(deltaTime);

        return (String.format("%02dh %02dm", hours, minutes));
    }

    @Override
    public DisplayPriority displayPriority() {
        try {
            if (mStartTime == 0 && mGroundSpeed.value() < MIN_GROUNDSPEED)
                return DisplayPriority.NONE;
            long currentTime = new Date().getTime();
            long deltaTime = currentTime - mStartTime;
            if((TimeUnit.MILLISECONDS.toMinutes(deltaTime) % DISPLAY_INTERVAL) == 0)
                return DisplayPriority.MEDIUM;
        } catch(Exception e){ return DisplayPriority.NONE; }
        return DisplayPriority.NONE;
    }

    @Override
    public long displayDuration() { return 5000; }
}
