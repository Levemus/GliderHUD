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

import java.util.EnumSet;
import java.util.UUID;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.levemus.gliderhud.FlightData.IFlightData;

/**
 * Created by mark@levemus on 15-12-08.
 */
class XCTracer implements IBluetoothDevice {

    private final String TAG = this.getClass().getSimpleName();

    private class DataBuffer {
        String mData;
        public DataBuffer(String data) {
            mData = data;
        }
    }

    private int XCTRACER_FRAME_TOKEN_COUNT = 19;

    private ArrayList<DataBuffer> mBuffers = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public IFlightData postData(byte[] data) {
        IFlightData result = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(data);
            String dataString = outputStream.toString();
            if(dataString.startsWith("$XCTRC,")) {
                if(mBuffers.size() > 0) {
                    String frame = new String();
                    for (DataBuffer buffer : mBuffers) {
                        frame += buffer.mData;
                    }
                    String[] tokenizedFrame = frame.split(",");
                    int frameTokenCount = tokenizedFrame.length;
                    if (frameTokenCount >= XCTRACER_FRAME_TOKEN_COUNT) {
                        result = new XCTracerFlightData(
                                Double.parseDouble(tokenizedFrame[13]), // vario
                                Double.parseDouble(tokenizedFrame[10]), // altitude
                                Double.parseDouble(tokenizedFrame[11]), // groundspeed
                                Double.parseDouble(tokenizedFrame[12]) // bearing
                        );
                    }
                }
                mBuffers.clear();
                mBuffers.add(new DataBuffer(dataString));
            }
            else if(mBuffers.size() != 0) {
                mBuffers.add(new DataBuffer(dataString));
            }
        } catch(Exception e) {}

        return result;
    }

    public void handleEvent(GattEvent event) {
    }

    @Override
    public UUID[] SupportedServices() {
        return new UUID[]{
                UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"),
                UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"),
                UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"),
        };
    }

    @Override
    public EnumSet<IFlightData.FlightDataType> SupportedTypes() {
        return new XCTracerFlightData().supportedTypes();
    }
}

class XCTracerFlightData implements IFlightData {

    private double mVario = 0;
    private double mAltitude = 0;
    private double mGroundSpeed = 0;
    private double mBearing = 0;

    public XCTracerFlightData() {} // to get around lack of statics in interfaces while accessing supported types

    public XCTracerFlightData(double vario, double altitude, double groundSpeed, double bearing) {
        mVario = vario;
        mBearing = bearing;
        mAltitude = altitude;
        mGroundSpeed = groundSpeed;
    }

    @Override
    public double getData(FlightDataType type) throws java.lang.UnsupportedOperationException
    {
        try {
            if (type == FlightDataType.VARIO)
                return mVario;

            if (type == FlightDataType.ALTITUDE)
                return mAltitude;

            if (type == FlightDataType.GROUNDSPEED)
                return Math.round((mGroundSpeed * 3.6f) * 10) / 10;

            if (type == FlightDataType.BEARING)
                return mBearing;
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public EnumSet<FlightDataType> supportedTypes() {
        return EnumSet.of(
                IFlightData.FlightDataType.ALTITUDE,
                IFlightData.FlightDataType.GROUNDSPEED,
                IFlightData.FlightDataType.BEARING,
                IFlightData.FlightDataType.VARIO);
    }
}
