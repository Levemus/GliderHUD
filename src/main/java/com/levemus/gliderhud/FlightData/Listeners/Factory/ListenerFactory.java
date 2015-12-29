package com.levemus.gliderhud.FlightData.Listeners.Factory;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Listeners.Listener;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
import com.levemus.gliderhud.FlightDisplay.IClient;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.ListenerBuilder;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Adjusters.SmoothAdjuster;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Adjusters.TimeRateAdjuster;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Converters.DifferenceConverter;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Converters.DistanceFromConverter;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Converters.DivideConverter;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IAdjuster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-27.
 */
public class ListenerFactory {
    public static Listener build(UUID id, IClient client) {
        if(id == ListenerID.ALTITUDE)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.ALTITUDE)))
                    .id(id)
                    .client(client)
                    .build();

        if(id == ListenerID.BEARING)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.BEARING)))
                    .id(id)
                    .client(client)
                    .build();

        if(id == ListenerID.GROUNDSPEED)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.GROUNDSPEED)))
                    .id(id)
                    .client(client)
                    .build();

        if(id == ListenerID.VARIO)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.VARIO)))
                    .id(id)
                    .notificationInterval(100)
                    .adjusters(new ArrayList<IAdjuster>(Arrays.asList(new SmoothAdjuster(5))))
                    .client(client)
                    .build();

        if(id == ListenerID.YAW)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.YAW)))
                    .id(id)
                    .notificationInterval(30)
                    .client(client)
                    .build();

        if(id == ListenerID.TURNRATE)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.BEARING)))
                    .id(id)
                    .adjusters(new ArrayList<>(Arrays.asList(
                            new TimeRateAdjuster(),
                            new SmoothAdjuster(5))))
                    .client(client)
                    .build();

        if(id == ListenerID.DISTANCEFR)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.LONGITUDE, MessageChannels.LATITUDE)))
                    .converter(new DistanceFromConverter())
                    .client(client)
                    .build();

        if(id == ListenerID.GLIDERATIO)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.VARIO, MessageChannels.GROUNDSPEED)))
                    .id(id)
                    .converter(new DivideConverter(MessageChannels.GROUNDSPEED, MessageChannels.VARIO))
                    .adjusters(new ArrayList<IAdjuster>(Arrays.asList(new SmoothAdjuster(5))))
                    .client(client)
                    .build();

        if(id == ListenerID.HEIGHTABV)
            return new ListenerBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.ALTITUDE)))
                    .converter(new DifferenceConverter(MessageChannels.ALTITUDE))
                    .client(client)
                    .build();

        throw new java.lang.UnsupportedOperationException();
    }
}
