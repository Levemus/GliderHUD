package com.levemus.gliderhud.FlightData.Broadcasters.Bluetooth.Message;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.

 Based upon demo source provided by Recon Instruments:
 https://github.com/ReconInstruments/sdk/tree/master/Samples/BluetoothLEDemo
 */

import com.levemus.gliderhud.FlightData.IFlightData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-15.
 */
public abstract class BluetoothFlightData implements IFlightData {

    private final String TAG = this.getClass().getSimpleName();

    protected ArrayList<String> mBuffers = new ArrayList<String>();

    public BluetoothFlightData build(String buffer)
    {
        if(mBuffers.size() == 0 && !buffer.startsWith(frameStart())) {
            return null;
        }
        if(mBuffers.size() != 0 && buffer.startsWith(frameStart())) {
            return null;
        }
        mBuffers.add(new String(buffer));
        if(buffer.endsWith(frameEnd()))
            return this;
        return null;
    }

    public abstract String frameStart();
    protected abstract String frameEnd();

    protected abstract int elementCount();
    protected abstract int elementOffset(UUID type);

    protected abstract String seperator();

    public double get(UUID type) throws java.lang.UnsupportedOperationException
    {
        StringBuilder builder = new StringBuilder();

        for (String string : mBuffers) {
            builder.append(string);
        }

        String[] tokenizedFrame = builder.toString().split(seperator());

        if(tokenizedFrame.length < elementCount())
            throw new java.lang.UnsupportedOperationException();

        int offset = elementOffset(type);
        if(offset>= 0) {
            try {
                return Double.parseDouble(tokenizedFrame[offset]);
            }
            catch(Exception e) {}
        }
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public abstract HashSet<UUID> supportedTypes();
}
