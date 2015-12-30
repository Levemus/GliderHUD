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

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
/**
 * Created by mark@levemus on 15-12-13.
 */
class LXWP0Message extends BluetoothDataMessage {

    private final String TAG = this.getClass().getSimpleName();

    public LXWP0Message() {}

    @Override
    public String frameStart() {return "$LXWP0";}

    @Override
    protected String frameEnd() {return "\r\n";}

    @Override
    protected int elementCount() {return 12;}

    @Override
    protected String seperator() {return ",";}

    @Override
    protected int elementOffset(UUID channel) {
        if(channel == MessageChannels.VARIO)
            return 4;
        if (channel == MessageChannels.ALTITUDE)
            return 3;

        return -1;
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                MessageChannels.ALTITUDE,
                MessageChannels.VARIO));
    }
}
