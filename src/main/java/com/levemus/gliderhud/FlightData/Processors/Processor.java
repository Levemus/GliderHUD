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

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Configuration.ChannelEntity;
import com.levemus.gliderhud.FlightData.Pipeline.MessageCache;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

/**
 * Created by mark@levemus on 15-12-26.
 */
public abstract class Processor<E> implements IProcessor<E>, ChannelEntity {

    protected MessageCache mCache = new MessageCache(false);

    // IProcessor
    @Override
    public void process() {}

    @Override
    public E onMsg(DataMessage msg) {
        HashSet<UUID> intersection = new HashSet(msg.channels());
        intersection.retainAll(channels());

        if(!intersection.isEmpty()) {
            mCache.onMsg(msg);
            long currentTime = new Date().getTime();
            if(currentTime - mTimeOfLastProcess > refreshPeriod()) {
                process();
                mTimeOfLastProcess = currentTime;
            }
        }

        return value();
    }

    @Override
    public boolean isValid(E value) {
        return (!value.equals(invalid()));
    }

    // ChannelEntity
    protected HashSet<UUID> mChannels = new HashSet();
    @Override
    public HashSet<UUID> channels() {
        return mChannels;
    }

    protected UUID mId;
    @Override
    public UUID id() { return mId; }

    // Value
    protected abstract E invalid();
    protected E mLastValue = invalid();
    protected E mValue = invalid();
    protected E value() {
        return mValue;
    }

    protected long mTimeOfLastProcess = 0;
    public long refreshPeriod() { return 500; }
}
