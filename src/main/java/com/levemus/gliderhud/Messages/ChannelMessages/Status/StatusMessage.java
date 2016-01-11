package com.levemus.gliderhud.Messages.ChannelMessages.Status;

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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.Messages.ChannelMessages.ChannelMessage;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class StatusMessage extends ChannelMessage<StatusOperation.Operation, ChannelStatus.Status> implements Serializable {

    public StatusMessage(UUID id, HashSet<UUID> channels, Long time, HashMap<UUID, ChannelStatus.Status> values) {
        super(StatusOperation.Operation.STATUS, id, channels, time, values);
    }

    public StatusMessage(UUID id, HashSet<UUID> channels, ChannelStatus.Status value) {
        super(StatusOperation.Operation.STATUS, id, channels, new Date().getTime(), new HashMap<UUID, ChannelStatus.Status>());
        for(UUID key : channels()) {
            mValues.put(key, value);
        }
    }

    @Override
    public String toString() {
        return "StatusMessage [mValues=" + mValues + "]";
    }
}
