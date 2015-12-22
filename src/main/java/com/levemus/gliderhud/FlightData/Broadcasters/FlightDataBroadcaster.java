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
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;

/**
 * Created by mark@levemus on 15-11-29.
 */
public abstract class FlightDataBroadcaster implements IFlightDataBroadcaster {

    private class ListenerInterval {
        public IFlightDataListener mListener;
        HashSet<UUID> mSubscription;
        public long mInterval;
        public long mTimeOfLastUpdate = 0;

        public ListenerInterval(IFlightDataListener datalistener, long notificationInterval, HashSet<UUID> subscription)
        {
            mListener = datalistener;
            mInterval = notificationInterval;
            mSubscription = new HashSet<UUID>(subscription);
        }
    }

    private ArrayList<ListenerInterval> mListeners = new ArrayList<ListenerInterval>();

    protected HashMap<UUID, BroadcasterStatus.Status> mStatus = new HashMap<UUID, BroadcasterStatus.Status>();

    protected void notifyListenersOfData(final IFlightData data)
    {
        final IFlightDataBroadcaster broadcaster = this;
        mActivity.runOnUiThread(new Runnable()
        {
            public void run() {
                HashSet<UUID> types = data.supportedTypes();
                HashSet<UUID> updateStatus = new HashSet<>();

                // determine if just online
                for(UUID id : types) {
                    if(!mStatus.containsKey(id) ||
                            (mStatus.containsKey(id) &&
                            mStatus.get(id) == BroadcasterStatus.Status.OFFLINE)) {
                        updateStatus.add(id);
                        mStatus.put(id, BroadcasterStatus.Status.ONLINE);
                    }
                }
                long currentTime = new Date().getTime();
                if(mListeners != null) {
                    for (ListenerInterval listenerInterval : mListeners) {
                        long elapsed = currentTime - listenerInterval.mTimeOfLastUpdate;
                        HashSet<UUID> intersection = new HashSet(types);
                        intersection.retainAll(listenerInterval.mSubscription);
                        if (listenerInterval.mInterval < elapsed && !intersection.isEmpty()) {

                            // status update, if needed
                            HashSet<UUID> listenerStatus
                                    = new HashSet<>(updateStatus);
                            listenerStatus.retainAll(intersection);
                            if(!listenerStatus.isEmpty())
                                notifyListenersOfStatus(listenerStatus);

                            // send msg
                            listenerInterval.mListener.onData(broadcaster, data);
                            listenerInterval.mTimeOfLastUpdate = currentTime;
                        }
                    }
                }
            }
        });
    }

    protected void notifyListenersOfStatus(final HashSet<UUID> types)
    {
        final IFlightDataBroadcaster broadcaster = this;
        mActivity.runOnUiThread(new Runnable()
        {
            public void run() {
                if (mListeners != null) {
                    for (ListenerInterval listenerInterval : mListeners) {
                        HashSet<UUID> intersection = new HashSet(types);
                        intersection.retainAll(listenerInterval.mSubscription);
                        if (!intersection.isEmpty()) {
                            HashMap<UUID, BroadcasterStatus.Status> status
                                    = new HashMap<>();
                            for (UUID type : intersection)
                                status.put(type, mStatus.get(type));
                            listenerInterval.mListener.onStatus(broadcaster, status);
                        }
                    }
                }
            }
        });
    }

    public HashSet<UUID> addListener(IFlightDataListener listener, long notificationInterval, HashSet<UUID> subscription)
    {
        HashSet<UUID> intersection = new HashSet(subscription);
        intersection.retainAll(supportedTypes());
        if(!intersection.isEmpty()) {
            mListeners.add(new ListenerInterval(listener, notificationInterval, subscription));
        }
        return intersection;
    }

    public abstract HashSet<UUID> supportedTypes();

    protected Activity mActivity;

    public void init(Activity activity) {
        for(UUID key : supportedTypes()) {
            if (!mStatus.containsKey(key))
                mStatus.put(key, BroadcasterStatus.Status.OFFLINE);
        }
        mActivity = activity;
    };
    public void pause(Activity activity) {};
    public void resume(Activity activity) {};
}
