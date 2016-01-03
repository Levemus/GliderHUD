package com.levemus.gliderhud.FlightData.Providers.Test;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.HashSet;

import android.content.Intent;

import com.levemus.gliderhud.FlightData.Configuration.Configuration;
import com.levemus.gliderhud.FlightData.Messages.Data.DataMessage;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
import com.levemus.gliderhud.FlightData.Providers.ServiceProvider;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.Angle;

/**
 * Created by mark@levemus on 16-01-01.
 */

public class TestService extends ServiceProvider {
    private final String TAG = this.getClass().getSimpleName();

    private long DATA_GENERATE_PERIOD = 500;
    public int _onStartCommand(Intent intent, int flags, int startId) {

        Runnable dataGenerator = new Runnable() {
            public void run() {
                generateTestData();
                mLocalHandler.postDelayed(this, DATA_GENERATE_PERIOD);
            }
        };
        mLocalHandler.postDelayed(dataGenerator, DATA_GENERATE_PERIOD);
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private double startAltitude = 500; // m
    private double turnRate = 0.0; // degree / sec
    private double airspeed = 50.0; // kph
    private Vector mWindVelocity =  new Vector(10,0);

    private double mCurrentAltitude = startAltitude;
    private Vector mCurrentVelocity =  new Vector(-1*airspeed,0);
    private double mCurrentClimbRate = 0;
    private double mLatitude = 0;
    private double mLongitude = 0;

    private long MS_PER_SECOND = 1000;
    private double DEGREES_PER_CIRCLE = 360;
    private long timeOfLastUpdate = 0;

    private double MAX_VARIO = 10.0;
    private double MIN_VARIO = -6.0;
    private void updateClimbRate()
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

    private void updateAltitude(long deltaTime)
    {
        mCurrentAltitude += (deltaTime * mCurrentClimbRate) / MS_PER_SECOND;
    }

    private void updateVelocity(long deltaTime)
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

    private void updateLocation(long deltaTime)
    {
        Vector velocity = new Vector(mCurrentVelocity).Add(mWindVelocity);
        double deltaX = (velocity.X() / 3.6) * (deltaTime / 1000); // kph to m/s and ms to s
        double deltaY = (velocity.Y() / 3.6) * (deltaTime / 1000); // kph to m/s and ms to s
        mLatitude = mLatitude + (180/Math.PI)*(deltaY/EARTH_RADIUS);
        mLongitude = mLongitude + (180/Math.PI)*(deltaX/EARTH_RADIUS)/ Math.cos(Math.PI/180.0*mLatitude);
    }

    private void generateTestData() {

        long currentTime = new Date().getTime();
        if(timeOfLastUpdate == 0)
            timeOfLastUpdate = currentTime;
        long deltaTime = currentTime - timeOfLastUpdate;

        if(mCurrentAltitude > startAltitude + 200
                && turnRate != 0.0
                && Angle.delta(mCurrentVelocity.Direction(), mWindVelocity.Direction()) < 20) {
            turnRate = 0.0;
            mCurrentVelocity.SetDirectionAndMagnitude(mCurrentVelocity.Direction(), 50);
        }
        else if (mCurrentAltitude < startAltitude && turnRate == 0.0){
            turnRate = 15.0;
            mCurrentVelocity.SetDirectionAndMagnitude(mCurrentVelocity.Direction(), 30);
        }

        updateClimbRate();
        updateVelocity(deltaTime);
        updateAltitude(deltaTime);
        updateLocation(deltaTime);

        Vector combinedVelocity = new Vector(mCurrentVelocity).Add(mWindVelocity);
        HashMap<UUID, Double> values = new HashMap<>();

        values.put(MessageChannels.GROUNDSPEED, combinedVelocity.Magnitude() / 3.6);
        values.put(MessageChannels.BEARING, combinedVelocity.Direction());
        values.put(MessageChannels.VARIO, mCurrentClimbRate);
        values.put(MessageChannels.LONGITUDE, mLongitude);
        values.put(MessageChannels.LATITUDE, mLatitude);
        values.put(MessageChannels.ALTITUDE, mCurrentAltitude);

        sendMsg(new Configuration(id(), new HashSet<>(values.keySet())),
                new Date().getTime(),
                new DataMessage(values));

        timeOfLastUpdate = currentTime;
    }
}
