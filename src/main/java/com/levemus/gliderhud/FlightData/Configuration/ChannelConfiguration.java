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
public class ChannelConfiguration extends Configuration implements IChannelized {

    private HashSet<UUID>  mChannels;
    @Override
    public HashSet<UUID> channels() { return mChannels; }

    public ChannelConfiguration() {
        super(UUID.randomUUID());
    }

    public ChannelConfiguration(UUID id, HashSet<UUID> channels) {
        super(id);
        mChannels = channels;
    }

    @Override
    public String toString() {
        return "Configuration [mId=" + mId.toString() + " mChannels= "+ mChannels.toString() + "]";
    }
}
