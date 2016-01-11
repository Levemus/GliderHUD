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

import com.levemus.gliderhud.FlightData.Managers.IClient;

/**
 * Created by mark@levemus on 15-12-27.
 */

public interface IProvider {
    void start(Activity activity);
    void stop(Activity activity);

    void registerClient(IClient client);
    void deRegisterClient(IClient client);
}
