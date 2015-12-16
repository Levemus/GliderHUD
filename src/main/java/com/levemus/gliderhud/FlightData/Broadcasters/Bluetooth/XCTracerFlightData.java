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
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.FlightDataType;

/**
 * Created by mark@levemus on 15-12-13.
 */
class XCTracerFlightData extends BluetoothFlightData {

    private final String TAG = this.getClass().getSimpleName();

    public XCTracerFlightData() {}

    @Override
    protected String frameStart() {return "$XCTRC";}

    @Override
    protected String frameEnd() {return "\r\n";}

    @Override
    protected int elementCount() {return 19;}

    @Override
    protected String seperator() {return ",";}

    @Override
    protected int elementOffset(UUID type) {
        if(type == FlightDataType.VARIORAW)
            return 13;
        if (type == FlightDataType.ALTITUDE)
            return 10;
        if(type == FlightDataType.BEARING)
            return 12;
        if (type == FlightDataType.GROUNDSPEED)
            return 11;

        return -1;
    }

    @Override
    public HashSet<UUID> supportedTypes() {
        return new HashSet(Arrays.asList(
                FlightDataType.ALTITUDE,
                FlightDataType.VARIORAW,
                FlightDataType.BEARING,
                FlightDataType.GROUNDSPEED));
    }
}

