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

import java.util.HashSet;
import java.util.UUID;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Managers.IClient;
import com.levemus.gliderhud.FlightData.Providers.Provider;

/**
 * Created by mark@levemus on 15-11-23.
 */

public class TestProvider extends Provider implements IIdentifiable, IChannelized {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();
    private TestService mService = new TestService();

    @Override
    public HashSet<UUID> channels() {
        return mService.channels();
    }

    @Override
    public UUID id() {
        return mService.id();
    }

    @Override
    public void start(final Activity activity) {
        mService.start(activity, TestService.class, id());
    }

    @Override
    public void stop(Activity activity) {
        mService.stop(activity, TestService.class);
    }

    @Override
    public void registerClient(IClient client) {
        mService.registerClient(client);
    }

    @Override
    public void deRegisterClient(IClient client) {
        mService.deRegisterClient(client);
    }
}

