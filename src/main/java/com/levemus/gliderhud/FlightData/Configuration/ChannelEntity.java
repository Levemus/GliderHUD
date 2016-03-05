package com.levemus.gliderhud.FlightData.Configuration;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 16-01-06.
 */
public interface ChannelEntity {
    HashSet<UUID> channels();
    UUID id();
}
