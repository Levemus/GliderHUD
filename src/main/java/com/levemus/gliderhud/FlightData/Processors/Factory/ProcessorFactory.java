package com.levemus.gliderhud.FlightData.Processors.Factory;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.ProcessorBuilder;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Adjusters.SmoothAdjuster;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Adjusters.TimeRateAdjuster;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.DifferenceConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.DistanceFromConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.DivideConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IAdjuster;


/**
 * Created by mark@levemus on 15-12-27.
 */
public class ProcessorFactory {
    public static Processor build(UUID id) {
        if(id == ProcessorID.ALTITUDE)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.ALTITUDE)))
                    .id(id)
                    .build();

        if(id == ProcessorID.BEARING)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.BEARING)))
                    .id(id)
                    .period(1000L)
                    .build();

        if(id == ProcessorID.GROUNDSPEED)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.GROUNDSPEED)))
                    .id(id)
                    .build();

        if(id == ProcessorID.VARIO)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.VARIO)))
                    .id(id)
                    .adjusters(new ArrayList<IAdjuster>(Arrays.asList(new SmoothAdjuster(5))))
                    .build();

        if(id == ProcessorID.YAW)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.YAW)))
                    .id(id)
                    .build();

        if(id == ProcessorID.TURNRATE)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.BEARING)))
                    .id(id)
                    .adjusters(new ArrayList<>(Arrays.asList(
                            new TimeRateAdjuster(),
                            new SmoothAdjuster(5))))
                    .build();

        if(id == ProcessorID.DISTANCEFR)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.LONGITUDE, MessageChannels.LATITUDE)))
                    .converter(new DistanceFromConverter())
                    .build();

        if(id == ProcessorID.GLIDERATIO)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.VARIO, MessageChannels.GROUNDSPEED)))
                    .id(id)
                    .converter(new DivideConverter(MessageChannels.GROUNDSPEED, MessageChannels.VARIO))
                    .adjusters(new ArrayList<IAdjuster>(Arrays.asList(new SmoothAdjuster(5))))
                    .build();

        if(id == ProcessorID.HEIGHTABV)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(MessageChannels.ALTITUDE)))
                    .converter(new DifferenceConverter(MessageChannels.ALTITUDE))
                    .build();

        throw new java.lang.UnsupportedOperationException();
    }
}
