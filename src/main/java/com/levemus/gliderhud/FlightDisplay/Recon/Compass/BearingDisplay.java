package com.levemus.gliderhud.FlightDisplay.Recon.Compass;

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
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;

/**
 * Created by mark@levemus on 15-12-29.
 */

// this class is only applicable within the context of the compass display
class BearingDisplay extends FlightDisplay {

    private Processor<Double> mBearing;
    private Processor<Double> mGroundSpeed;

    private DirectionDisplayImage mBearingDisplay = null;
    @Override
    public void init(Activity activity) {
        super.init(activity);
        mBearingDisplay = new DirectionDisplayImage((ImageView)
                activity.findViewById(com.levemus.gliderhud.R.id.bearing_pointer));
    }

    private double MIN_GROUND_SPEED = 0.3;

    @Override
    public void display(Activity activity) {
        try {
            if (mGroundSpeed.value() > MIN_GROUND_SPEED) {
                mBearingDisplay.setCurrentDirection(mBearing.value());
                mBearingDisplay.display(activity);
            }
        }catch(Exception e){}
    }

    @Override
    public void registerProvider(IChannelDataProvider provider)
    {
        mBearing = ProcessorFactory.build(ProcessorID.BEARING);
        mGroundSpeed = ProcessorFactory.build(ProcessorID.GROUNDSPEED);
        mBearing.registerProvider(provider);
        mGroundSpeed.registerProvider(provider);
        mBearing.start();
        mGroundSpeed.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {
        mBearing.deRegisterProvider(provider);
        mGroundSpeed.deRegisterProvider(provider);
        mBearing.stop();
        mGroundSpeed.stop();
        mBearing = null;
        mGroundSpeed = null;
    }
    public void setBaseAngle(double angle) {
        mBearingDisplay.setParentDirection(angle);
    }

    protected int refreshPeriod() { return Integer.MAX_VALUE; } // will refresh with compass refresh
}

