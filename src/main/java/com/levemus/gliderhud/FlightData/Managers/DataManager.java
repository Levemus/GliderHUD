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

import java.util.UUID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.util.Log;

import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Messages.Data.DataMessage;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Messages.Status.StatusMessage;
import com.levemus.gliderhud.FlightData.Messages.Status.ChannelStatus;

/**
 * Created by mark@levemus on 16-01-01.
 */
public class DataManager implements IDataManager, IChannelDataClient, IChannelDataProvider {
    private final String TAG = this.getClass().getSimpleName();

    private HashMap<UUID, UUID> mChannelToBroadcaster = new HashMap<>();

    public void registerProvider(IConfiguration config) {
        for(UUID channel : config.channels()) {
            if(!mChannelToBroadcaster.containsKey(channel)) {
                mChannelToBroadcaster.put(channel, config.id());
                Log.i(TAG, "REGISTERED: Broadcaster: " + config.id() + " Channel: " + channel);
            }
        }
    }

    public void deRegisterProvider(IConfiguration config) {
        for(UUID channel : config.channels()) {
            if(mChannelToBroadcaster.containsKey(channel)) {
                mChannelToBroadcaster.remove(channel);
                Log.i(TAG, "DEREGISTERED: Broadcaster: " + config.id() + " Channel: " + channel);
            }
        }
    }

    private HashMap<UUID, Double> mChannelToData = new HashMap<>();
    ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();

    @Override
    public void pushTo(IConfiguration config, Long time, IMessage msg) {

        if(msg.getType() == IMessage.Type.STATUS) {
            HashSet<UUID> intersection = new HashSet<>(msg.channels());
            intersection.retainAll(mChannelToBroadcaster.keySet());
            for (UUID channel : intersection) {
                if (mChannelToBroadcaster.get(channel).compareTo(config.id()) == 0) {
                    StatusMessage statusMsg = (StatusMessage) msg;
                    if (statusMsg.get(channel) == ChannelStatus.Status.OFFLINE) {
                        mChannelToBroadcaster.remove(channel); // TODO: place blocking lock here - we must RX this msg
                        Log.i(TAG, "OFFLINE: Broadcaster: " + config.id() + " Channel: " + channel);
                    }
                }
            }
        } else if(msg.getType() == IMessage.Type.DATA) {
            HashSet<UUID> exclusion = new HashSet<>(msg.channels());
            exclusion.removeAll(mChannelToBroadcaster.keySet());
            for(UUID channel : exclusion) {
                mChannelToBroadcaster.put(channel, config.id()); // TODO: place non blocking lock
                Log.i(TAG, "ONLINE: Broadcaster: " + config.id() + " Channel: " + channel);
            }

            DataMessage dataMsg = (DataMessage) msg;
            if (mLock.writeLock().tryLock()) {
                try {
                    for(UUID channel : (HashSet<UUID>)msg.channels()) {
                        if(mChannelToBroadcaster.get(channel).compareTo(config.id()) ==0) {
                            mChannelToData.put(channel, dataMsg.get(channel));
                        }
                    }
                } catch (Exception e) {
                } finally {
                    mLock.writeLock().unlock();
                }
            }
        }
    }

    @Override
    public HashMap<UUID, Double> pullFrom(IConfiguration config) {
        HashMap<UUID, Double> values = new HashMap<>();
        if (mLock.readLock().tryLock()) {
            try {
                for (UUID channel : config.channels()) {
                    values.put(channel, mChannelToData.get(channel));
                }
            } catch (Exception e) {
            } finally {
                mLock.readLock().unlock();
            }
        }
        return values;
    }
}
