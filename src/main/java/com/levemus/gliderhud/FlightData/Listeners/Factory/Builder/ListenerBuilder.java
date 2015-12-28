package com.levemus.gliderhud.FlightData.Listeners.Factory.Builder;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Listeners.IListenerClient;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IAdjuster;

import java.util.UUID;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mark@levemus on 15-12-26.
 */
public class ListenerBuilder {
    private UUID mId = UUID.randomUUID();
    private HashSet<UUID> mChannels = new HashSet<>();
    private long mNotificationInterval = 500;
    private IConverter mConverter = null;
    private List<IAdjuster> mAdjusters = null;
    HashSet<IListenerClient> mClients = new HashSet<IListenerClient>();

    public ListenerBuilder id(UUID id) {
        mId = id;
        return this;
    }

    public ListenerBuilder channels(HashSet channels) {
        mChannels = channels;
        return this;
    }

    public ListenerBuilder notificationInterval(int interval) {
        mNotificationInterval = interval;
        return this;
    }

    public ListenerBuilder converter(IConverter op) {
        mConverter = op;
        return this;
    }

    public ListenerBuilder adjusters(List<IAdjuster>ops) {
        mAdjusters = ops;
        return this;
    }

    public ListenerBuilder client(IListenerClient client) {
        if(mClients == null)
            mClients = new HashSet<>();
        mClients.add(client);
        return this;
    }


    public Listener build() {
        // create and populate
        Listener result = new Listener();
        result.mChannels = mChannels;
        result.mId = mId;
        result.mNotificationInterval = mNotificationInterval;
        result.mConverter = mConverter;
        result.mAdjusters = mAdjusters;
        result.mClients = mClients;

        // reset defaults
        mChannels = new HashSet<>();
        mId = UUID.randomUUID();
        mNotificationInterval = 500;
        mConverter = null;
        mAdjusters = null;
        mClients = new HashSet<IListenerClient>();
        return result;
    }
}


