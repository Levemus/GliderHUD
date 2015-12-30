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

import com.levemus.gliderhud.FlightData.Configuration.Configuration;
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

        for (UUID channel : (HashSet<UUID>) message.channels()) {
            if (mChannelToSourceId.get(channel) == null)
                mChannelToSourceId.put(channel, config.id());
        }

        HashSet<UUID> channelsToNotify = new HashSet<>();
        for (UUID channel : config.allChannels()) {
            if (mChannelToSourceId.get(channel).compareTo(config.id()) == 0) {
                channelsToNotify.add(channel);
            }
        }

        if (message.getType() == IMessage.Type.STATUS) {
            StatusMessage statusMsg = (StatusMessage) message;
            if (statusMsg != null) {
                for (UUID channel : (HashSet<UUID>) message.channels()) {
                    if (statusMsg.get(channel) == ChannelStatus.Status.OFFLINE)
                        mChannelToSourceId.put(channel, null);
                }
            }
        }

        if (!channelsToNotify.isEmpty()) {
            Configuration newConfig = new Configuration(config.id(), channelsToNotify, config.notificationInterval());
            mSubscriber.onMessage(newConfig, message);
        }
    }
}
