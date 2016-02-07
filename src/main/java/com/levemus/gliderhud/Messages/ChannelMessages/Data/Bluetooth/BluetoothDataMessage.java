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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Configuration.ChannelConfiguration;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

/**
 * Created by mark@levemus on 15-12-15.
 */
public abstract class BluetoothDataMessage extends DataMessage {

    private final String TAG = this.getClass().getSimpleName();

    protected ArrayList<String> mBuffers = new ArrayList<String>();

    public BluetoothDataMessage(UUID id, HashSet<UUID> channels, Long time, HashMap<UUID, Double> values) {
        super(id, channels, time, values);
    }

    public BluetoothDataMessage() {
        super(UUID.randomUUID(), new HashSet<UUID>(), new Date().getTime(), new HashMap<UUID, Double>());
    }

    public BluetoothDataMessage(UUID id) {
        super(id, new HashSet<UUID>(), new Date().getTime(), new HashMap<UUID, Double>());
    }

    public BluetoothDataMessage build(String buffer)
    {
        if(mBuffers.size() == 0 && !buffer.startsWith(frameStart())) {
            return null;
        }
        if(mBuffers.size() != 0 && buffer.startsWith(frameStart())) {
            return null;
        }
        String newBuffer = new String(buffer);
        mBuffers.add(newBuffer);
        if(buffer.endsWith(frameEnd())) {
            int test = 0;
            return this; // TODO: Checksum the result
        }
        return null;
    }

    public abstract String frameStart();
    protected abstract String frameEnd();

    protected abstract int elementCount();
    protected abstract int elementOffset(UUID channel);

    protected abstract String seperator();

    public Double get(UUID channel) throws java.lang.UnsupportedOperationException
    {
        StringBuilder builder = new StringBuilder();

        for (String string : mBuffers) {
            builder.append(string);
        }

        String[] tokenizedFrame = builder.toString().split(seperator());

        if(tokenizedFrame.length < elementCount())
            throw new java.lang.UnsupportedOperationException();

        int offset = elementOffset(channel);
        if(offset>= 0) {
            try {
                return Double.parseDouble(tokenizedFrame[offset]);
            }
            catch(Exception e) {}
        }
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public abstract HashSet<UUID> keys();

    @Override
    public String toString() {
        return "BluetoothDataMessage [mValues=" + mValues.toString() + " mBuffers= "+ mBuffers.toString() + "]";
    }
}
