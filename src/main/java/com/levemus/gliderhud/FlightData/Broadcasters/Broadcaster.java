package com.levemus.gliderhud.FlightData.Broadcasters;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.app.Activity;

import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Broadcasters.Components.Dispatchers.Dispatcher;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Messages.Status.ChannelStatus;
import com.levemus.gliderhud.FlightData.Messages.Status.StatusMessage;

/**
 * Created by mark@levemus on 15-11-29.
 */
public abstract class Broadcaster implements IBroadcaster, IConfiguration  {

    // Dispatcher
    protected Dispatcher mDispatcher = new Dispatcher();

    // IBroadcaster
    protected Activity mActivity;

    public void init(Activity activity) {
        mActivity = activity;
    };
    public void pause(Activity activity) {};
    public void resume(Activity activity) {};

    public void registerWith(IConfiguration config, IMessageNotify subscriber ) {
        mDispatcher.registerWith(config, subscriber);
        notifyListeners(new StatusMessage(allChannels(), ChannelStatus.Status.ONLINE));
    }

    public void deregisterFrom(IConfiguration config, IMessageNotify subscriber ) {
        notifyListeners(new StatusMessage(allChannels(), ChannelStatus.Status.OFFLINE));
        mDispatcher.deregisterFrom(config, subscriber);
    }

    protected void notifyListeners(IMessage msg)
    {
        mDispatcher.postMessage(this, msg);
    }

    // IConfiguration
    public abstract UUID id();
    public abstract HashSet<UUID> allChannels();
    public HashSet<UUID> orphanedChannels() { return new HashSet<>(); }
    public long notificationInterval() { return Integer.MAX_VALUE; }
}
