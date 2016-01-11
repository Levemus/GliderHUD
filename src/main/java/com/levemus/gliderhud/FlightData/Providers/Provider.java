package com.levemus.gliderhud.FlightData.Providers;

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

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Managers.IClient;

/**
 * Created by mark@levemus on 15-11-29.
 */

public abstract class Provider implements IProvider, IIdentifiable, IChannelized {

    // IProvider
    @Override
    public void start(Activity activity) {}
    @Override
    public void stop(Activity activity) {}

    @Override
    public void registerClient(IClient client) {}
    @Override
    public void deRegisterClient(IClient client) {}
}
