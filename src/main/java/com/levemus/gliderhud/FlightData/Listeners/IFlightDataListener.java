package com.levemus.gliderhud.FlightData.Listeners;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;

/**
 * Created by mark@levemus on 15-11-23.
 */
public interface IFlightDataListener {

    void onData(IFlightDataBroadcaster broadcaster, IFlightData data);
    void onStatus(IFlightDataBroadcaster broadcaster, BroadcasterStatus status);
    void registerWith(IFlightDataBroadcaster broadcaster);
}
