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

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Listeners.IListenerClient;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IAdjuster;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Converters.SelectConverter;
import com.levemus.gliderhud.FlightData.Listeners.IListenerClients;
import com.levemus.gliderhud.FlightData.Listeners.IListenerConfig;
import com.levemus.gliderhud.FlightData.Listeners.IListenerNotify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.List;

/**
 * Created by mark@levemus on 15-12-26.
 */
public class Listener implements IListenerNotify, IListenerConfig, IListenerClients {

    HashSet<IListenerClient> mClients = new HashSet<>();
    @Override
    public HashSet<IListenerClient> clients() {return mClients;}

    HashSet<UUID> mChannels = new HashSet();
    @Override
    public HashSet<UUID> requiredChannels() {
        return mChannels;
    }

    HashMap<UUID, Double> mValues = new HashMap<>();
    IConverter mConverter = null;
    List<IAdjuster> mAdjusters = null;

    @Override
    public void onData(HashSet<UUID> channels, IFlightData data) {
        try {
            for (UUID channel : channels) {
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

            for(IListenerClient client : mClients)
                client.onDataReady();
        } catch (Exception e){}
    }

    @Override
    public void onStatus(HashSet<UUID> channels, BroadcasterStatus status) {
        HashSet<UUID> intersection = new HashSet<>(channels);
        intersection.retainAll(mChannels);

        if(intersection.size() > 0 && status.value() == BroadcasterStatus.Status.OFFLINE) {
            mValue = INVALID;
            for(IListenerClient client : mClients)
                client.onDataReady();
        }
    }

    private double INVALID = Double.MIN_VALUE;
    private double mValue = INVALID;
    public double value() {return mValue;}

    UUID mId;
    @Override
    public UUID id() { return mId; }

    long mNotificationInterval;
    @Override
    public long notificationInterval() { return mNotificationInterval;}
}
