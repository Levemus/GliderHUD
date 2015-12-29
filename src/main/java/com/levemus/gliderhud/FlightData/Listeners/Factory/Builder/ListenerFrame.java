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

import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Converters.SelectConverter;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IAdjuster;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.FlightData.Listeners.Listener;
import com.levemus.gliderhud.FlightData.Messages.Data.DataMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-26.
 */
public class ListenerFrame extends Listener {
    private HashMap<UUID, Double> mValues = new HashMap<>();
    private IConverter mConverter = null;
    private List<IAdjuster> mAdjusters = null;

    protected void onData(IConfiguration config, DataMessage data) {
        try {
            HashSet<UUID> intersection = new HashSet<>(allChannels());
            intersection.retainAll(data.channels());
            for (UUID channel : intersection) {
                mValues.put(channel, data.get(channel));
            }

            if(mConverter == null) {
                mConverter = new SelectConverter(mValues.keySet().iterator().next());
            }

            double value = mConverter.convert(mValues);

            if(mAdjusters != null) {
                for(IAdjuster adjuster : mAdjusters) {
                    value = adjuster.adjust(value);
                }
            }

            mValue = value;
            mClient.onDataReady();
        } catch (Exception e){}
    }

    public void populate(ListenerFrameConfig config) {
        mChannels = config.mChannels;
        mOrphanedChannels = new HashSet<>(mChannels);
        mId = config.mId;
        mNotificationInterval = config.mNotificationInterval;
        mConverter = config.mConverter;
        mAdjusters = config.mAdjusters;
        mClient = config.mClient;
    }
}
