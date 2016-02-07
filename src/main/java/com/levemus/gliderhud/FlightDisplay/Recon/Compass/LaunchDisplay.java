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
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Processors.Custom.Turnpoint;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayText;

/**
 * Created by mark@levemus on 16-01-18.
 */
// this class is only applicable within the context of the compass display
class LaunchDisplay extends CompassSubDisplay {

    private DirectionDisplayImage mDirectionDisplay = null;
    private DirectionDisplayText mDistanceDisplay = null;

    private Processor<Double> mBearingTo;
    private Processor<Double> mDistanceFr;

    private Turnpoint mTurnpoint;

    @Override
    public void init(Activity activity) {
        super.init(activity);
        mDirectionDisplay = new DirectionDisplayImage((ImageView) activity.findViewById(com.levemus.gliderhud.R.id.launch_pointer));
        mDistanceDisplay = new DirectionDisplayText((TextView) activity.findViewById(com.levemus.gliderhud.R.id.launchDistance));
    }

    @Override
    public void deInit(Activity activity) {
        super.deInit(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider)
    {
        mBearingTo = ProcessorFactory.build(ProcessorID.BEARINGTO);
        mDistanceFr = ProcessorFactory.build(ProcessorID.DISTANCEFR);
        mTurnpoint = new Turnpoint();
        mBearingTo.registerSource(provider);
        mDistanceFr.registerSource(provider);
        mTurnpoint.registerSource(provider);
        mBearingTo.start();
        mDistanceFr.start();
        mTurnpoint.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider)
    {
        mBearingTo.deRegisterSource(provider);
        mDistanceFr.deRegisterSource(provider);
        mTurnpoint.deRegisterSource(provider);
        mBearingTo.stop();
        mDistanceFr.stop();
        mTurnpoint.stop();
        mBearingTo = null;
        mDistanceFr = null;
        mTurnpoint = null;
    }

    private Double MIN_DISTANCE = 20.0;

    @Override
    public void display(Activity activity) {
        try {
            if (!canDisplay())
                return;

            double distance = mDistanceFr.value();
            double direction = mBearingTo.value();

            mDirectionDisplay.setCurrentDirection(direction);
            mDirectionDisplay.display(activity);

            mDistanceDisplay.setCurrentDirection(direction);
            distance = Math.round(distance / 100);
            distance /= 10;
            mDistanceDisplay.setText(Double.toString(distance));
            mDistanceDisplay.display(activity);
        } catch(Exception e) {}
    }

    private double mOffsetAngle = 0;
    @Override
    public void setParentDirection(double angle) {
        mOffsetAngle = angle;
        mDirectionDisplay.setParentDirection(mOffsetAngle);
        mDistanceDisplay.setParentDirection(mOffsetAngle);
    }


    @Override
    public boolean canDisplay() {
        return (!mTurnpoint.isValid() && mDistanceFr.isValid() && (mDistanceFr.value() > MIN_DISTANCE));
    }

    @Override
    public void setAlpha(int alpha)
    {
        mDistanceDisplay.setAlpha(alpha);
    }

    @Override
    public int getPosition() {return mDistanceDisplay.getPosition();}

    @Override
    public int getWidth() {return mDistanceDisplay.getWidth();}
}
