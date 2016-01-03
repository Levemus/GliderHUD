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

public class GlideRatioDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final Double MIN_GLIDE = -0.01;
    private final Double CRITICAL_GLIDE = -3.0;
    private final Double MAX_TURN_RATE = 10.0;
    private final Double MAX_GLIDE_VALUE = -100.0;

    // Listeners
    private Processor<Double> mTurnRate;
    private Processor<Double> mGlide;

    // Initialization/registration
    public GlideRatioDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerProvider(IChannelDataProvider provider) {
        mTurnRate = ProcessorFactory.build(ProcessorID.TURNRATE, provider);
        mGlide = ProcessorFactory.build(ProcessorID.GLIDERATIO, provider);
        mTurnRate.registerProvider(provider);
        mGlide.registerProvider(provider);
        mTurnRate.start();
        mGlide.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {
        mTurnRate.stop();
        mGlide.stop();
        mTurnRate.deRegisterProvider(provider);
        mGlide.deRegisterProvider(provider);
        mTurnRate = null;
        mGlide = null;
    }

    // Operation
    @Override
    protected String title() {return "Glide";}

    @Override
    protected String value() {
        if(mGlide.value() >= MIN_GLIDE || mGlide.value() < MAX_GLIDE_VALUE)
            return "---";

        double displayGlide = Math.round(mGlide.value() * 100);
        displayGlide /= 100;
        return Double.toString(displayGlide);
    }

    @Override
    public MFDElement.DisplayPriority displayPriority() {
        try {
            mGlide.process();
            mTurnRate.process();
            if(mGlide.value() >= MIN_GLIDE || mGlide.value() < MAX_GLIDE_VALUE || mTurnRate.value() > MAX_TURN_RATE)
                return MFDElement.DisplayPriority.NONE;
            else if(mGlide.value() >= CRITICAL_GLIDE &&  mTurnRate.value() < MAX_TURN_RATE)
                return MFDElement.DisplayPriority.CRITICAL;
            else
                return MFDElement.DisplayPriority.MEDIUM;
        }catch(Exception e) {
            return DisplayPriority.NONE;
        }
    }
}
