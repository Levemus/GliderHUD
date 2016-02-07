package com.levemus.gliderhud.Messages.ChannelMessages.Data.Bluetooth;

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

import com.levemus.gliderhud.Messages.ChannelMessages.Channels;

/**
 * Created by mark@levemus on 15-12-13.
 */
public class XCTracerMessage extends BluetoothDataMessage {

    private final String TAG = this.getClass().getSimpleName();

    public XCTracerMessage() {}

    public XCTracerMessage(UUID id) {mId = id;}

    @Override
    public String frameStart() {return "$XCTRC";}

    @Override
    protected String frameEnd() {return "\r\n";}

    @Override
    protected int elementCount() {return 19;}

    @Override
    protected String seperator() {return ",";}

    @Override
    protected int elementOffset(UUID channel) {
        if(channel == Channels.VARIO)
            return 13;
        if (channel == Channels.ALTITUDE)
            return 10;
        if (channel == Channels.LONGITUDE)
            return 9;
        if (channel == Channels.LATITUDE)
            return 8;
        if(channel == Channels.BEARING)
            return 12;
        if (channel == Channels.GROUNDSPEED)
            return 11;

        return -1;
    }

    @Override
    public HashSet<UUID> keys() {
        return new HashSet(Arrays.asList(
                Channels.LATITUDE,
                Channels.LONGITUDE,
                Channels.ALTITUDE,
                Channels.VARIO,
                Channels.BEARING,
                Channels.GROUNDSPEED));
    }

    @Override
    public Double get(UUID channel) throws java.lang.UnsupportedOperationException
    {
        if(channel == Channels.GROUNDSPEED)
            return super.get(channel) * 3.6;
        return(super.get(channel));
    }
}

