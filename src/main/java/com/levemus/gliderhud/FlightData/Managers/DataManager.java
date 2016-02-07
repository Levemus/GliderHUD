package com.levemus.gliderhud.FlightData.Managers;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.os.AsyncTask;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Configuration.ChannelConfiguration;
import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.Messages.ChannelMessages.ChannelMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Status.StatusMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Status.ChannelStatus;
import com.levemus.gliderhud.Messages.IMessage;
import com.levemus.gliderhud.Messages.ServiceMessages.ServiceEventMessage;

/**
 * Created by mark@levemus on 16-01-01.
 */
public class DataManager implements IDataManager, IClient, IChannelDataSource {
    private final String TAG = this.getClass().getSimpleName();

    private HashMap<UUID, UUID> mChannelToBroadcaster = new HashMap<>();
    private HashMap<UUID, ReentrantReadWriteLock> mChannelToLock = new HashMap();
    private List<UUID> mProviderOrder = new ArrayList<>();

    public void registerProvider(IIdentifiable id, IChannelized channels) {
        mProviderOrder.add(id.id());
        for(UUID channel : channels.channels()) {
            if(!mChannelToBroadcaster.containsKey(channel)) {
                mChannelToBroadcaster.put(channel, id.id());
                mChannelToLock.put(channel, new ReentrantReadWriteLock());
                Log.i(TAG, "REGISTERED: Broadcaster: " + id.id() + " Channel: " + channel);
            }
        }
    }

    public void deRegisterProvider(IIdentifiable id, IChannelized channels) {
        mProviderOrder.remove(id.id());
        for(UUID channel : channels.channels()) {
            if(mChannelToBroadcaster.containsKey(channel)) {
                mChannelToBroadcaster.remove(channel);
                mChannelToLock.remove(channel);
                Log.i(TAG, "DEREGISTERED: Broadcaster: " + id.id() + " Channel: " + channel);
            }
        }
    }

    private HashMap<UUID, Double> mChannelToData = new HashMap<>();

    @Override
    public void onMsg(final IMessage msg) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... arg0) {
                if(msg instanceof ChannelMessage)
                    _pushTo((ChannelMessage) msg);
                else if(msg instanceof ServiceEventMessage)
                    Log.i(TAG, "Service Event: " + ((ServiceEventMessage) msg).id() +  " Event: "+ msg.opCode());
                return null;
            }
        };
        task.execute((Void[]) null);
    }

    private void _pushTo(ChannelMessage msg) {

        if(msg instanceof StatusMessage) {
            HashSet<UUID> intersection = new HashSet<>(msg.keys());
            intersection.retainAll(mChannelToBroadcaster.keySet());
            for (UUID channel : intersection) {
                if (mChannelToBroadcaster.get(channel).compareTo(msg.id()) == 0) {
                    StatusMessage statusMsg = (StatusMessage) msg;
                    if (statusMsg.get(channel) == ChannelStatus.Status.OFFLINE) {
                        mChannelToBroadcaster.remove(channel); // TODO: place blocking lock here - we must RX this msg
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
                mChannelToBroadcaster.put(channel, msg.id()); // TODO: place non blocking lock
                Log.i(TAG, "ONLINE: Broadcaster: " + msg.id() + " Channel: " + channel);
            }

            DataMessage dataMsg = (DataMessage) msg;

            for(UUID channel : (HashSet<UUID>)msg.keys()) {
                if (mChannelToLock.get(channel).writeLock().tryLock()) {
                    try {
                        if(mChannelToBroadcaster.get(channel).compareTo(msg.id()) ==0) {
                            mChannelToData.put(channel, (Double)dataMsg.get(channel));
                        }
                    } catch (Exception e) {
                    } finally {
                        mChannelToLock.get(channel).writeLock().unlock();
                    }
                }
            }
        }
    }

    @Override
    public HashMap<UUID, Double> get(ChannelConfiguration config) {
        HashMap<UUID, Double> values = new HashMap<>();
        for (UUID channel : config.channels()) {
            mChannelToLock.get(channel).readLock().lock();
                try {
                    values.put(channel, mChannelToData.get(channel));
                } catch (Exception e) {
                } finally {
                    mChannelToLock.get(channel).readLock().unlock();
                }
            }

        return values;
    }
}
