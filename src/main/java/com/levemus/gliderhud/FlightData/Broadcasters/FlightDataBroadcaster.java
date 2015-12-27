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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Listeners.IListenerStatus;
import com.levemus.gliderhud.FlightData.Listeners.IListenerConfig;
import com.levemus.gliderhud.FlightData.Listeners.IListenerData;

/**
 * Created by mark@levemus on 15-11-29.
 */
public abstract class FlightDataBroadcaster implements IFlightDataBroadcaster {

    // Data Notification
    protected class ListenerDataInterval {
        public IListenerData mListener;
        public long mInterval = 0;
        HashSet<UUID> mSubscription;
        public long mTimeOfLastUpdate = 0;

        public ListenerDataInterval(IListenerData datalistener, HashSet<UUID> subscription, long interval)
        {
            mListener = datalistener;
            mSubscription = new HashSet<UUID>(subscription);
            mInterval = interval;
        }
    }

    private ArrayList<ListenerDataInterval> mDataListeners = new ArrayList<>();

    public void registerForData(IListenerConfig config, IListenerData listener) {
        HashSet<UUID> intersection = new HashSet<>(config.requiredChannels());
        intersection.retainAll(supportedChannels());
        if(!intersection.isEmpty()) {
            mDataListeners.add(new ListenerDataInterval(listener, intersection, config.notificationInterval()));
        }

        config.requiredChannels().removeAll(intersection);
    }

    protected void notifyListenersOfData(final IFlightData data, final HashSet<UUID> channels)
    {
        long currentTime = new Date().getTime();
        if(mDataListeners != null) {
            for (ListenerDataInterval listenerInterval : mDataListeners) {
                long elapsed = currentTime - listenerInterval.mTimeOfLastUpdate;
                HashSet<UUID> intersection = new HashSet(channels);
                intersection.retainAll(listenerInterval.mSubscription);
                if (listenerInterval.mInterval < elapsed && !intersection.isEmpty()) {

                    // send msg
                    listenerInterval.mListener.onData(intersection, data);
                    listenerInterval.mTimeOfLastUpdate = currentTime;
                }
            }
        }
    }


    public abstract HashSet<UUID> supportedChannels();

    protected Activity mActivity;

    public void init(Activity activity) {
        mActivity = activity;
    };

    public void pause(Activity activity) {};
    public void resume(Activity activity) {};
}
