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

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-18.
 */

public class HeightAbvLaunchDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_HEIGHT_ABVL = 10.0; // meters
    private final double MAX_DISTANCE_FROM_LAUNCH = 5000; // meters
    private final double MAX_TURN_RATE = 15;
    private final double MIN_CLIMB_RATE = -0.5;

    // Listeners
    private Processor<Double> mDistanceFr;
    private Processor<Double> mHeightAbv;
    private Processor<Double> mTurnRate;
    private Processor<Double> mVario;

    // Initialization/registration
    public HeightAbvLaunchDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerProvider(IChannelDataProvider provider)
    {
        mDistanceFr = ProcessorFactory.build(ProcessorID.DISTANCEFR, provider);
        mHeightAbv = ProcessorFactory.build(ProcessorID.HEIGHTABV, provider);
        mTurnRate = ProcessorFactory.build(ProcessorID.TURNRATE, provider);
        mVario = ProcessorFactory.build(ProcessorID.VARIO, provider);
        mDistanceFr.registerProvider(provider);
        mHeightAbv.registerProvider(provider);
        mTurnRate.registerProvider(provider);
        mVario.registerProvider(provider);
        mDistanceFr.start();
        mHeightAbv.start();
        mTurnRate.start();
        mVario.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {
        mDistanceFr.stop();
        mHeightAbv.stop();
        mTurnRate.stop();
        mVario.stop();
        mDistanceFr.deRegisterProvider(provider);
        mHeightAbv.deRegisterProvider(provider);
        mTurnRate.deRegisterProvider(provider);
        mVario.deRegisterProvider(provider);
        mDistanceFr = null;
        mHeightAbv = null;
        mTurnRate = null;
        mVario = null;
    }

    // Operation
    protected String title() {return "Height ABL (m)";}

    @Override
    protected String value() {
        double height = 0;
        if(mHeightAbv.value() > MIN_HEIGHT_ABVL) {
            height = Math.round(mHeightAbv.value() * 10) / 10;
        }
        return Double.toString(height);
    }

    @Override
    public DisplayPriority displayPriority() {
        try {
            mHeightAbv.process();
            mTurnRate.process();
            mDistanceFr.process();
            mVario.process();
            if(mHeightAbv.value() < MIN_HEIGHT_ABVL || mDistanceFr.value() > MAX_DISTANCE_FROM_LAUNCH)
                return DisplayPriority.NONE;
            else if(mTurnRate.value() < MAX_TURN_RATE || mVario.value() < MIN_CLIMB_RATE)
                return DisplayPriority.LOW;
            else
                return DisplayPriority.MEDIUM;
        }catch(Exception e) {
            return DisplayPriority.NONE;
        }
    }

    @Override
    public long displayDuration() { return 5000; }
}
