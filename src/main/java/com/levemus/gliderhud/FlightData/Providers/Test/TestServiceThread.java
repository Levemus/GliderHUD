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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.Angle;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public class TestServiceThread extends ServiceProviderThread implements IChannelized, IIdentifiable {

    private final String TAG = this.getClass().getSimpleName();

    public TestServiceThread(String id) {
        super(id);
    }

    private long DATA_GENERATE_PERIOD = 500; // ms
    private long DATA_GENERATE_LOCATION_PERIOD = 5 * 1000; // ms

    private Handler mLocalHandler;

    @Override
    public void run() {
        Looper.prepare();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = (Bundle)msg.obj;
                SerializablePayloadMessage message = (SerializablePayloadMessage) bundle.getSerializable("MSG");
                onRequest(message);
            }
        };

        try {
            mLocalHandler = new Handler();
            Runnable dataGenerator = new Runnable() {
                public void run() {
                    generateTestData();
                    mLocalHandler.postDelayed(this, DATA_GENERATE_PERIOD);
                }
            };
            mLocalHandler.postDelayed(dataGenerator, DATA_GENERATE_PERIOD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mLocalHandler = new Handler();
            Runnable dataGenerator = new Runnable() {
                public void run() {
                    generateTestDataLocation();
                    mLocalHandler.postDelayed(this, DATA_GENERATE_LOCATION_PERIOD);
                }
            };
            mLocalHandler.postDelayed(dataGenerator, DATA_GENERATE_LOCATION_PERIOD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Looper.loop();
    }

    private double startAltitude = 1061; // m
    private double turnRate = 0.0; // degree / sec
    private double airspeed = 50.0; // kph
    private Vector mWindVelocity =  new Vector(10,0);

    private double mCurrentAltitude = startAltitude;
    private Vector mCurrentVelocity =  new Vector(-1*airspeed,0);
    private double mCurrentClimbRate = 0;
    private double mLatitude = -18.886100;
    private double mLongitude = -41.915700;

    private long MS_PER_SECOND = 1000;
    private double DEGREES_PER_CIRCLE = 360;
    private long mTimeOfLastUpdateGeneral = 0;
    private long mTimeOfLastLocationUpdate = 0;

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
        if(mTimeOfLastUpdateGeneral == 0)
            mTimeOfLastUpdateGeneral = currentTime;
        long deltaTime = currentTime - mTimeOfLastUpdateGeneral;

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

        Vector combinedVelocity = new Vector(mCurrentVelocity).Add(mWindVelocity);
        HashMap<UUID, Double> values = new HashMap<>();

        values.put(Channels.GROUNDSPEED, combinedVelocity.Magnitude() / 3.6);
        values.put(Channels.BEARING, combinedVelocity.Direction());
        values.put(Channels.VARIO, mCurrentClimbRate);
        values.put(Channels.ALTITUDE, mCurrentAltitude);

        sendResponse(new DataMessage( id(), channels(),
                new Date().getTime(), values));

        mTimeOfLastUpdateGeneral = currentTime;
    }

    private void generateTestDataLocation() {
        long currentTime = new Date().getTime();
        if(mTimeOfLastLocationUpdate == 0)
            mTimeOfLastLocationUpdate = currentTime;
        long deltaTime = currentTime - mTimeOfLastLocationUpdate;

        updateLocation(deltaTime);

        HashMap<UUID, Double> values = new HashMap<>();

        values.put(Channels.LONGITUDE, mLongitude);
        values.put(Channels.LATITUDE, mLatitude);

        sendResponse(new DataMessage( id(), channels(),
                new Date().getTime(), values));

        mTimeOfLastLocationUpdate = currentTime;
    }

    @Override
    public UUID id() {
        return UUID.fromString("39e961ed-3eb5-46a8-9eb4-5ee70d09219b");
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                Channels.LATITUDE,
                Channels.LONGITUDE,
                Channels.ALTITUDE,
                Channels.GROUNDSPEED,
                Channels.BEARING,
                Channels.VARIO));
    }

}
