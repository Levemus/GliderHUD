package com.levemus.gliderhud.FlightData.Broadcasters.Recon;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

import java.util.EnumSet;

/**
 * Created by markcarter on 15-12-06.
 */
public class HeadLocationDataBroadcaster extends FlightDataBroadcaster implements HeadLocationListener {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();
    private HUDHeadingManager mHUDHeadingManager = null;

    private EnumSet<IFlightData.FlightDataType> mSupportedTypes = EnumSet.of(
                IFlightData.FlightDataType.YAW);

    @Override
    public void init(Activity activity)
    {
        mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);
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
        if (Float.isNaN(yaw)) {
            return;
        }

        NotifyListeners(new HeadFlightData(yaw),
                EnumSet.of(IFlightData.FlightDataType.YAW));
    }

    private class HeadFlightData implements IFlightData {

        private double mYaw;

        public HeadFlightData (double yaw)
        {
            mYaw = yaw;
        }

        @Override
        public double getData(FlightDataType type) throws java.lang.UnsupportedOperationException
        {
            try {
                if (type == FlightDataType.YAW)
                    return mYaw;
            }
            catch(Exception e) {}
            throw new java.lang.UnsupportedOperationException();
        }
    }
}
