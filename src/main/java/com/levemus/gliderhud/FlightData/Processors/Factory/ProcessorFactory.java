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

import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.BearingToConverter;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
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
                    .channels(new HashSet<>(Arrays.asList(Channels.ALTITUDE)))
                    .id(id)
                    .build();

        if(id == ProcessorID.BEARING)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.BEARING)))
                    .id(id)
                    .period(1000L)
                    .build();

        if(id == ProcessorID.GROUNDSPEED)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.GROUNDSPEED)))
                    .adjusters(new ArrayList<IAdjuster>(Arrays.asList(new SmoothAdjuster(10))))
                    .id(id)
                    .build();

        if(id == ProcessorID.VARIO)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.VARIO)))
                    .id(id)
                    .adjusters(new ArrayList<IAdjuster>(Arrays.asList(new SmoothAdjuster(10))))
                    .build();

        if(id == ProcessorID.YAW)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.YAW)))
                    .id(id)
                    .build();

        if(id == ProcessorID.TURNRATE)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.BEARING)))
                    .id(id)
                    .adjusters(new ArrayList<>(Arrays.asList(
                            new TimeRateAdjuster(360.0),
                            new SmoothAdjuster(16))))
                    .build();

        if(id == ProcessorID.DISTANCEFR)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.LONGITUDE, Channels.LATITUDE)))
                    .converter(new DistanceFromConverter())
                    .build();

        if(id == ProcessorID.BEARINGTO)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.LONGITUDE, Channels.LATITUDE)))
                    .converter(new BearingToConverter())
                    .build();

        if(id == ProcessorID.GLIDERATIO)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.VARIO, Channels.GROUNDSPEED)))
                    .id(id)
                    .converter(new DivideConverter(Channels.GROUNDSPEED, Channels.VARIO))
                    .adjusters(new ArrayList<IAdjuster>(Arrays.asList(new SmoothAdjuster(5))))
                    .build();

        if(id == ProcessorID.HEIGHTABV)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.ALTITUDE)))
                    .converter(new DifferenceConverter(Channels.ALTITUDE))
                    .build();

        if(id == ProcessorID.BATTERY)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.BATTERY)))
                    .id(id)
                    .build();

        if(id == ProcessorID.GPSALTITUDE)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.GPSALTITUDE)))
                    .id(id)
                    .build();

        if(id == ProcessorID.PRESSUREALTITUDE)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.PRESSUREALTITUDE)))
                    .id(id)
                    .build();

        if(id == ProcessorID.TIME)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.TIME)))
                    .id(id)
                    .build();

        if(id == ProcessorID.LATITUDE)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.LATITUDE)))
                    .id(id)
                    .build();

        if(id == ProcessorID.LONGITUDE)
            return new ProcessorBuilder()
                    .channels(new HashSet<>(Arrays.asList(Channels.LONGITUDE)))
                    .id(id)
                    .build();

        throw new java.lang.UnsupportedOperationException();
    }
}
