package com.levemus.gliderhud.Messages.ChannelMessages;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 16-01-05.
 */
public abstract class ChannelMessage<E, F> extends SerializablePayloadMessage<E, UUID, F> implements IIdentifiable, IChannelized {

    protected HashSet<UUID> mChannels;
    @Override
    public HashSet<UUID> channels() { return mChannels;}

    protected UUID mId;
    @Override
    public UUID id() {return mId;}

    protected Long mTime;
    public Long time() { return mTime; }

    public ChannelMessage(E opCode, UUID id, HashSet<UUID> channels, Long time, HashMap<UUID, F> values) {
        super(opCode, values);
        mChannels = channels;
        mTime = time;
        mId = id;
    }

    public ChannelMessage(E opCode, UUID id, HashMap<UUID, F> values) {
        super(opCode, values);
        mChannels = new HashSet<UUID>(values.keySet());
        mTime = new Date().getTime();
    }
}
