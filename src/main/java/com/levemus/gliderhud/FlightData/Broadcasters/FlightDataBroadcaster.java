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
import java.util.EnumSet;
import java.util.ArrayList;

import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;

/**
 * Created by mark@levemus on 15-11-29.
 */
public abstract class FlightDataBroadcaster implements IFlightDataBroadcaster {

    private class ListenerInterval {
        public IFlightDataListener mListener = null;
        EnumSet<IFlightData.FlightDataType> mSubscription = IFlightData.FlightDataType.ALL_OPTS;
        public long mTimeOfLastUpdate = 0;
        public long mInterval = 0;

        public ListenerInterval(IFlightDataListener datalistener, long notificationInterval, EnumSet<IFlightData.FlightDataType> subscription)
        {
            mListener = datalistener;
            mInterval = notificationInterval;
            mSubscription = EnumSet.copyOf(subscription);
        }
    }

    private ArrayList<ListenerInterval> mListeners = new ArrayList<ListenerInterval>();

    protected void NotifyListeners(IFlightData data, EnumSet<IFlightData.FlightDataType> types)
    {
        long currentTime = new Date().getTime();
        if(mListeners != null) {
            for(ListenerInterval listenerInterval : mListeners) {
                long elapsed = currentTime - listenerInterval.mTimeOfLastUpdate;
                EnumSet<IFlightData.FlightDataType> intersection = EnumSet.copyOf(types);
                intersection.retainAll(listenerInterval.mSubscription);
                if(listenerInterval.mInterval < elapsed && !intersection.isEmpty()) {
                    listenerInterval.mListener.onData(data);
                    listenerInterval.mTimeOfLastUpdate = currentTime;
                }
            }
        }
    }

    public EnumSet<IFlightData.FlightDataType> AddListener(IFlightDataListener listener, long notificationInterval, EnumSet<IFlightData.FlightDataType> subscription)
    {
        EnumSet<IFlightData.FlightDataType> intersection = EnumSet.copyOf(subscription);
        intersection.retainAll(getSupportedTypes());
        if(!intersection.isEmpty())
            mListeners.add(new ListenerInterval(listener, notificationInterval, subscription));
        return intersection;
    }

    public EnumSet<IFlightData.FlightDataType> getSupportedTypes()
    {
        return EnumSet.noneOf(IFlightData.FlightDataType.class);
    }

    public void init(Activity activity) {};
    public void pause() {};
    public void resume() {};
}
