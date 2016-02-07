package com.levemus.gliderhud.FlightData.Processors;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.HashSet;
import java.util.UUID;

import android.os.Handler;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Configuration.ChannelConfiguration;

/**
 * Created by mark@levemus on 15-12-26.
 */
public abstract class Processor<E>extends ChannelConfiguration implements IProcessor {

    // IProcessor
    protected IChannelDataSource mProvider;
    @Override
    public void registerSource(IChannelDataSource source) {mProvider = source;}
    @Override
    public void deRegisterSource(IChannelDataSource source) {mProvider = null;}

    Handler mHandler = new Handler();

    @Override
    public void start() {
        if(mProvider == null)
            throw new java.lang.UnsupportedOperationException();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                process();
                mHandler.postDelayed(this, refreshPeriod());
            }
        }, refreshPeriod());
    }

    @Override
    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public void process() {}

    @Override
    public long refreshPeriod() { return 500; }

    // IConfiguration
    protected HashSet<UUID> mChannels = new HashSet();
    @Override
    public HashSet<UUID> channels() {
        return mChannels;
    }

    protected UUID mId;
    @Override
    public UUID id() { return mId; }

    // Value
    public boolean isValid() {
        return (!mValue.equals(invalid()));
    }
    protected abstract E invalid();
    protected E mLastValue = invalid();
    protected abstract boolean hasChanged();
    protected E mValue = invalid();
    public E value() {return mValue;}
}
