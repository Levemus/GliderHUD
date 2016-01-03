package com.levemus.gliderhud.FlightData.Messages.Data;

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
import java.io.Serializable;

import com.levemus.gliderhud.FlightData.Messages.Message;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class DataMessage extends Message<Double> implements Serializable {
    @Override
    public Type getType() { return Type.DATA; }
    public DataMessage() {}
    public DataMessage( HashMap<UUID, Double> values) {
        mValues = values;
    }

    @Override
    public String toString() {
        return "DataMessage [mValues=" + mValues.toString() + "]";
    }
}
