package com.levemus.gliderhud.FlightData.Listeners;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Messages.Data.DataMessage;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Messages.Status.ChannelStatus;
import com.levemus.gliderhud.FlightDisplay.IClient;
import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;
import com.levemus.gliderhud.FlightData.Messages.Status.StatusMessage;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-26.
 */
public abstract class Listener implements IMessageNotify, IConfiguration, IListener {

    // IListenerClient
    protected IClient mClient;
    @Override
    public void setClient(IClient client) {mClient = client;}

    // IConfiguration
    protected HashSet<UUID> mChannels = new HashSet();
    @Override
    public HashSet<UUID> allChannels() {
        return mChannels;
    }

    protected HashSet<UUID> mOrphanedChannels = new HashSet();
    @Override
    public HashSet<UUID> orphanedChannels() { return mOrphanedChannels; }

    protected UUID mId;
    @Override
    public UUID id() { return mId; }

    protected long mNotificationInterval;
    @Override
    public long notificationInterval() { return mNotificationInterval;}

    // IMessageNotify
    @Override
    public void onMessage(IConfiguration config, IMessage message) {
        if(message.getType() == IMessage.Type.DATA)
            onData(config, (DataMessage)message);
        else if(message.getType() == IMessage.Type.STATUS)
            onStatus(config, (StatusMessage)message);
    }

    protected abstract void onData(IConfiguration config, DataMessage data);

    protected void onStatus(IConfiguration config, StatusMessage data) {
        HashSet<UUID> intersection = new HashSet<>(data.channels());
        intersection.retainAll(mChannels);

        for (UUID channel : intersection) {
            if(data.get(channel) == ChannelStatus.Status.OFFLINE) {
                mValue = INVALID;
                mClient.onDataReady();
                return;
            }

        }
    }

    // Value
    protected Double INVALID = Double.MIN_VALUE;
    protected Double mValue = INVALID;
    public Double value() {return mValue;}
}
