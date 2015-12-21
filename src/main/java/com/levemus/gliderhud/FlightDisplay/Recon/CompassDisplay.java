package com.levemus.gliderhud.FlightDisplay.Recon;

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

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Listeners.WindDrift;
import com.levemus.gliderhud.FlightData.Listeners.Bearing;
import com.levemus.gliderhud.FlightData.Listeners.Orientation;
import com.levemus.gliderhud.FlightData.Listeners.GroundSpeed;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayText;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class CompassDisplay extends FlightDisplay {

    private final String TAG = this.getClass().getSimpleName();

    private Orientation mOrientation = new Orientation(this);

    private DirectionDisplayImage mHeadingDisplay = null;
    private WindDisplay mWindDisplay = null;
    private BearingDisplay mBearingDisplay = null;
    @Override
    public void init(Activity activity)
    {
        mHeadingDisplay = new DirectionDisplayImage((ImageView)
                activity.findViewById(com.levemus.gliderhud.R.id.compass_bar));

        mWindDisplay = new WindDisplay();
        mWindDisplay.init(activity);

        mBearingDisplay = new BearingDisplay();
        mBearingDisplay.init(activity);
    }

    @Override
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {

        HashSet<UUID> result = new HashSet<UUID>();

        result.addAll(mOrientation.registerWith(broadcaster));
        result.addAll(mWindDisplay.registerWith(broadcaster));
        result.addAll(mBearingDisplay.registerWith(broadcaster));
        return result;
    }

    @Override
    public void display()
    {
        mHeadingDisplay.setCurrentDirection(
                DirectionDisplay.smoothDirection(mOrientation.yaw(),
                        mHeadingDisplay.getCurrentDirection()));
        mBearingDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
        mWindDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
        mHeadingDisplay.display();
        mBearingDisplay.display();
        mWindDisplay.display();
    }

    @Override
    public long getUpdateInterval() { return 10; } // milliseconds

    // this class is only applicabile within the context of the compass display
    private class WindDisplay extends FlightDisplay {

        private WindDrift mWindDrift = new WindDrift(this);

        private DirectionDisplayImage mWindDirectionDisplay = null;
        private DirectionDisplayText mWindSpeedDisplay = null;

        @Override
        public void init(Activity activity) {
            mWindDirectionDisplay = new DirectionDisplayImage((ImageView) activity.findViewById(com.levemus.gliderhud.R.id.wind_pointer));
            mWindSpeedDisplay = new DirectionDisplayText((TextView) activity.findViewById(com.levemus.gliderhud.R.id.windSpeed));
        }

        @Override
        public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
            return mWindDrift.registerWith(broadcaster);
        }

        private double DEGREES_FULL_CIRCLE = 360;
        private double DEGREES_HALF_CIRCLE = DEGREES_FULL_CIRCLE / 2;
        @Override
        public void display() {
            double windSpeed = mWindDrift.speed();
            if(windSpeed <= 0 )
                return;

            double mWindDirection = (mWindDrift.direction() + DEGREES_HALF_CIRCLE) % DEGREES_FULL_CIRCLE;

            mWindDirectionDisplay.setCurrentDirection(mWindDirection);
            mWindDirectionDisplay.display();

            mWindSpeedDisplay.setCurrentDirection(mWindDirection);
            mWindSpeedDisplay.setText(Double.toString(Math.round(windSpeed)));
            mWindSpeedDisplay.display();
        }

        @Override
        public void onDataReady(boolean force) {}

        private double mOffsetAngle = 0;
        public void setBaseAngle(double angle) {
            mOffsetAngle = angle;
            mWindDirectionDisplay.setOffsetBaseAngle(mOffsetAngle);
            mWindSpeedDisplay.setOffsetBaseAngle(mOffsetAngle);
        }
    }

    // this class is only applicabile within the context of the compass display
    private class BearingDisplay extends FlightDisplay {

        private Bearing mBearing = new Bearing(this);
        private GroundSpeed mGroundSpeed = new GroundSpeed(this);

        private DirectionDisplayImage mBearingDisplay = null;
        @Override
        public void init(Activity activity) {
            mBearingDisplay = new DirectionDisplayImage((ImageView)
                    activity.findViewById(com.levemus.gliderhud.R.id.bearing_pointer));
        }

        private double MIN_GROUND_SPEED = 1.0;

        @Override
        public void display() {
            try {
                if (mGroundSpeed.value() > MIN_GROUND_SPEED) {
                    mBearingDisplay.setCurrentDirection(mBearing.value());
                    mBearingDisplay.display();
                }
            }catch(Exception e){}
        }

        @Override
        public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
            HashSet<UUID> result = new HashSet<UUID>();
            result.addAll(mGroundSpeed.registerWith(broadcaster));
            result.addAll(mBearing.registerWith(broadcaster));
            return result;

        }

        @Override
        public void onDataReady(boolean force) {}

        public void setBaseAngle(double angle) {
            mBearingDisplay.setOffsetBaseAngle(angle);
        }
    }
}
