package com.levemus.gliderhud.FlightData.Providers.Android;

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

import com.levemus.gliderhud.FlightData.Managers.IClient;
import com.levemus.gliderhud.FlightData.Providers.Provider;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class InternalGPSProvider extends Provider {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public HashSet<UUID> channels() {
        return(mService.channels());
    }

    @Override
    public UUID id() {
        return(mService.id());
    }

    private InternalGPSService mService = new InternalGPSService();

    @Override
    public void start(final Activity activity) {
        mService.start(activity, InternalGPSService.class, id());
    }

    @Override
    public void stop(Activity activity) {
        mService.stop(activity, InternalGPSService.class);
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
