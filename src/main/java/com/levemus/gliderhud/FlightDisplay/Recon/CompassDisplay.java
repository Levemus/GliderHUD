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
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Listeners.WindDrift;
import com.levemus.gliderhud.FlightDisplay.Components.DirectionDisplay;
import com.levemus.gliderhud.FlightDisplay.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.Components.DirectionDisplayText;
import com.levemus.gliderhud.FlightDisplay.FlightDisplayListener;

import java.util.EnumSet;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class CompassDisplay extends FlightDisplayListener {

    private final String TAG = this.getClass().getSimpleName();

    private DirectionDisplayImage mHeadingDisplay = null;
    private WindDisplay mWindDisplay = null;
    private BearingDisplay mBearingDisplay = null;

    private int UPDATE_INTERVAl_MS = 10;
    private double SCREEN_WIDTH_ANGLE = 53;

    EnumSet<IFlightData.FlightDataType> mSubscriptionFlags = EnumSet.of(
            IFlightData.FlightDataType.YAW);

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
    public void registerWith(IFlightDataBroadcaster broadcaster) {
        if(!mSubscriptionFlags.isEmpty()) {
            EnumSet<IFlightData.FlightDataType> result = broadcaster.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.retainAll(EnumSet.complementOf(result));
        }
        mWindDisplay.registerWith(broadcaster);
        mBearingDisplay.registerWith(broadcaster);
    }

    @Override
    public void display()
    {
        mHeadingDisplay.display();
        mBearingDisplay.display();
        mWindDisplay.display();
    }

    @Override
    public void onData(IFlightData data) {
        try {
            mHeadingDisplay.setCurrentDirection(
                    DirectionDisplay.smoothDirection(data.getData(IFlightData.FlightDataType.YAW),
                            mHeadingDisplay.getCurrentDirection()));
            mBearingDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
            mWindDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
            display();
        }
        catch(java.lang.UnsupportedOperationException e){}
    }

    // this class is only applicabile within the context of the compass display
    private class WindDisplay extends FlightDisplayListener {

        private DirectionDisplayImage mWindDirectionDisplay = null;
        private DirectionDisplayText mWindSpeedDisplay = null;

        private WindDrift mWindDrift = new WindDrift();
        private double mWindSpeed = -1;

        private double mOffsetAngle = 0;

        private double DEGREES_FULL_CIRCLE = 360;
        private double DEGREES_HALF_CIRCLE = DEGREES_FULL_CIRCLE / 2;

        private int UPDATE_INTERVAl_MS = 10000;

        EnumSet<IFlightData.FlightDataType> mSubscriptionFlags = EnumSet.of(
                IFlightData.FlightDataType.WINDDIRECTION,
                IFlightData.FlightDataType.WINDSPEED);

        @Override
        public void init(Activity activity) {
            mWindDirectionDisplay = new DirectionDisplayImage((ImageView) activity.findViewById(com.levemus.gliderhud.R.id.wind_pointer));
            mWindSpeedDisplay = new DirectionDisplayText((TextView) activity.findViewById(com.levemus.gliderhud.R.id.windSpeed));
            mWindDrift.init(activity);
        }

        @Override
        public void registerWith(IFlightDataBroadcaster broadcaster) {
            mWindDrift.registerWith(broadcaster);
            if(!mSubscriptionFlags.isEmpty()) {
                EnumSet<IFlightData.FlightDataType> result = mWindDrift.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
                mSubscriptionFlags.retainAll(EnumSet.complementOf(result));
            }
        }

        @Override
        public void display() {
            if(mWindSpeed <= 0 )
                return;
            mWindDirectionDisplay.display();
            mWindSpeedDisplay.display();
        }

        @Override
        public void onData(IFlightData data) {
            try {
                mWindSpeed = data.getData(IFlightData.FlightDataType.WINDSPEED);
                double mWindDirection = (data.getData(IFlightData.FlightDataType.WINDDIRECTION) + DEGREES_HALF_CIRCLE) % DEGREES_FULL_CIRCLE;
                mWindDirectionDisplay.setCurrentDirection(mWindDirection);
                mWindSpeedDisplay.setCurrentDirection(mWindDirection);
                mWindSpeedDisplay.setText(Double.toString(Math.round(mWindSpeed)));
                display();
            }
            catch(java.lang.UnsupportedOperationException e){}

        }

        public void setBaseAngle(double angle) {
            mOffsetAngle = angle;
            mWindDirectionDisplay.setOffsetBaseAngle(mOffsetAngle);
            mWindSpeedDisplay.setOffsetBaseAngle(mOffsetAngle);
        }
    }

    // this class is only applicabile within the context of the compass display
    private class BearingDisplay extends FlightDisplayListener {
        private DirectionDisplayImage mBearingDisplay = null;
        private long UPDATE_INTERVAl_MS = 2000;
        private double mOffsetAngle = 0;
        private EnumSet<IFlightData.FlightDataType> mSubscriptionFlags = EnumSet.of(
                IFlightData.FlightDataType.BEARING);

        @Override
        public void init(Activity activity) {
            mBearingDisplay = new DirectionDisplayImage((ImageView)
                    activity.findViewById(com.levemus.gliderhud.R.id.bearing_pointer));
        }

        @Override
        public void display() {
            mBearingDisplay.display();
        }

        @Override
        public void registerWith(IFlightDataBroadcaster broadcaster) {
            if(!mSubscriptionFlags.isEmpty()) {
                EnumSet<IFlightData.FlightDataType> result = broadcaster.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
                mSubscriptionFlags.retainAll(EnumSet.complementOf(result));
            }
        }

        @Override
        public void onData(IFlightData data) {
            try {
                mBearingDisplay.setCurrentDirection(data.getData(IFlightData.FlightDataType.BEARING));
                display();
            } catch(java.lang.UnsupportedOperationException e){}
        }

        public void setBaseAngle(double angle) {
            mOffsetAngle = angle;
            mBearingDisplay.setOffsetBaseAngle(mOffsetAngle);
        }
    }
}
