package com.levemus.gliderhud.FlightData.Providers.Recon;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataClient;
import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Providers.IProvider;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;
import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;

/**
 * Created by mark@levemus on 15-12-06.
 */

public class HeadLocationProvider implements HeadLocationListener, IConfiguration, IProvider, IChannelDataProvider {

    private final String TAG = this.getClass().getSimpleName();
    private HUDHeadingManager mHUDHeadingManager = null;

    @Override
    public void stop(Activity activity) {
        mHUDHeadingManager.unregister(this);
    }

    @Override
    public void start(Activity activity) {
        if(mHUDHeadingManager == null)
            mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);
        mHUDHeadingManager.register(this);
    }

    protected IChannelDataClient mClient;
    @Override
    public void registerClient(IChannelDataClient client) {mClient = client;}

    @Override
    public void deRegisterClient(IChannelDataClient client) {
        mClient = null;
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                MessageChannels.YAW));
    }

    @Override
    public UUID id() {
        return UUID.fromString("28ca23b9-c1c7-4419-92b9-2afd940f1d5d");
    }

    private HashMap<UUID, Double> mValues = new HashMap<>();

    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if (Float.isNaN(yaw)) {
            return;
        }

        mValues.put(MessageChannels.YAW, (double)yaw);
    }

    @Override
    public HashMap<UUID, Double> pullFrom(IConfiguration config) {
        HashMap<UUID, Double> result = new HashMap<>();
        for(UUID channel : config.channels())
            result.put(channel, mValues.get(channel));

        return result;
    }
}
