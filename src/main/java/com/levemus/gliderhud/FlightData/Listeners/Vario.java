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
import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;

import java.util.EnumSet;

/**
 * Created by mark@levemus on 15-12-02.
 */
public class Vario extends FlightDataBroadcaster implements IFlightDataListener {

    private long VARIO_UPDATE_INTERVAL_MS = 100;

    // IFlightDataBroadcaster
    @Override
    public EnumSet<IFlightData.FlightDataType> supportedTypes() {
        return new VarioFlightData().supportedTypes();
    }

    @Override
    public void init(Activity activity) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    // IFlightDataListener
    @Override
    public void onData(IFlightData data) {
        try {
            double altitude = data.getData(IFlightData.FlightDataType.ALTITUDE);
        }
        catch(java.lang.UnsupportedOperationException e){}
        notifyListeners(new VarioFlightData(0)); // TODO: need to populate vario data - exponential moving avg?
    }

    EnumSet<IFlightData.FlightDataType> mSubscriptionFlags = EnumSet.of(
            IFlightData.FlightDataType.ALTITUDE);

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster) {
        broadcaster.addListener(this, VARIO_UPDATE_INTERVAL_MS, mSubscriptionFlags);
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
    public double getData(FlightDataType type) throws java.lang.UnsupportedOperationException
    {
        try {
            if (type == FlightDataType.VARIO)
                return mVario;
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public EnumSet<FlightDataType> supportedTypes() {
        return EnumSet.of(
                IFlightData.FlightDataType.VARIO);
    }
}
