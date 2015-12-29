package com.levemus.gliderhud.FlightData.Broadcasters.Components.Dispatchers;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Configuration.Configuration;
import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;
import com.levemus.gliderhud.FlightData.Messages.IMessage;

import java.util.Date;
import java.util.UUID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Created by mark@levemus on 15-12-28.
 */
public class Dispatcher implements IDispatcher {

    private HashMap<UUID, HashSet<IMessageNotify>> mChannelSubcribers = new HashMap<>();
    private HashMap<IMessageNotify, NotifyInterval> mNotifyIntervals = new HashMap<>();

    private class NotifyInterval{
        public IConfiguration mConfig;
        public long mStartTime = 0;

        NotifyInterval(IConfiguration config) {
            mConfig = config;
        }
    }

    // Subscriber registration
    public void registerWith(IConfiguration config, IMessageNotify subscriber ) {
        HashSet<UUID> subscribedChannels = new HashSet<>();
        for(UUID channel : config.orphanedChannels()) {
            if(!mChannelSubcribers.containsKey(channel))
                mChannelSubcribers.put(channel, new HashSet<>(Arrays.asList(subscriber)));
            else
                mChannelSubcribers.get(channel).add(subscriber);
            subscribedChannels.add(channel);
        }

        config.orphanedChannels().removeAll(subscribedChannels);
        mNotifyIntervals.put(subscriber,
                new NotifyInterval(new Configuration(config.id(), subscribedChannels,
                        config.notificationInterval())));
    }

    public void deregisterFrom(IConfiguration config, IMessageNotify subscriber ) {
        for(HashSet<IMessageNotify> value : mChannelSubcribers.values()) {
            if(value.contains(subscriber))
                value.remove(subscriber);
        }
        config.orphanedChannels().clear();
        config.orphanedChannels().addAll(config.allChannels());
    }

    // Send message to subscribers
    public void postMessage(IConfiguration config, IMessage message) {
        HashSet<IMessageNotify> subscribers = new HashSet<>();
        HashSet<UUID> channels = (HashSet<UUID>) message.channels();
        if (channels != null){
            for (UUID channel : channels) {
                if(mChannelSubcribers.get(channel) != null)
                    subscribers.addAll(mChannelSubcribers.get(channel));
            }
        }
        long time = new Date().getTime();
        for(IMessageNotify subscriber : subscribers) {
            NotifyInterval interval = mNotifyIntervals.get(subscriber);
            if(time - interval.mStartTime > interval.mConfig.notificationInterval()) {
                HashSet<UUID> intersection = new HashSet<>(interval.mConfig.allChannels());
                intersection.retainAll(message.channels());
                if(!intersection.isEmpty())
                    subscriber.onMessage(new Configuration(config.id(),intersection,config.notificationInterval()),
                        message);
                interval.mStartTime = time;
            }
        }
    }
}
