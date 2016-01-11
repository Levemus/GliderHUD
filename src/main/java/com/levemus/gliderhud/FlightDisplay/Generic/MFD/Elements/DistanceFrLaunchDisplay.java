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

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-18.
 */

public class DistanceFrLaunchDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_DISTANCE_FROM_LAUNCH = 5000; // meters

    // Listeners
    private Processor<Double> mDistanceFr;

    // Initialization/registration
    public DistanceFrLaunchDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerProvider(IChannelDataSource provider) {
        mDistanceFr = ProcessorFactory.build(ProcessorID.DISTANCEFR);
        mDistanceFr.registerProvider(provider);
        mDistanceFr.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {
        mDistanceFr.stop();
        mDistanceFr.deRegisterProvider(provider);
        mDistanceFr = null;
    }

    // Operation
    @Override
    protected String title() {return "Dist Fr Lnch (km)";}

    @Override
    protected String value() {
        double distance = Math.round(mDistanceFr.value() / 10) / 100;
        return Double.toString(distance);
    }

    @Override
    public DisplayPriority displayPriority() {
        try {
            mDistanceFr.process();
            DisplayPriority priority = DisplayPriority.NONE;
            if(mDistanceFr.value() > MIN_DISTANCE_FROM_LAUNCH)
                priority = DisplayPriority.MEDIUM;
            return priority;
        }catch(Exception e) {
            return DisplayPriority.NONE;
        }
    }

    @Override
    public long displayDuration() { return 5000; }
}
