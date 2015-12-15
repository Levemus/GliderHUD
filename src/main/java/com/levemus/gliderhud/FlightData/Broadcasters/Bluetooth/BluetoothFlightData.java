package com.levemus.gliderhud.FlightData.Broadcasters.Bluetooth;

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
import java.util.EnumSet;

/**
 * Created by mark@levemus on 15-12-15.
 */
public abstract class BluetoothFlightData implements IFlightData {

    private final String TAG = this.getClass().getSimpleName();

    protected ArrayList<String> mBuffers = new ArrayList<String>();

    public boolean build(String buffer)
    {
        if(mBuffers.size() == 0 && !buffer.startsWith(frameStart())) {
            return false;
        }
        if(mBuffers.size() != 0 && buffer.startsWith(frameStart())) {
            return false;
        }
        mBuffers.add(new String(buffer));
        if(buffer.endsWith(frameEnd()))
            return true;
        return false;
    }

    protected String frameStart() {return "$START";}
    protected String frameEnd() {return "\r\n";}

    protected int elementCount() {return 1;}
    protected int elementOffset(FlightDataType type) {return -1;}

    protected String seperator() {return ",";}

    @Override
    public double get(FlightDataType type) throws java.lang.UnsupportedOperationException
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
    public abstract EnumSet<FlightDataType> supportedTypes();
}
