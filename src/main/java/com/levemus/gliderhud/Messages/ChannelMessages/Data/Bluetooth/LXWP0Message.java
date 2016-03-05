package com.levemus.gliderhud.Messages.ChannelMessages.Data.Bluetooth;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.

 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.Messages.ChannelMessages.Channels;

/**
 * Created by mark@levemus on 15-12-13.
 */
public class LXWP0Message extends BluetoothDataMessage {

    private final String TAG = this.getClass().getSimpleName();

    public LXWP0Message() {}

    public LXWP0Message(UUID id) { mId = id; }

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
        if(channel == Channels.VARIO)
            return 4;
        if (channel == Channels.ALTITUDE)
            return 3;

        return -1;
    }

    @Override
    public HashSet<UUID> keys() {
        return new HashSet(Arrays.asList(
                Channels.ALTITUDE,
                Channels.VARIO));
    }
}

