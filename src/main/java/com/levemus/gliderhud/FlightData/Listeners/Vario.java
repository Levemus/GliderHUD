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

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import android.util.Log;

/**
 * Created by mark@levemus on 15-12-02.
 */
public class Vario extends FlightDataBroadcaster implements IFlightDataListener {

    private final String TAG = this.getClass().getSimpleName();

    private long VARIO_UPDATE_INTERVAL_MS = 100;

    private class RawVario {
        double mVario;
        public RawVario(double vario) {mVario = vario;}
    }

    private int MAX_RAW_VARIO_ENTRIES = 5;
    private double MIN_VARIO = 0.10;

    private ArrayList<RawVario> mVarioRaw = new ArrayList<RawVario>();
    private double mAvgVario = 0;

    // IFlightDataBroadcaster
    @Override
    public HashSet<UUID> supportedTypes() {
        return new VarioFlightData().supportedTypes();
    }

    @Override
    public void init(Activity activity) {}

    @Override
    public void pause(Activity activity) {}

    @Override
    public void resume(Activity activity) {}

    // IFlightDataListener
    @Override
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        try {
            double value = data.get(FlightDataType.VARIORAW);
            mVarioRaw.add( new RawVario(value));
            if(mVarioRaw.size() > MAX_RAW_VARIO_ENTRIES)
                mVarioRaw.remove(0);

            for(RawVario currentVario : mVarioRaw) {
                mAvgVario += currentVario.mVario; // TODO: Running weighted average
            }

            mAvgVario /= mVarioRaw.size();
        }
        catch(java.lang.UnsupportedOperationException e){}
        mAvgVario = Math.round(mAvgVario * 100);
        mAvgVario /= 100;
        if(Math.abs(mAvgVario) < MIN_VARIO)
            mAvgVario = 0;
        notifyListeners(new VarioFlightData(mAvgVario));
    }

    public void onStatus(IFlightDataBroadcaster broadcaster, BroadcasterStatus status) {}

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataType.VARIORAW));

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster) {
        if(!mSubscriptionFlags.isEmpty()) {
            HashSet<UUID> result = broadcaster.addListener(this, VARIO_UPDATE_INTERVAL_MS, mSubscriptionFlags);
            mSubscriptionFlags.removeAll(result);
        }
    }
}


class VarioFlightData implements IFlightData{
    private double mVario;

    public VarioFlightData() {} // to get around lack of statics in interfaces while accessing supported types
    public VarioFlightData(double vario)
    {
        mVario = vario;
    }

    @Override
    public double get(UUID type) throws java.lang.UnsupportedOperationException
    {
        try {
            if (type == FlightDataType.VARIO)
                return mVario;
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public HashSet<UUID> supportedTypes() {
        return new HashSet(Arrays.asList(
                FlightDataType.VARIO));
    }
}
