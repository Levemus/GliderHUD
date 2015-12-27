package com.levemus.gliderhud.FlightData;

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
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class FlightData implements IFlightData{

    protected HashMap<UUID, Double> mValues;
    public FlightData(HashMap<UUID, Double> values) {
        mValues = values;
    }

    @Override
    public double get(UUID channel) throws java.lang.UnsupportedOperationException {
        try {
            if(mValues.containsKey(channel))
                return mValues.get(channel);
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public HashSet<UUID> supportedChannels() {
        return(new HashSet<UUID>(mValues.keySet()));
    }
}
