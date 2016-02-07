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
import android.widget.Toast;

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
class WaypointDisplay extends CompassSubDisplay {

    private Turnpoint mTurnpoint;

    private DirectionDisplayImage mDirectionDisplay = null;
    private DirectionDisplayText mDistanceDisplay = null;

    private WifiDirectManager mManager = new WifiDirectManager();

    @Override
    public void init(Activity activity) {
        super.init(activity);
        //mManager.start(activity);
        mDirectionDisplay = new DirectionDisplayImage((ImageView) activity.findViewById(com.levemus.gliderhud.R.id.waypoint_pointer));
        mDistanceDisplay = new DirectionDisplayText((TextView) activity.findViewById(com.levemus.gliderhud.R.id.waypointDistance));
    }

    @Override
    public void deInit(Activity activity) {
        super.deInit(activity);
        //mManager.stop(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider)
    {
        mTurnpoint = new Turnpoint();
        mTurnpoint.registerSource(provider);
        mTurnpoint.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider)
    {
        mTurnpoint.stop();
        mTurnpoint.deRegisterSource(provider);
        mTurnpoint = null;
    }

    private Double MIN_DISTANCE = 50.0;
    private int mCurrentIndex = 1;

    @Override
    public void display(Activity activity) {
        try {
            if(!canDisplay()) {
                return;
            }

            Vector turnPoint = mTurnpoint.value();

            double distance = turnPoint.Magnitude();
            if(distance <= MIN_DISTANCE )
                return;

            double direction = turnPoint.Direction();

            mDirectionDisplay.setCurrentDirection(direction);
            mDirectionDisplay.display(activity);

            mDistanceDisplay.setCurrentDirection(direction);
            distance = Math.round(distance / 100);
            distance /= 10;

            if(mCurrentIndex != mTurnpoint.currentIndex()) {
                String message = "";

                if(mCurrentIndex == -1)
                    message = "Goal Reached!";
                else //if(mCurrentIndex != 0)
                    message = "Turnpoint " + mCurrentIndex + " Reached.";

                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                mCurrentIndex = mTurnpoint.currentIndex();
            }

            StringBuilder builder = new StringBuilder();
            if(mTurnpoint.currentIndex() == -1)
                builder.append("G");
            else
                builder.append(mTurnpoint.currentIndex());
            builder.append(": ");
            builder.append(Double.toString(distance));
            mDistanceDisplay.setText(builder.toString());
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
        return mTurnpoint.isValid();
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
