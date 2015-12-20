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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.NormalDistribution;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class TestFlightDataBroadcaster extends FlightDataBroadcaster {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    private double startAltitude = 500; // m
    private double turnRate = 0.0; // degree / sec
    private double airspeed = 50.0; // kph
    private Vector mWindVelocity =  new Vector(10,0);

    private double mCurrentAltitude = startAltitude;
    private Vector mCurrentVelocity =  new Vector(-1*airspeed,0);
    private double mCurrentClimbRate = 0;
    private NormalDistribution climbRateRandomGen = new NormalDistribution();
    private double mLatitude = 0;
    private double mLongitude = 0;

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

            if(mCurrentAltitude > startAltitude + 200 && turnRate != 0.0) {
                turnRate = 0.0;
                mCurrentVelocity.SetDirectionAndMagnitude(mCurrentVelocity.Direction(), 50);
            }
            else if (mCurrentAltitude < startAltitude && turnRate == 0.0){
                turnRate = 15.0;
                mCurrentVelocity.SetDirectionAndMagnitude(mCurrentVelocity.Direction(), 30);
            }

            UpdateClimbRate();
            UpdateVelocity(deltaTime);
            UpdateAltitude(deltaTime);
            UpdateLocation(deltaTime);
            mActivity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    notifyListeners(new TestFlightData(mCurrentVelocity, mWindVelocity,
                            mCurrentAltitude, mCurrentClimbRate,
                            mLatitude,mLongitude));
                }
            });

            mHandler.postDelayed(this, mInterval);
            timeOfLastUpdate = currentTime;
        }
    }

    private DataNotifier mDataNotifer = new DataNotifier(this);
    private Activity mActivity;

    @Override
    public void init(Activity activity) {
        mActivity = activity;
        timeOfLastUpdate = new Date().getTime();
        mHandler = new Handler();
        mDataNotifer.run();
    }

    @Override
    public HashSet<UUID> supportedTypes() {
        return new TestFlightData().supportedTypes();
    }

    private double MAX_VARIO = 10.0;
    private double MIN_VARIO = -6.0;
    private void UpdateClimbRate()
    {
        if(turnRate < 1.0) {
            mCurrentClimbRate = -1.1;
        }
        else {
            int numRandom = 10;
            double random = 0;
            for (int count = 0; count < numRandom; count++)
                random += Math.random();
            random /= numRandom;
            double varioRange = MAX_VARIO - MIN_VARIO;
            double varioTarget = (random * varioRange) + MIN_VARIO;
            mCurrentClimbRate += (varioTarget - mCurrentClimbRate) * 0.2;
        }
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

    private static double EARTH_RADIUS = 6378137; // m

    private void UpdateLocation(long deltaTime)
    {
        Vector velocity = new Vector(mCurrentVelocity).Add(mWindVelocity);
        double deltaX = (velocity.X() / 3.6) * (deltaTime / 1000); // kph to m/s and ms to s
        double deltaY = (velocity.Y() / 3.6) * (deltaTime / 1000); // kph to m/s and ms to s
        mLatitude = mLatitude + (180/Math.PI)*(deltaY/EARTH_RADIUS);
        mLongitude = mLongitude + (180/Math.PI)*(deltaX/EARTH_RADIUS)/ Math.cos(Math.PI/180.0*mLatitude);
    }
}

class TestFlightData implements IFlightData {

    private Vector mCurrentVelocity;
    private Vector mWindVelocity;
    private double mAltitude;
    private double mClimbRate;
    private double mLatitude;
    private double mLongitude;

    public TestFlightData() {} // to get around lack of statics in interfaces while accessing supported types

    public TestFlightData(Vector groundVelocity, Vector windVelocity,
                          double altitude, double climbrate,
                          double latitude, double longitude)
    {
        mCurrentVelocity = groundVelocity;
        mWindVelocity = windVelocity;
        mAltitude = altitude;
        mClimbRate = climbrate;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    private double GroundSpeed() {
        return(new Vector(mCurrentVelocity).Add(mWindVelocity).Magnitude());
    }

    private double Bearing() {
        return(new Vector(mCurrentVelocity).Add(mWindVelocity).Direction());
    }

    @Override
    public double get(UUID type) throws java.lang.UnsupportedOperationException
    {
        try {
            if (type == FlightDataType.ALTITUDE)
                return mAltitude;

            if (type == FlightDataType.GROUNDSPEED)
                return GroundSpeed();

            if (type == FlightDataType.BEARING)
                return Bearing();

            if (type == FlightDataType.VARIO)
                return mClimbRate;

            if (type == FlightDataType.LONGITUDE)
                return mLongitude;

            if (type == FlightDataType.LATITUDE)
                return mLatitude;
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public HashSet<UUID> supportedTypes() {
        return new HashSet(Arrays.asList(
                FlightDataType.ALTITUDE,
                FlightDataType.GROUNDSPEED,
                FlightDataType.BEARING,
                FlightDataType.VARIO,
                FlightDataType.LONGITUDE,
                FlightDataType.LATITUDE));
    }
}

