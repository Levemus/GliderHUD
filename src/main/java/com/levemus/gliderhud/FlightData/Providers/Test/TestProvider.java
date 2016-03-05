package com.levemus.gliderhud.FlightData.Providers.Test;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Types.Vector;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-11-23.
 */

public class TestProvider extends Provider {
    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    private long DATA_GENERATE_PERIOD = 500; // ms
    private long DATA_GENERATE_LOCATION_PERIOD = 1 * 1000; // ms

    private Handler mLocalHandler;
    public void start(Context ctx) {
        try {
            mLocalHandler = new Handler();
            Runnable dataGenerator = new Runnable() {
                public void run() {
                    generateTestData();
                    mLocalHandler.postDelayed(this, DATA_GENERATE_PERIOD);
                }
            };

            mLocalHandler.postDelayed(dataGenerator, DATA_GENERATE_PERIOD);

            Runnable locationGenerator = new Runnable() {
                public void run() {
                    generateTestDataLocation();
                    mLocalHandler.postDelayed(this, DATA_GENERATE_LOCATION_PERIOD);
                }
            };
            mLocalHandler.postDelayed(locationGenerator, DATA_GENERATE_LOCATION_PERIOD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double startAltitude = 1061; // m
    private double turnRate = 0.0; // degree / sec
    private double airspeed = 50.0; // kph
    private Vector mWindVelocity =  new Vector(-5,0);

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
            Location currentLocation = new Location("Current Location");
            currentLocation.setLatitude(mLatitude);
            currentLocation.setLongitude(mLongitude);
            currentLocation.setAltitude(mCurrentAltitude);

            // Get thermal location at this altitude
            Double thermalStartAtitude = mThermalCoreLocation.getAltitude();
            Double thermalStartLatitude = mThermalCoreLocation.getLatitude();
            Double thermalStartLongitude = mThermalCoreLocation.getLongitude();

            Double deltaAltitude = mCurrentAltitude - thermalStartAtitude;
            Double deltaTime = deltaAltitude / mThermalCoreStrength;

            Vector velocity = mWindVelocity; // add wind drift to the thermal
            double deltaX = (velocity.X() / 3.6) * (deltaTime / 1000); // kph to m/s and ms to s
            double deltaY = (velocity.Y() / 3.6) * (deltaTime / 1000); // kph to m/s and ms to s
            Double thermalLatitude = thermalStartLatitude + (180/Math.PI)*(deltaY/EARTH_RADIUS);
            Double thermalLongitude = thermalStartLongitude + (180/Math.PI)*(deltaX/EARTH_RADIUS)/ Math.cos(Math.PI/180.0*thermalLatitude);

            Location thermalCoreAtCurrentAltitude = new Location("Current Core");
            thermalCoreAtCurrentAltitude.setLatitude(thermalLatitude);
            thermalCoreAtCurrentAltitude.setLongitude(thermalLongitude);
            float distanceFromCore = thermalCoreAtCurrentAltitude.distanceTo(currentLocation);
            mCurrentClimbRate = Math.max(0.5, mThermalCoreStrength * 1 - distanceFromCore / mThermalRadius);
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
            mCurrentVelocity.setDirectionAndMagnitude(newHeading, mCurrentVelocity.Magnitude());
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

        if(mCurrentAltitude > startAltitude + 1000
                && turnRate != 0.0) {
            turnRate = 0.0;
            mCurrentVelocity.setDirectionAndMagnitude(((mWindVelocity.Direction() + 180.0) % 360), 50); // else go downwind on bar
        }
        else if (mCurrentAltitude < startAltitude && turnRate == 0.0){
            generateThermal();
            turnRate = 15.0;
            mCurrentVelocity.setDirectionAndMagnitude(mCurrentVelocity.Direction(), 30);
        }

        updateClimbRate();
        updateVelocity(deltaTime);
        updateAltitude(deltaTime);

        HashMap<UUID, Double> values = new HashMap<>();
        Vector combinedVelocity = new Vector(mCurrentVelocity).Add(mWindVelocity);

        values.put(Channels.GROUNDSPEED, combinedVelocity.Magnitude() / 3.6);
        values.put(Channels.BEARING, combinedVelocity.Direction());
        values.put(Channels.VARIO, mCurrentClimbRate);
        values.put(Channels.ALTITUDE, mCurrentAltitude);
        values.put(Channels.GPSALTITUDE, mCurrentAltitude);
        values.put(Channels.PRESSUREALTITUDE, mCurrentAltitude);
        values.put(Channels.TIME, (double)new Date().getTime());

        if(mClient != null)
            mClient.onMsg(new DataMessage(id(), new HashSet(values.keySet()), new Date().getTime(), values));

        mTimeOfLastUpdateGeneral = currentTime;
    }

    private Location mThermalCoreLocation;
    private Double mThermalCoreStrength;
    private Double mThermalRadius;

    private void generateThermal() {
        mThermalCoreLocation = new Location("Test Thermal");
        mThermalCoreLocation.setLatitude(mLatitude);
        mThermalCoreLocation.setLongitude(mLongitude);
        mThermalCoreLocation.setAltitude(mCurrentAltitude);

        int numRandom = 10;
        double random = 0;
        for (int count = 0; count < numRandom; count++)
            random += Math.random();
        random /= numRandom;
        double varioRange = MAX_VARIO - MIN_VARIO;
        mThermalCoreStrength = 2.0; //(random * varioRange) + MIN_VARIO;
        mThermalRadius = 30.0; // 200 meters for now
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

        if(mClient != null)
            mClient.onMsg(new DataMessage(id(), new HashSet(values.keySet()), new Date().getTime(), values));

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
                Channels.GPSALTITUDE,
                Channels.PRESSUREALTITUDE,
                Channels.TIME,
                Channels.GROUNDSPEED,
                Channels.BEARING,
                Channels.VARIO));
    }
}

