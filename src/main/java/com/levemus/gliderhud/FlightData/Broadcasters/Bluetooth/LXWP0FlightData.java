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
import java.util.HashMap;

/**
 * Created by mark@levemus on 15-12-13.
 */
class LXWP0FlightData extends BluetoothFlightData {

    private final String TAG = this.getClass().getSimpleName();

    public LXWP0FlightData() {}

    @Override
    protected String frameStart() {return "$LXWP0";}

    @Override
    protected String frameEnd() {return "\r\n";}

    @Override
    protected int elementCount() {return 12;}

    @Override
    protected int elementOffset(FlightDataType type) {
        if(type == FlightDataType.VARIORAW)
            return 4;
        if (type == FlightDataType.ALTITUDE)
            return 3;

        return -1;
    }

    @Override
    public EnumSet<FlightDataType> supportedTypes() {
        return EnumSet.of(
                FlightDataType.ALTITUDE,
                FlightDataType.VARIORAW);
    }
}

