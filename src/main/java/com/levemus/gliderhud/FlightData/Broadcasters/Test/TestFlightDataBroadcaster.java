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
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class TestFlightDataBroadcaster extends FlightDataBroadcaster implements HeadLocationListener {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    private double startAltitude = 500; // m
    private double climbRate = 0.5; // m/s
    private double turnRate = 5.0; // degree / sec
    private double airspeed = 30.0; // kph
    private Vector mWindVelocity =  new Vector(10,0);

    private double currentAltitude = startAltitude;
    private Vector mCurrentVelocity =  new Vector(-1*airspeed,0);
    private double currentClimbRate = climbRate;
    private double mYaw = 0;

    private Handler mHandler = null;
    private int mInterval = 100;
    private long MS_PER_SECOND = 1000;
    private double DEGREES_PER_CIRCLE = 360;
    private long timeOfLastUpdate = 0;

    private HUDHeadingManager mHUDHeadingManager = null;

    private EnumSet<IFlightData.FlightDataType> mSupportedTypes = EnumSet.of(
            IFlightData.FlightDataType.YAW,
            IFlightData.FlightDataType.ALTITUDE,
            IFlightData.FlightDataType.GROUNDSPEED,
            IFlightData.FlightDataType.BEARING,
            IFlightData.FlightDataType.GLIDE,
            IFlightData.FlightDataType.VARIO);

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
        mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);
        mHandler = new Handler();
        mDataNotifer.run();
        mHUDHeadingManager.register(this);
    }

    @Override
    public void pause() {
        mHUDHeadingManager.unregister(this);
    }

    @Override
    public void resume() {
        mHUDHeadingManager.register(this);
    }

    @Override
    public EnumSet<IFlightData.FlightDataType> getSupportedTypes()
    {
        return mSupportedTypes;
    }

    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if ((Float.isNaN(yaw))) {
            return;
        }

        mYaw = yaw;
        NotifyListeners(new TestFlightData(null, null, mYaw), EnumSet.of(IFlightData.FlightDataType.YAW));
    }

    private void NotifyListeners()
    {
        NotifyListeners(new TestFlightData(mCurrentVelocity, mWindVelocity, mYaw), mSupportedTypes);
    }

    private void UpdateAltitude(long deltaTime)
    {
        currentAltitude += (deltaTime * currentClimbRate) / MS_PER_SECOND;
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

    private class TestFlightData implements IFlightData {

        private Vector mCurrentVelocity;
        private Vector mWindVelocity;
        private double mYaw;
        private int MIN_SPEED = 0;

        public TestFlightData(Vector groundVelocity, Vector windVelocity, double yaw)
        {
            mCurrentVelocity = groundVelocity;
            mWindVelocity = windVelocity;
            mYaw = yaw;
        }

        private double GroundSpeed() {
            return(Math.max(Math.round(new Vector(mCurrentVelocity).Add(mWindVelocity).Magnitude() * 10) / 10, MIN_SPEED));
        }

        @Override
        public double getData(FlightDataType type) throws java.lang.UnsupportedOperationException
        {
            try {
                if (type == FlightDataType.ALTITUDE)
                    return currentAltitude;

                if (type == FlightDataType.GROUNDSPEED)
                    return GroundSpeed();

                if (type == FlightDataType.BEARING)
                    return new Vector(mCurrentVelocity).Add(mWindVelocity).Direction();

                if (type == FlightDataType.GLIDE)
                    return (currentClimbRate != 0 ? GroundSpeed() / climbRate : 0);

                if (type == FlightDataType.VARIO)
                    return currentClimbRate;

                if (type == FlightDataType.YAW)
                    return mYaw;
            }
            catch(Exception e) {}
            throw new java.lang.UnsupportedOperationException();
        }
    }
}
