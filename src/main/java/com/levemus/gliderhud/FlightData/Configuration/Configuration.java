package com.levemus.gliderhud.FlightData.Configuration;

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

/**
 * Created by mark@levemus on 15-12-29.
 */
public class Configuration implements IConfiguration {

    private UUID mId;
    @Override
    public UUID id() { return mId; }

    private HashSet<UUID>  mChannels;
    @Override
    public HashSet<UUID> allChannels() { return mChannels; }

    private HashSet<UUID>  mOrphanedChannels;
    @Override
    public HashSet<UUID> orphanedChannels() { return mOrphanedChannels; }

    private long mNotificationInterval;
    @Override
    public long notificationInterval() { return mNotificationInterval; }

    public Configuration(UUID id, HashSet<UUID> channels, long notificationInterval) {
        mId = id;
        mChannels = channels;
        mOrphanedChannels = new HashSet<>(mChannels);
        mNotificationInterval = notificationInterval;
    }
}
