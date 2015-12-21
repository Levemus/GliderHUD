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
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-19.
 */
public class DistanceFr implements IFlightDataListener {

    IFlightDataClient mClient;

    private double mStartLongitude;
    private double mStartLatitude;

    public DistanceFr(IFlightDataClient client) {
        mStartLongitude = Double.MIN_VALUE;
        mStartLatitude = Double.MIN_VALUE;

        mClient = client;
    }

    public DistanceFr(IFlightDataClient client, double latitude, double longitude) {
        mStartLongitude = latitude;
        mStartLatitude = longitude;

        mClient = client;
    }

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataID.LATITUDE,
            FlightDataID.LONGITUDE
    ));

    private int UPDATE_INTERVAl_MS = 100;
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
        HashSet<UUID> result = new HashSet<>();
        if(!mSubscriptionFlags.isEmpty()) {
            result = broadcaster.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.removeAll(result);
        }

        return result;
    }

    private double mDistance = 0;

    public double value() {
        return mDistance;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        double latitude = data.get(FlightDataID.LATITUDE);
        double longtitude = data.get(FlightDataID.LONGITUDE);

        if (mStartLongitude == Double.MIN_VALUE ||
                mStartLatitude == Double.MIN_VALUE) {
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

        if(mClient != null)
            mClient.onDataReady(false);
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}
}
