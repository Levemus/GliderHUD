package com.levemus.gliderhud.FlightData.Broadcasters.Components.Filters;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;
import com.levemus.gliderhud.FlightData.Messages.Status.ChannelStatus;
import com.levemus.gliderhud.FlightData.Messages.Status.StatusMessage;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-29.
 */
public class StatusFilter extends Filter {

    public StatusFilter(IConfiguration config, IMessageNotify subscriber ) {
        registerWith(config, subscriber);
    }

    @Override
    public void onMessage(IConfiguration config, IMessage message) {
        HashSet<UUID> channelsToSource = new HashSet<>();
        for(UUID channel : (HashSet<UUID>)message.channels()) {
            if(!mChannelToSourceId.containsKey(channel) ||
                    mChannelToSourceId.get(channel) == null) {
                mChannelToSourceId.put(channel, config.id());
            }

            if(mChannelToSourceId.get(channel).compareTo(config.id())== 0) {
                channelsToSource.add(channel);
            }
        }

        if(!channelsToSource.isEmpty()) {
            if(message.getType() == IMessage.Type.STATUS) {
                StatusMessage statusMsg = (StatusMessage)message;
                if(statusMsg != null) {
                    if(statusMsg.get(statusMsg.channels().iterator().next()) == ChannelStatus.Status.OFFLINE) {
                        for(UUID channel : channelsToSource) {
                            mChannelToSourceId.put(channel, null);
                        }
                    }
                }
            }

            // send the msg
            if(mSubscriber != null) {
                mSubscriber.onMessage(config, message);
            }
        }
    }
}
