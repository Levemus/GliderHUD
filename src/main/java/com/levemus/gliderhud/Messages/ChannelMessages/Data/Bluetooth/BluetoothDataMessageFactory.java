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

import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Configuration.ChannelConfiguration;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class BluetoothDataMessageFactory {

    BluetoothDataMessage[] mSupportedMessages = new BluetoothDataMessage[] {
            new LXWP0Message(),
            new XCTracerMessage()
    };

    private BluetoothDataMessage mCurrentMessage;

    public SerializablePayloadMessage build(ChannelConfiguration config, String buffer)  {
        for(BluetoothDataMessage msg: mSupportedMessages) {
            if(buffer.startsWith(msg.frameStart())) {
                Class<? extends BluetoothDataMessage> c = msg.getClass();
                try {
                    mCurrentMessage = c.getDeclaredConstructor(UUID.class).newInstance(config.id());
                    break;
                }catch(Exception e){}
            }
        }

        if(mCurrentMessage != null)
            return mCurrentMessage.build(buffer);

        return null;
    }

    public HashSet<UUID> supportedTypes() {
        if(mCurrentMessage != null)
            return mCurrentMessage.keys();

        HashSet<UUID> result = new HashSet<>();
        for(BluetoothDataMessage msg: mSupportedMessages) {
            result.addAll(msg.keys());
        }
        return result;
    }
}
