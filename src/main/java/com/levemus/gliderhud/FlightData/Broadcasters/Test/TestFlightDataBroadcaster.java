package com.levemus.gliderhud.FlightData.Broadcasters.Test;

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
import android.os.Handler;
import java.util.Date;
import java.util.EnumSet;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.Types.Vector;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class TestFlightDataBroadcaster extends FlightDataBroadcaster {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    private double startAltitude = 500; // m
    private double climbRate = 0.5; // m/s
    private double turnRate = 5.0; // degree / sec
    private double airspeed = 30.0; // kph
    private Vector mWindVelocity =  new Vector(10,0);

    private double mCurrentAltitude = startAltitude;
    private Vector mCurrentVelocity =  new Vector(-1*airspeed,0);
    private double mCurrentClimbRate = climbRate;
    private double mYaw = 0;

    private Handler mHandler = null;
    private int mInterval = 100;
    private long MS_PER_SECOND = 1000;
    private double DEGREES_PER_CIRCLE = 360;
    private long timeOfLastUpdate = 0;

    private class DataNotifier implements Runnable {
        private FlightDataBroadcaster broadcaster;
        public DataNotifier(FlightDataBroadcaster broadcaster) {
            this.broadcaster = broadcaster;
        }
        public void run() {

            long currentTime = new Date().getTime();
            long deltaTime = currentTime - timeOfLastUpdate;
            UpdateVelocity(deltaTime);
            UpdateAltitude(deltaTime);
            NotifyListeners();
            mHandler.postDelayed(this, mInterval);
            timeOfLastUpdate = currentTime;
        }
    }

    private DataNotifier mDataNotifer = new DataNotifier(this);

    @Override
    public void init(Activity activity) {
        timeOfLastUpdate = new Date().getTime();
        mHandler = new Handler();
        mDataNotifer.run();
    }

    @Override
    public EnumSet<IFlightData.FlightDataType> supportedTypes() {
        return new TestFlightData().supportedTypes();
    }

    private void NotifyListeners()
    {
        notifyListeners(new TestFlightData(mCurrentVelocity, mWindVelocity, mCurrentAltitude, mCurrentClimbRate));
    }

    private void UpdateAltitude(long deltaTime)
    {
        mCurrentAltitude += (deltaTime * mCurrentClimbRate) / MS_PER_SECOND;
    }

    private void UpdateVelocity(long deltaTime)
    {
        if(turnRate > 0) {

            double deltaHeading = (deltaTime * turnRate) / MS_PER_SECOND;
            double newHeading = mCurrentVelocity.Direction() + deltaHeading;
            while(newHeading < 0.0f)
                newHeading+= DEGREES_PER_CIRCLE;
            newHeading %= DEGREES_PER_CIRCLE;
            mCurrentVelocity.SetDirectionAndMagnitude(newHeading, mCurrentVelocity.Magnitude());
        }
    }
}

class TestFlightData implements IFlightData {

    private Vector mCurrentVelocity;
    private Vector mWindVelocity;
    private double mAltitude;
    private double mClimbRate;
    private int MIN_SPEED = 0;

    public TestFlightData() {} // to get around lack of statics in interfaces while accessing supported types

    public TestFlightData(Vector groundVelocity, Vector windVelocity, double altitude, double climbrate)
    {
        mCurrentVelocity = groundVelocity;
        mWindVelocity = windVelocity;
        mAltitude = altitude;
        mClimbRate = climbrate;
    }

    private double GroundSpeed() {
        return(Math.max(Math.round(new Vector(mCurrentVelocity).Add(mWindVelocity).Magnitude() * 10) / 10, MIN_SPEED));
    }

    @Override
    public double get(FlightDataType type) throws java.lang.UnsupportedOperationException
    {
        try {
            if (type == FlightDataType.ALTITUDE)
                return mAltitude;

            if (type == FlightDataType.GROUNDSPEED)
                return GroundSpeed();

            if (type == FlightDataType.BEARING)
                return new Vector(mCurrentVelocity).Add(mWindVelocity).Direction();

            if (type == FlightDataType.GLIDE)
                return (mClimbRate != 0 ? GroundSpeed() / mClimbRate : 0);

            if (type == FlightDataType.VARIO)
                return mClimbRate;
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public EnumSet<FlightDataType> supportedTypes() {
        return EnumSet.of(
                IFlightData.FlightDataType.ALTITUDE,
                IFlightData.FlightDataType.GROUNDSPEED,
                IFlightData.FlightDataType.BEARING,
                IFlightData.FlightDataType.GLIDE,
                IFlightData.FlightDataType.VARIO);
    }
}

