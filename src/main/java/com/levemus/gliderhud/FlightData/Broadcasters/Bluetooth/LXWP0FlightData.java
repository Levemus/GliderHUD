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
 * Created by mark@levemus on 15-12-13.
 */
class LXWP0FlightData implements IFlightData {

    private int FRAME_ELEMENT_COUNT = 12;

    private ArrayList<String> mBuffers = new ArrayList<String>();
    private static String mFrameStart = "$LXWP0";
    private static String mFrameEnd = "\r\n";

    public LXWP0FlightData() {}

    public boolean build(String buffer)
    {
        if(mBuffers.size() == 0 && !buffer.startsWith(mFrameStart)) {
            return false;
        }
        if(mBuffers.size() != 0 && buffer.startsWith(mFrameStart)) {
            return false;
        }
        mBuffers.add(new String(buffer));
        if(buffer.endsWith(mFrameEnd))
            return true;
        return false;
    }

    @Override
    public double get(FlightDataType type) throws java.lang.UnsupportedOperationException
    {
        StringBuilder builder = new StringBuilder();

        for (String string : mBuffers) {
            builder.append(string);
        }

        String[] tokenizedFrame = builder.toString().split(",");

        if(tokenizedFrame.length < FRAME_ELEMENT_COUNT)
            throw new java.lang.UnsupportedOperationException();

        try {
            if (type == FlightDataType.VARIORAW)
                return Double.parseDouble(tokenizedFrame[4]);

            if (type == FlightDataType.ALTITUDE)
                return Double.parseDouble(tokenizedFrame[3]);
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public EnumSet<FlightDataType> supportedTypes() {
        return EnumSet.of(
                IFlightData.FlightDataType.ALTITUDE,
                IFlightData.FlightDataType.VARIORAW);
    }
}

