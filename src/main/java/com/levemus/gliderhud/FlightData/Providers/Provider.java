package com.levemus.gliderhud.FlightData.Providers;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.levemus.gliderhud.FlightData.Configuration.ChannelEntity;
import com.levemus.gliderhud.FlightData.Pipeline.MessageBroadcaster;
import com.levemus.gliderhud.FlightData.Pipeline.MessageListener;
import com.levemus.gliderhud.Messages.ChannelMessages.ChannelMessage;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-11-29.
 */

public abstract class Provider implements ChannelEntity, MessageBroadcaster {

    public void start(Context ctx) {}

    public void stop(Context ctx) {}

    protected MessageListener mClient;

    @Override
    public void add(MessageListener client) {mClient = client;}

    @Override
    public void remove(MessageListener client) {mClient = null;}

    @Override
    public HashSet<UUID> channels() {
        return new HashSet<UUID>();
    }

    @Override
    public UUID id() {
        return UUID.randomUUID();
    }
}
