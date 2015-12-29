package com.levemus.gliderhud.FlightData.Broadcasters.Components.Filters;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;

/**
 * Created by mark@levemus on 15-12-28.
 */
public interface IFilter {

    // Subscriber registration
    void registerWith(IConfiguration config, IMessageNotify subscriber );
    void deregisterFrom(IConfiguration config, IMessageNotify subscriber );
}
