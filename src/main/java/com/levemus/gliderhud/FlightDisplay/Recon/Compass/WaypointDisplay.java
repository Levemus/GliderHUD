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

import java.util.Arrays;
import java.util.HashSet;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.ProcessorBuilder;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayText;
import com.levemus.gliderhud.Utils.WaypointManager;
import com.levemus.gliderhud.Types.Vector;

/**
 * Created by mark@levemus on 15-12-29.
 */
// this class is only applicable within the context of the compass display
class WaypointDisplay extends FlightDisplay {

    private Processor<Double> mLongitude;

    private Processor<Double> mLatitude;

    WaypointManager mTurnpointManager = new WaypointManager();

    private DirectionDisplayImage mDirectionDisplay = null;
    private DirectionDisplayText mDistanceDisplay = null;

    @Override
    public void init(Activity activity) {
        super.init(activity);
        //mDirectionDisplay = new DirectionDisplayImage((ImageView) activity.findViewById(com.levemus.gliderhud.R.id.waypoint_pointer));
        //mDistanceDisplay = new DirectionDisplayText((TextView) activity.findViewById(com.levemus.gliderhud.R.id.waypoint_distance));
    }

    @Override
    public void registerProvider(IChannelDataProvider provider)
    {
        mLongitude = new ProcessorBuilder()
                .channels(new HashSet<>(Arrays.asList(MessageChannels.LONGITUDE)))
                .build();

        mLatitude = new ProcessorBuilder()
                .channels(new HashSet<>(Arrays.asList(MessageChannels.LATITUDE)))
                .build();

        mLongitude.registerProvider(provider);
        mLatitude.registerProvider(provider);
        mLongitude.start();
        mLatitude.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {
        mLongitude.stop();
        mLatitude.stop();
        mLongitude.deRegisterProvider(provider);
        mLatitude.deRegisterProvider(provider);
        mLongitude = null;
        mLatitude = null;
    }

    private Double MIN_DISTANCE = 50.0;

    @Override
    public void display(Activity activity) {
        try {
            Double longitude = mLongitude.value();
            Double latitude = mLatitude.value();

            Vector turnPoint = mTurnpointManager.getBearingAndDistance(longitude, latitude);
            double distance = turnPoint.Magnitude();
            if(distance <= MIN_DISTANCE )
                return;

            double direction = turnPoint.Direction();

            mDirectionDisplay.setCurrentDirection(direction);
            mDirectionDisplay.display(activity);

            mDistanceDisplay.setCurrentDirection(direction);
            mDistanceDisplay.setText(Double.toString(Math.round(distance / 100) / 10));
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
