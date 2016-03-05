package com.levemus.gliderhud.FlightData.Pipeline;

import android.util.Log;

import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.Messages.ChannelMessages.ChannelMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Status.ChannelStatus;
import com.levemus.gliderhud.Messages.ChannelMessages.Status.StatusMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by markcarter on 16-02-12.
 */
public class MessageMultiplexer implements MessageListener, MessageBroadcaster {

    private final String TAG = this.getClass().getSimpleName();

    private HashMap<UUID, UUID> mChannelToBroadcaster = new HashMap<>();
    private List<UUID> mProviderOrder = new ArrayList<>();
    private HashSet<MessageListener> mClients = new HashSet<>();

    public void add(Provider provider) {
        mProviderOrder.add(provider.id());
        for(UUID channel : provider.channels()) {
            if(!mChannelToBroadcaster.containsKey(channel)) {
                mChannelToBroadcaster.put(channel, provider.id());
                Log.i(TAG, "REGISTERED: Broadcaster: " + provider.id() + " Channel: " + channel);
            }
        }
    }

    public void remove(Provider provider) {
        mProviderOrder.remove(provider.id());
        for(UUID channel : provider.channels()) {
            if(mChannelToBroadcaster.containsKey(channel)) {
                mChannelToBroadcaster.remove(channel);
                Log.i(TAG, "DEREGISTERED: Broadcaster: " + provider.id() + " Channel: " + channel);
            }
        }
    }

    public void add(MessageListener client) {
        mClients.add(client);
    }

    public void remove(MessageListener client) {
        if(mClients.contains(client))
            mClients.remove(client);
    }

    @Override
    public void onMsg(final ChannelMessage msg) {
        if(msg instanceof StatusMessage) {
            HashSet<UUID> intersection = new HashSet<>(msg.keys());
            intersection.retainAll(mChannelToBroadcaster.keySet());
            for (UUID channel : intersection) {
                if (mChannelToBroadcaster.get(channel).compareTo(msg.id()) == 0) {
                    StatusMessage statusMsg = (StatusMessage) msg;
                    if (statusMsg.get(channel) == ChannelStatus.Status.OFFLINE) {
                        mChannelToBroadcaster.remove(channel);
                        Log.i(TAG, "OFFLINE: Broadcaster: " + msg.id() + " Channel: " + channel);
                    }
                }
            }
        } else if(msg instanceof DataMessage) {
            HashSet<UUID> exclusion = new HashSet<>(msg.keys());
            for(UUID channel : exclusion) {
                if(mChannelToBroadcaster.containsKey(channel)) {
                    if (mProviderOrder.indexOf(mChannelToBroadcaster.get(channel)) >
                            mProviderOrder.indexOf(msg.id())) {
                        mChannelToBroadcaster.remove(channel);
                    }
                }
            }
            exclusion.removeAll(mChannelToBroadcaster.keySet());
            for(UUID channel : exclusion) {
                mChannelToBroadcaster.put(channel, msg.id());
                Log.i(TAG, "ONLINE: Broadcaster: " + msg.id() + " Channel: " + channel);
            }

            DataMessage dataMsg = (DataMessage) msg;

            HashSet<MessageListener> clients = new HashSet<>();
            for(UUID channel : (HashSet<UUID>)msg.keys()) {
                if(mChannelToBroadcaster.get(channel).compareTo(msg.id()) == 0) {
                    for(MessageListener client : mClients) {
                        clients.add(client);
                    }
                }
            }

            for(MessageListener client : clients)
                client.onMsg(dataMsg);
        }
    }
}
