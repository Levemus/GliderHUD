package com.levemus.gliderhud.FlightData.Broadcasters.Bluetooth.Message;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.

 */
import com.levemus.gliderhud.FlightData.IFlightData;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class BluetoothFlightDataFactory {

    BluetoothFlightData[] mSupportedMessages = new BluetoothFlightData[] {
            new LXWP0FlightData(),
            new XCTracerFlightData()
    };

    private BluetoothFlightData mCurrentMessage;

    public IFlightData build(String buffer)  {
        for(BluetoothFlightData msg: mSupportedMessages) {
            if(buffer.startsWith(msg.frameStart())) {
                Class<? extends BluetoothFlightData> c = msg.getClass();
                try {
                    mCurrentMessage = c.newInstance();
                    break;
                }catch(Exception e){}
            }
        }

        if(mCurrentMessage != null)
            return mCurrentMessage.build(buffer);

        return null;
    }

    public HashSet<UUID> supportedTypes() {
        HashSet<UUID> result = new HashSet<>();
        for(BluetoothFlightData msg: mSupportedMessages) {
            result.addAll(msg.supportedChannels());
        }
        return result;
    }
}
