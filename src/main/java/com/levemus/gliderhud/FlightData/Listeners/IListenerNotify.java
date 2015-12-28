package com.levemus.gliderhud.FlightData.Listeners;

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.IFlightData;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 15-12-27.
 */
public interface IListenerNotify {
    void onData(HashSet<UUID> channels, IFlightData value);
    void onStatus(HashSet<UUID> channels, BroadcasterStatus value);
}
