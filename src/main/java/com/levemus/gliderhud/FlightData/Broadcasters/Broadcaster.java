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
import com.levemus.gliderhud.FlightData.Listeners.IListenerConfig;

/**
 * Created by mark@levemus on 15-11-29.
 */
public abstract class Broadcaster implements IBroadcaster, IRegisterListener {

    // IBroadcaster
    protected Activity mActivity;

    public void init(Activity activity) {
        mActivity = activity;
    };
    public void pause(Activity activity) {};
    public void resume(Activity activity) {};
    public abstract HashSet<UUID> supportedChannels();

    interface IListenerNotify<E>{
        void notify(HashSet<UUID> channels, E value);
    }

    interface IBroadcasterNotify<E>{
        void notify(IBroadcaster broadcaster, HashSet<UUID> channels, E value);
    }

    protected class ListenerManager <E>{

        // NotificationConfig
        private class ListenerConfig implements IListenerConfig {
            UUID mId;
            public UUID id() {return mId;}

            HashSet<UUID> mChannels = new HashSet<>();
            public HashSet<UUID> requiredChannels() {return mChannels;}

            long mNotificationInterval;
            public long notificationInterval() {return mNotificationInterval;}

            public ListenerConfig(IListenerConfig config){
                mId = config.id();
                mChannels = new HashSet<>(config.requiredChannels());
                mNotificationInterval = config.notificationInterval();
            }
        }

        // Notification Interval Container
        private class NotifyInterval {
            public IListenerNotify mListener;
            public IBroadcasterNotify mBroadcaster;
            public IListenerConfig mConfig;
            public long mTimeOfLastUpdate;

            public NotifyInterval(IListenerNotify notify, IListenerConfig config)
            {
                mListener = notify;
                mBroadcaster = null;
                mConfig = config;
                mTimeOfLastUpdate = 0;
            }

            public NotifyInterval(IBroadcasterNotify notify, IListenerConfig config)
            {
                mBroadcaster = notify;
                mListener = null;
                mConfig = config;
                mTimeOfLastUpdate = 0;
            }
        }

        private ArrayList<NotifyInterval> mListeners = new ArrayList<>();
        void add(IListenerConfig config, IListenerNotify listener)
        {
            HashSet<UUID> intersection = new HashSet<>(config.requiredChannels());
            intersection.retainAll(supportedChannels());
            if(!intersection.isEmpty()) {
                mListeners.add(new NotifyInterval(
                        listener,
                        new ListenerConfig(config)));
                config.requiredChannels().removeAll(intersection);
            }
        }

        void add(IListenerConfig config, IBroadcasterNotify listener)
        {
            HashSet<UUID> intersection = new HashSet<>(config.requiredChannels());
            intersection.retainAll(supportedChannels());
            if(!intersection.isEmpty()) {
                mListeners.add(new NotifyInterval(
                        listener,
                        new ListenerConfig(config)));
                config.requiredChannels().removeAll(intersection);
            }
        }

        public void notifyListeners(final IBroadcaster broadcaster, final HashSet<UUID> channels, final E value) {
            long currentTime = new Date().getTime();
            if(mListeners != null) {
                for (NotifyInterval listenerInterval : mListeners) {
                    long elapsed = currentTime - listenerInterval.mTimeOfLastUpdate;
                    HashSet<UUID> intersection = new HashSet(channels);
                    intersection.retainAll(listenerInterval.mConfig.requiredChannels());
                    intersection.retainAll(supportedChannels());
                    if (listenerInterval.mConfig.notificationInterval() < elapsed && !intersection.isEmpty()) {
                        // send msg
                        if(listenerInterval.mListener != null)
                            listenerInterval.mListener.notify(intersection, value);
                        else if(listenerInterval.mBroadcaster != null)
                            listenerInterval.mBroadcaster.notify(broadcaster, intersection, value);
                        listenerInterval.mTimeOfLastUpdate = currentTime;
                    }
                }
            }
        }
    }

    protected ListenerManager<IFlightData> mDataListeners = new ListenerManager<>();
    protected ListenerManager<BroadcasterStatus> mStatusListeners = new ListenerManager<>();

    @Override
    public void register(final IListenerConfig config, final com.levemus.gliderhud.FlightData.Listeners.IListenerNotify listener) {

        mDataListeners.add(config, new IListenerNotify<IFlightData>(){
            public void notify(HashSet<UUID> channels, IFlightData value)
            {
                listener.onData(channels, value);
            }
        });

        mStatusListeners.add(config, new IListenerNotify<BroadcasterStatus>(){
            public void notify(HashSet<UUID> channels, BroadcasterStatus value)
            {
                listener.onStatus(channels, value);
            }
        });
    }
}
