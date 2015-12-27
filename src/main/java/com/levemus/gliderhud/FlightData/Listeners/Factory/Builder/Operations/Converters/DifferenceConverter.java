package com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Converters;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IConverter;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by markcarter on 15-12-26.
 */
public class DifferenceConverter implements IConverter {

    private final double INVALID = Double.MIN_VALUE;
    private double mInitialValue = INVALID;
    private UUID mChannel;

    public DifferenceConverter(UUID channel) {
        mChannel = channel;
    }

    public DifferenceConverter(UUID channel, double initialValue) {
        mChannel = channel;
        mInitialValue = initialValue;
    }

    @Override
    public double convert(HashMap<UUID, Double> values) {
        if(mInitialValue == INVALID)
            mInitialValue = values.get(mChannel);
        return values.get(mChannel) - mInitialValue;
    }
}
