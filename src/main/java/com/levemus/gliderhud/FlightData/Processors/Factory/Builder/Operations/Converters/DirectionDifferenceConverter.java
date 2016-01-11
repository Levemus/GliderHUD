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

import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.Utils.Angle;

/**
 * Created by mark@levemus on 15-12-26.
 */
public class DirectionDifferenceConverter implements IConverter {
    private final double INVALID = Double.MIN_VALUE;
    private double mInitialValue = INVALID;
    DirectionDifferenceConverter(){}
    DirectionDifferenceConverter(double initialValue) {
        mInitialValue = initialValue;
    }

    @Override
    public double convert(HashMap<UUID, Double> values) {
        if(mInitialValue == INVALID)
            mInitialValue = values.get(Channels.BEARING);
        return Angle.delta(values.get(Channels.BEARING), mInitialValue);
    }
}
