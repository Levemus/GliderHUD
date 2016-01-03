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
 * Created by mark@levemus on 15-12-17.
 */

public class ClimbRateDisplay extends MFDTextElement {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final Double MIN_VARIO = 0.10;
    private final Double MIN_CLIMB_TURN_RATE = 10.0;
    private final Double HIGH_CLIMB_RATE = 3.0;
    private final Double MAX_CLIMB_RATE = 100.0;
    // Listeners
    private Processor<Double> mClimbRate;
    private Processor<Double> mTurnRate;

    // Initialization/registration
    public ClimbRateDisplay(FlightDisplay parent) {
        super(parent);
    }

    @Override
    public void registerProvider(IChannelDataProvider provider) {
        mClimbRate = ProcessorFactory.build(ProcessorID.VARIO, provider);
        mTurnRate = ProcessorFactory.build(ProcessorID.TURNRATE, provider);
        mClimbRate.registerProvider(provider);
        mTurnRate.registerProvider(provider);
        mClimbRate.start();
        mTurnRate.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {
        mClimbRate.stop();
        mTurnRate.stop();
        mClimbRate.deRegisterProvider(provider);
        mTurnRate.deRegisterProvider(provider);
        mClimbRate = null;
        mTurnRate = null;
    }

    // Operation
    @Override
    protected String title() {return "Climb (m/s)";}

    @Override
    protected String value() {
        if (Math.abs(mClimbRate.value()) > MAX_CLIMB_RATE)
            return "---";

        double displayVario = 0;
        if (Math.abs(mClimbRate.value()) > MIN_VARIO) {
            displayVario = Math.round(mClimbRate.value() * 100);
            displayVario /= 100;
        }
        return Double.toString(displayVario);
    }

    @Override
    public MFDElement.DisplayPriority displayPriority() {
        try {
            mClimbRate.process();
            mTurnRate.process();
            if(Math.abs(mClimbRate.value()) > MAX_CLIMB_RATE)
                return MFDElement.DisplayPriority.NONE;
            else if (Math.abs(mTurnRate.value()) > HIGH_CLIMB_RATE)
                return MFDElement.DisplayPriority.HIGH;
            else
                return MFDElement.DisplayPriority.MEDIUM;
        }catch(Exception e) {
            return MFDElement.DisplayPriority.NONE;
        }
    }

    @Override
    public int refreshPeriod() { return 200; } // ms
}
