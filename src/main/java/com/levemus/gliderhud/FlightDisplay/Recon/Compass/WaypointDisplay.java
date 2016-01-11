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
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayText;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.WifiDirect.WifiDirectManager;

/**
 * Created by mark@levemus on 15-12-29.
 */
// this class is only applicable within the context of the compass display
class WaypointDisplay extends FlightDisplay {

    private Turnpoint mTurnpoint;

    private DirectionDisplayImage mDirectionDisplay = null;
    private DirectionDisplayText mDistanceDisplay = null;

    private WifiDirectManager mManager = new WifiDirectManager();

    @Override
    public void init(Activity activity) {
        super.init(activity);
        mManager.start(activity);
        mDirectionDisplay = new DirectionDisplayImage((ImageView) activity.findViewById(com.levemus.gliderhud.R.id.waypoint_pointer));
        mDistanceDisplay = new DirectionDisplayText((TextView) activity.findViewById(com.levemus.gliderhud.R.id.waypointDistance));
    }

    @Override
    public void deInit(Activity activity) {
        super.deInit(activity);
        mManager.stop(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider)
    {
        mTurnpoint = new Turnpoint();
        mTurnpoint.registerProvider(provider);
        mTurnpoint.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider)
    {
        mTurnpoint.stop();
        mTurnpoint.deRegisterProvider(provider);
        mTurnpoint = null;
    }

    private Double MIN_DISTANCE = 50.0;
    private double DEGREES_FULL_CIRCLE = 360;
    private double DEGREES_HALF_CIRCLE = DEGREES_FULL_CIRCLE / 2;

    @Override
    public void display(Activity activity) {
        try {
            if(!mTurnpoint.isValid()) {
                return;
            }

            Vector turnPoint = mTurnpoint.value();

            double distance = turnPoint.Magnitude();
            if(distance <= MIN_DISTANCE )
                return;

            double direction = (turnPoint.Direction() + DEGREES_HALF_CIRCLE) % DEGREES_FULL_CIRCLE;

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
    public void setBaseAngle(double angle) {
        mOffsetAngle = angle;
        mDirectionDisplay.setParentDirection(mOffsetAngle);
        mDistanceDisplay.setParentDirection(mOffsetAngle);
    }
}
