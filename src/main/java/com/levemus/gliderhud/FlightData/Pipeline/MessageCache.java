package com.levemus.gliderhud.FlightData.Pipeline;

import com.levemus.gliderhud.FlightData.Configuration.ChannelEntity;
import com.levemus.gliderhud.Messages.ChannelMessages.ChannelMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by markcarter on 16-02-12.
 */
public class MessageCache implements MessageListener, MessageBroadcaster, ChannelEntity, ChannelDataSource {

    private HashMap<UUID, Double> mChannelToData = new HashMap<>();
    private ReentrantReadWriteLock mLock;

    public MessageCache(boolean lock) {
        if(lock)
            mLock = new ReentrantReadWriteLock();
    }

    @Override
    public void onMsg(final ChannelMessage msg) {
        DataMessage dataMsg = (DataMessage) msg;
        if (mLock == null || mLock.writeLock().tryLock()) {
            try {
                for(UUID channel : (HashSet<UUID>)msg.keys()) {
                    mChannelToData.put(channel, (Double)dataMsg.get(channel));
                }
            } catch (Exception e) {
            } finally {
                if(mLock != null)
                    mLock.writeLock().unlock();
            }
        }

        for(MessageListener client : mClients)
            client.onMsg(msg);
    }

    public HashMap<UUID, Double> data() {
        HashMap<UUID, Double> values = new HashMap<>();

        if (mLock == null || mLock.readLock().tryLock()) {
            try {
                for (UUID channel : mChannelToData.keySet()) {
                    values.put(channel, mChannelToData.get(channel));
                }
            } catch (Exception e) {
            } finally {
                if(mLock != null)
                    mLock.readLock().unlock();
            }
        }
        return values;
    }

    @Override
    public HashMap<UUID, Double> data(ChannelEntity config) {
        HashMap<UUID, Double> values = data();
        HashMap<UUID, Double> result = new HashMap<>();
        for(UUID channel : config.channels()) {
            if (mLock == null || mLock.readLock().tryLock()) {
                try {
                    if (values.containsKey(channel))
                        result.put(channel, values.get(channel));
                } catch (Exception e) {
                } finally {
                    if(mLock != null)
                        mLock.readLock().unlock();
                }
            }
        }
        return result;
    }

    HashSet<MessageListener> mClients = new HashSet<>();
    public void add(MessageListener client) {
        mClients.add(client);
    }
    public void remove(MessageListener client) {
        mClients.remove(client);
    }

    @Override
    public HashSet<UUID> channels() { return new HashSet<>(mChannelToData.keySet()); }

    private UUID mId = UUID.randomUUID();
    @Override
    public UUID id() { return mId; }
}
