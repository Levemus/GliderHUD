package com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.HashMap;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IConverter;

/**
 * Created by mark@levemus on 15-12-26.
 */
public class SelectConverter implements IConverter {
    UUID mChannel;
    public SelectConverter(UUID channel) {
        mChannel = channel;
    }

    @Override
    public double convert(HashMap<UUID, Double> values) {
        return values.get(mChannel);
    }
}
