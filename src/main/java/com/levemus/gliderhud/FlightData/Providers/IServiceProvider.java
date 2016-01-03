package com.levemus.gliderhud.FlightData.Providers;

import java.util.UUID;

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataClient;

/**
 * Created by mark@levemus on 16-01-02.
 */

public interface IServiceProvider {
    void start(Activity activity, Class service, UUID id);
    void stop(Activity activity, Class service);

    void registerClient(IChannelDataClient client);
    void deRegisterClient(IChannelDataClient client);
}
