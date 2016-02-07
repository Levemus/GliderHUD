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
import com.levemus.gliderhud.FlightData.Processors.Custom.WindDrift;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayText;
import com.levemus.gliderhud.Utils.Angle;

/**
 * Created by mark@levemus on 15-12-29.
 */

// this class is only applicable within the context of the compass display
class WindDisplay extends CompassSubDisplay {

    private WindDrift mWindDrift;

    private DirectionDisplayImage mWindDirectionDisplay = null;
    private DirectionDisplayText mWindSpeedDisplay = null;

    @Override
    public void init(Activity activity) {
        super.init(activity);
        mWindDirectionDisplay = new DirectionDisplayImage((ImageView) activity.findViewById(com.levemus.gliderhud.R.id.wind_pointer));
        mWindSpeedDisplay = new DirectionDisplayText((TextView) activity.findViewById(com.levemus.gliderhud.R.id.windSpeed));
    }

    @Override
    public void registerProvider(IChannelDataSource provider)
    {
        mWindDrift = new WindDrift();
        mWindDrift.registerSource(provider);
        mWindDrift.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider)
    {
        mWindDrift.stop();
        mWindDrift.deRegisterSource(provider);
        mWindDrift = null;
    }

    private double DEGREES_FULL_CIRCLE = 360;
    private double DEGREES_HALF_CIRCLE = DEGREES_FULL_CIRCLE / 2;

    @Override
    public void display(Activity activity) {

        try {

            if(!canDisplay())
                return;

            double mWindDirection = (mWindDrift.value().Direction() + DEGREES_HALF_CIRCLE) % DEGREES_FULL_CIRCLE;
            mWindDirectionDisplay.setCurrentDirection(mWindDirection);
            mWindDirectionDisplay.display(activity);

            double windSpeed = mWindDrift.value().Magnitude();
            mWindSpeedDisplay.setCurrentDirection(mWindDirection);
            mWindSpeedDisplay.setText(Double.toString(Math.round(windSpeed * 3.6)));
            mWindSpeedDisplay.display(activity);

        } catch(Exception e) {}
    }

    private double mOffsetAngle = 0;

    @Override
    public void setParentDirection(double angle) {
        mOffsetAngle = angle;
        mWindDirectionDisplay.setParentDirection(mOffsetAngle);
        mWindSpeedDisplay.setParentDirection(mOffsetAngle);
    }

    @Override
    protected int refreshPeriod() { return Integer.MAX_VALUE; } // will refresh with compass refresh

    @Override
    public boolean canDisplay() {
        return (mWindDrift == null || !mWindDrift.isValid() || mWindDrift.value().Magnitude() <= 0);
    }

    @Override
    public void setAlpha(int alpha)
    {
        mWindSpeedDisplay.setAlpha(alpha);
    }

    @Override
    public int getPosition() {return mWindSpeedDisplay.getPosition();}

    @Override
    public int getWidth() {return mWindSpeedDisplay.getWidth();}
}