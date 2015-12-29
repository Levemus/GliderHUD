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

import com.levemus.gliderhud.FlightData.Listeners.Listener;
import com.levemus.gliderhud.FlightDisplay.IClient;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IAdjuster;

import java.util.UUID;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mark@levemus on 15-12-26.
 */
public class ListenerBuilder {
    private ListenerFrameConfig mConfig = new ListenerFrameConfig();

    public ListenerBuilder id(UUID id) {
        mConfig.mId = id;
        return this;
    }

    public ListenerBuilder channels(HashSet channels) {
        mConfig.mChannels = channels;
        return this;
    }

    public ListenerBuilder notificationInterval(int interval) {
        mConfig.mNotificationInterval = interval;
        return this;
    }

    public ListenerBuilder converter(IConverter op) {
        mConfig.mConverter = op;
        return this;
    }

    public ListenerBuilder adjusters(List<IAdjuster>ops) {
        mConfig.mAdjusters = ops;
        return this;
    }

    public ListenerBuilder client(IClient client) {
        mConfig.mClient = client;
        return this;
    }

    public Listener build() {
        // create and populate
        ListenerFrame result = new ListenerFrame();
        result.populate(mConfig);

        // reset defaults
        mConfig = new ListenerFrameConfig();

        return result;
    }
}


