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

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattDescriptor;
import com.levemus.gliderhud.FlightData.IFlightData;

import java.util.HashSet;
import java.util.UUID;

import android.util.Log;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataType;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import android.annotation.SuppressLint;

/**
 * Created by mark@levemus on 15-12-08.
 */

@SuppressLint("NewApi")
public class BluetoothBroadcaster extends FlightDataBroadcaster
{
    private final String TAG = this.getClass().getSimpleName();

    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private String mAddress = "20:C3:8F:EB:04:9E"; // I only care about my vario - TODO: re-add a scanner

    Activity mActivity;

    @Override
    public void init(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void resume(Activity activity) {
        Log.d(TAG, "resume()");
        mActivity = activity;
        // Attempt BLE Connection
        mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null)
        {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
            {
                Log.e(TAG, "BT Adapter is null or not enabled!");
            }
        }
        else { Log.e(TAG, "Unable to retrieve BluetoothManager"); }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
        mBluetoothGatt = device.connectGatt(mActivity, false, mGattCallback);
    }

    @Override
    public void pause(Activity activity) {
        Log.d(TAG, "pause()");
        mActivity = activity;
        if (mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    @Override
    public HashSet<UUID> supportedTypes() {
        return new LXWP0FlightData().supportedTypes();
    }

    protected static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                Log.i(TAG, "Connected to GATT server.");
                if(mBluetoothGatt != null)
                    Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                Log.i(TAG, "Disconnected from GATT server.");
                if(mBluetoothGatt != null)
                    resume(mActivity);
            }
        }


        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                Log.i(TAG, "Services discovered.");
                for(BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {

                        int properties = characteristic.getProperties();
                        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            gatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor descriptor = characteristic
                                    .getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
            else { Log.w(TAG, "onServicesDiscovered received: " + status); }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Message dataMsg = new Message();
            dataMsg.obj = characteristic.getValue();
            messageHandler.sendMessage(dataMsg);
        }
    };


    BluetoothFlightData flightDataMsg = new LXWP0FlightData();
    private Handler messageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            try {
                String decoded = new String((byte[]) msg.obj, "UTF-8");
                if(flightDataMsg.build(decoded)) {
                    final BluetoothFlightData notifyMsg = flightDataMsg;
                    flightDataMsg = new LXWP0FlightData();
                    notifyListeners(notifyMsg);
                }
            }catch(Exception e){}
        }
    };
}


