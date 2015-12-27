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

import java.util.UUID;
import java.util.HashMap;

/**
 * Created by markcarter on 15-12-26.
 */
public class DivideConverter implements IConverter {

    UUID mNumerator;
    UUID mDenominator;

    public DivideConverter(UUID numerator, UUID denominator) {
        mNumerator = numerator;
        mDenominator = denominator;
    }

    @Override
    public double convert(HashMap<UUID, Double> values) {
        return values.get(mNumerator) / values.get(mDenominator);
    }
}
