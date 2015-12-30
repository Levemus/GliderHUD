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
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-28.
 */
public class Filter implements IMessageNotify, IFilter {

    public Filter() {}
    public Filter(IConfiguration config, IMessageNotify subscriber ) {
        registerWith(config, subscriber);
    }

    // Filter messages - first sender through a channel becomes filter for channel
    protected HashMap<UUID, UUID> mChannelToSourceId = new HashMap<>();

    @Override
    public void onMessage(IConfiguration config, IMessage message) {
        for(UUID channel : config.allChannels()) {
            if(!mChannelToSourceId.containsKey(channel) ||
                    mChannelToSourceId.get(channel) == null) {
                mChannelToSourceId.put(channel, config.id());
            }

            if(mChannelToSourceId.get(channel).compareTo(config.id())== 0) {
                // send the msg
                if(mSubscriber != null) {
                    mSubscriber.onMessage(config, message);
                    return;
                }
            }
        }
    }

    // Subscriber registration
    protected IMessageNotify mSubscriber = null;

    public void registerWith(IConfiguration config, IMessageNotify subscriber ) {
        mSubscriber = subscriber;
    }

    public void deregisterFrom(IConfiguration config, IMessageNotify subscriber ) {
        mSubscriber = null;
    }
}
