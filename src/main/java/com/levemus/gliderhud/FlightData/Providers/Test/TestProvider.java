package com.levemus.gliderhud.FlightData.Providers.Test;

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
import java.util.HashSet;
import java.util.UUID;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataClient;
import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;

/**
 * Created by mark@levemus on 15-11-23.
 */

public class TestProvider extends Provider {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                MessageChannels.ALTITUDE,
                MessageChannels.GROUNDSPEED,
                MessageChannels.BEARING,
                MessageChannels.VARIO,
                MessageChannels.LONGITUDE,
                MessageChannels.LATITUDE
                ));
    }

    @Override
    public UUID id() {
        return UUID.fromString("39e961ed-3eb5-46a8-9eb4-5ee70d09219b");
    }

    private TestService service = new TestService();

    @Override
    public void start(final Activity activity) {
        service.start(activity, TestService.class, id());
    }

    @Override
    public void stop(Activity activity) {
        service.stop(activity, TestService.class);
    }

    @Override
    public void registerClient(IChannelDataClient client) {
        service.registerClient(client);
    }

    @Override
    public void deRegisterClient(IChannelDataClient client) {
        service.deRegisterClient(client);
    }
}

