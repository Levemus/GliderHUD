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
public class HeightAbv implements IFlightDataListener {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    private double mStartAltitude;
    private UUID mId;

    public HeightAbv() {
        mStartAltitude = Double.MIN_VALUE;
        mId = UUID.randomUUID();
    }

    public HeightAbv(double startAltitude) {
        mStartAltitude = startAltitude;
        mId = UUID.randomUUID();
    }

    HashSet<UUID> mRequiredChannels = new HashSet(Arrays.asList(
            FlightDataID.ALTITUDE
    ));

    @Override
    public List<HashSet<UUID>> requiredChannels() {
        return Arrays.asList(mRequiredChannels);
    }

    @Override
    public long notificationInterval() {return 100; }

    private final double INVALID =  Double.MIN_VALUE;
    private double mHeight = INVALID;

    public double value() {
        return mHeight;
    }

    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {

        try {
            double altitude = data.get(FlightDataID.ALTITUDE);
            if(mStartAltitude == Double.MIN_VALUE)
                mStartAltitude = altitude;
            mHeight = altitude - mStartAltitude;
        }
        catch(java.lang.UnsupportedOperationException e){}

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}

    @Override
    public UUID id() { return mId;}
}
