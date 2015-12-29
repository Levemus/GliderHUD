package com.levemus.gliderhud.FlightData.Broadcasters;

import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Broadcasters.Components.Filters.Filter;
import com.levemus.gliderhud.FlightData.Broadcasters.Components.Filters.StatusFilter;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;

import java.util.HashSet;
import java.util.UUID;
import java.util.HashMap;

/**
 * Created by markcarter on 15-12-28.
 */
public class Multiplexer extends Broadcaster implements IMessageNotify {

    private Filter mFilter = new StatusFilter(this, this);

    @Override
    public void onMessage(IConfiguration config, IMessage value) {
        mDispatcher.postMessage(config, value);
    }

    // Subscriber registration
    private HashMap<UUID, HashSet<UUID>> mChannelSets = new HashMap<>();
    private HashSet<Broadcaster> mBroadcasters = new HashSet<>();

    public void registerWith(Broadcaster broadcaster) {
        mBroadcasters.add(broadcaster);
        mChannelSets.put(broadcaster.id(), broadcaster.allChannels());
        broadcaster.registerWith(this, mFilter);
    }

    public void deregisterFrom(Broadcaster broadcaster) {
        broadcaster.deregisterFrom(this, mFilter);
        mBroadcasters.remove(broadcaster);
        mChannelSets.remove(broadcaster.id());
    }

    // IConfiguration
    @Override
    public UUID id() {
        return UUID.fromString("c0f2168e-b38b-4ade-aa43-61b5b1fc9778");
    }

    @Override
    public HashSet<UUID> allChannels() {
        HashSet<UUID> channels = new HashSet();
        for(UUID channelSetId : mChannelSets.keySet())
        {
            channels.addAll(mChannelSets.get(channelSetId));
        }
        return channels;
    }

    @Override
    public HashSet<UUID> orphanedChannels() {
        return allChannels();
    }
    
    @Override
    public long notificationInterval() { return 0; }

}
