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

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Processors.Custom.WindDrift;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayText;

/**
 * Created by mark@levemus on 15-12-29.
 */

// this class is only applicable within the context of the compass display
class WindDisplay extends FlightDisplay {

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
    public void registerProvider(IChannelDataProvider provider)
    {
        mWindDrift = new WindDrift();
        mWindDrift.registerProvider(provider);
        mWindDrift.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider)
    {
        mWindDrift.stop();
        mWindDrift.deRegisterProvider(provider);
        mWindDrift = null;
    }

    private double DEGREES_FULL_CIRCLE = 360;
    private double DEGREES_HALF_CIRCLE = DEGREES_FULL_CIRCLE / 2;
    @Override
    public void display(Activity activity) {

        try {
            mWindDirectionDisplay.display(activity);

            if (mWindDrift == null || !mWindDrift.isValid() || mWindDrift.value().Magnitude() <= 0)
                return;

            double windSpeed = mWindDrift.value().Magnitude();

            double mWindDirection = (mWindDrift.value().Direction() + DEGREES_HALF_CIRCLE) % DEGREES_FULL_CIRCLE;

            mWindDirectionDisplay.setCurrentDirection(mWindDirection);
            mWindSpeedDisplay.setCurrentDirection(mWindDirection);
            mWindSpeedDisplay.setText(Double.toString(Math.round(windSpeed * 3.6)));
            mWindSpeedDisplay.display(activity);
        } catch(Exception e) {}
    }

    private double mOffsetAngle = 0;
    public void setBaseAngle(double angle) {
        mOffsetAngle = angle;
        mWindDirectionDisplay.setParentDirection(mOffsetAngle);
        mWindSpeedDisplay.setParentDirection(mOffsetAngle);
    }

    @Override
    protected int refreshPeriod() { return Integer.MAX_VALUE; } // will refresh with compass refresh
}
