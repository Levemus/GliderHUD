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

import com.levemus.gliderhud.FlightData.Broadcasters.Broadcaster;
import com.levemus.gliderhud.FlightData.FlightData;
import com.levemus.gliderhud.FlightData.FlightDataChannel;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
/**
 * Created by mark@levemus on 15-12-06.
 */
public class HeadLocationDataBroadcaster extends Broadcaster implements HeadLocationListener {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();
    private HUDHeadingManager mHUDHeadingManager = null;

    @Override
    public void init(Activity activity) {
        super.init(activity);
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
    public HashSet<UUID> supportedChannels() {
        return new HashSet(Arrays.asList(
                FlightDataChannel.YAW));
    }

    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if (Float.isNaN(yaw)) {
            return;
        }

        HashMap<UUID, Double> values = new HashMap<>();
        values.put(FlightDataChannel.YAW, (double)yaw);

        mDataListeners.notifyListeners(this, supportedChannels(), new FlightData(values));
    }
}
