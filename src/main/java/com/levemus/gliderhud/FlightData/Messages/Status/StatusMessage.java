package com.levemus.gliderhud.FlightData.Messages.Status;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.HashSet;

import com.levemus.gliderhud.FlightData.Messages.Message;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class StatusMessage extends Message<ChannelStatus.Status> implements Serializable {

    public StatusMessage() {}
    public StatusMessage( HashMap<UUID, ChannelStatus.Status> values) {
        mValues = values;
    }
    public StatusMessage( HashSet<UUID> channels,  ChannelStatus.Status status) {
        mValues = new HashMap<UUID, ChannelStatus.Status>();
        for(UUID channel : channels) {
            mValues.put(channel, status);
        }
    }

    @Override
    public Type getType() { return Type.STATUS; }

    @Override
    public String toString() {
        return "StatusMessage [mValues=" + mValues + "]";
    }
}
