package com.levemus.gliderhud.FlightData.Pipeline;

/**
 * Created by markcarter on 16-02-12.
 */
public interface MessageBroadcaster {
    void add(MessageListener client);
    void remove(MessageListener client);
}
