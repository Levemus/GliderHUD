package com.levemus.gliderhud.FlightData.Listeners;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.location.Location;

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataID;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class DistanceFr implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    private final double INVALID =  Double.MIN_VALUE;
    private double mStartLongitude = INVALID;
    private double mStartLatitude = INVALID;

    private UUID mId;

    public DistanceFr() {
        mId = UUID.randomUUID();
    }

    public DistanceFr(double latitude, double longitude) {
        mStartLongitude = latitude;
        mStartLatitude = longitude;
        mId = UUID.randomUUID();
    }

    HashSet<UUID> mRequiredChannels = new HashSet(Arrays.asList(
            FlightDataID.LATITUDE,
            FlightDataID.LONGITUDE
    ));

    @Override
    public List<HashSet<UUID>> requiredChannels() {
        return Arrays.asList(mRequiredChannels);
    }

    @Override
    public long notificationInterval() { return 100; }

    private double mDistance = INVALID;

    public double value() {
        return mDistance;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        double latitude = data.get(FlightDataID.LATITUDE);
        double longtitude = data.get(FlightDataID.LONGITUDE);

        if (mStartLongitude == INVALID ||
                mStartLatitude == INVALID) {
            mStartLatitude = latitude;
            mStartLongitude = longtitude;
        }

        Location launchLocation = new Location("initial");
        launchLocation.setLatitude(mStartLatitude);
        launchLocation.setLongitude(mStartLongitude);
        Location currentLocation = new Location("current");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longtitude);
        mDistance = currentLocation.distanceTo(launchLocation);

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}

    @Override
    public UUID id() { return mId;}

}
