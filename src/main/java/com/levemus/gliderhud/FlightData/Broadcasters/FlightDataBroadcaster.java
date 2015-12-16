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
import java.util.HashSet;
import java.util.ArrayList;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;

/**
 * Created by mark@levemus on 15-11-29.
 */
public abstract class FlightDataBroadcaster implements IFlightDataBroadcaster {

    private class ListenerInterval {
        public IFlightDataListener mListener = null;
        HashSet<UUID> mSubscription = FlightDataType.ALL;
        public long mTimeOfLastUpdate = 0;
        public long mInterval = 0;

        public ListenerInterval(IFlightDataListener datalistener, long notificationInterval, HashSet<UUID> subscription)
        {
            mListener = datalistener;
            mInterval = notificationInterval;
            mSubscription = new HashSet(FlightDataType.ALL);
        }
    }

    private ArrayList<ListenerInterval> mListeners = new ArrayList<ListenerInterval>();

    protected void notifyListeners(IFlightData data)
    {
        HashSet<UUID> types = data.supportedTypes();
        long currentTime = new Date().getTime();
        if(mListeners != null) {
            for(ListenerInterval listenerInterval : mListeners) {
                long elapsed = currentTime - listenerInterval.mTimeOfLastUpdate;
                HashSet<UUID> intersection = new HashSet(types);
                intersection.retainAll(listenerInterval.mSubscription);
                if(listenerInterval.mInterval < elapsed && !intersection.isEmpty()) {
                    listenerInterval.mListener.onData(this, data);
                    listenerInterval.mTimeOfLastUpdate = currentTime;
                }
            }
        }
    }

    protected void notifyListeners(BroadcasterStatus status)
    {
        HashSet<UUID> types = status.affectedTypes();
        if(mListeners != null) {
            for(ListenerInterval listenerInterval : mListeners) {
                HashSet<UUID> intersection = new HashSet(types);
                intersection.retainAll(listenerInterval.mSubscription);
                if(!intersection.isEmpty()) {
                    listenerInterval.mListener.onStatus(this, status);
                }
            }
        }
    }

    public HashSet<UUID> addListener(IFlightDataListener listener, long notificationInterval, HashSet<UUID> subscription)
    {
        HashSet<UUID> intersection = new HashSet(subscription);
        intersection.retainAll(supportedTypes());
        if(!intersection.isEmpty())
            mListeners.add(new ListenerInterval(listener, notificationInterval, subscription));
        return intersection;
    }

    public abstract HashSet<UUID> supportedTypes();

    public void init(Activity activity) {};
    public void pause(Activity activity) {};
    public void resume(Activity activity) {};
}
