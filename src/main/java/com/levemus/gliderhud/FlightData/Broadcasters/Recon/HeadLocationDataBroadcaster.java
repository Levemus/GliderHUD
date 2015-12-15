package com.levemus.gliderhud.FlightData.Broadcasters.Recon;

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

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

import java.util.EnumSet;

/**
 * Created by mark@levemus on 15-12-06.
 */
public class HeadLocationDataBroadcaster extends FlightDataBroadcaster implements HeadLocationListener {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();
    private HUDHeadingManager mHUDHeadingManager = null;

    @Override
    public void init(Activity activity)
    {
        mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);
    }

    @Override
    public void pause(Activity activity) {
            mHUDHeadingManager.unregister(this);
        }

    @Override
    public void resume(Activity activity) {
            mHUDHeadingManager.register(this);
        }

    @Override
    public EnumSet<IFlightData.FlightDataType> supportedTypes() {
        return new HeadLocationFlightData().supportedTypes();
    }

    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if (Float.isNaN(yaw)) {
            return;
        }

        notifyListeners(new HeadLocationFlightData(yaw));
    }
}

class HeadLocationFlightData implements IFlightData {

    private double mYaw;

    public HeadLocationFlightData() {} // to get around lack of statics in interfaces while accessing supported types

    public HeadLocationFlightData(double yaw)
    {
        mYaw = yaw;
    }

    @Override
    public double get(FlightDataType type) throws java.lang.UnsupportedOperationException
    {
        try {
            if (type == FlightDataType.YAW)
                return mYaw;
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public EnumSet<FlightDataType> supportedTypes() {
        return EnumSet.of(IFlightData.FlightDataType.YAW);
    }
}
