package com.levemus.gliderhud.FlightData.Broadcasters.Bluetooth;
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

import java.util.EnumSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-08.
 */

public interface IBluetoothDevice {

    class GattEvent {
        String mAddress;
        Type mType;

        public enum Type {
            DISCONNECTED, CONNECTED, DISCOVERED, DATA
        };

        public GattEvent(String address, Type type) {
            mAddress = address;
            mType = type;

        }
    };

    public void handleEvent(GattEvent event);
    public IFlightData postData(byte[] data);
    public UUID[] SupportedServices();
    public EnumSet<IFlightData.FlightDataType> SupportedTypes();
}
