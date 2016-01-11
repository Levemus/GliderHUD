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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Managers.IClient;
import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

/**
 * Created by mark@levemus on 15-12-06.
 */

public class HeadLocationProvider extends Provider implements HeadLocationListener {

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

    protected IClient mClient;
    @Override
    public void registerClient(IClient client) {mClient = client;}

    @Override
    public void deRegisterClient(IClient client) {
        mClient = null;
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                Channels.YAW));
    }

    @Override
    public UUID id() {
        return UUID.fromString("28ca23b9-c1c7-4419-92b9-2afd940f1d5d");
    }

    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if (Float.isNaN(yaw)) {
            return;
        }

        HashMap<UUID, Double> values = new HashMap<>();
        values.put(Channels.YAW, (double)yaw);

        if(mClient != null)
            mClient.onMsg( new DataMessage(id(), channels(), new Date().getTime(), values));
    }
}
