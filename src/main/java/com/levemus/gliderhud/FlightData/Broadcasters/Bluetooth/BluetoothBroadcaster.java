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
import com.levemus.gliderhud.FlightData.IFlightData;
import android.os.Message;
import android.os.Handler;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;

import android.annotation.SuppressLint;

/**
 * Created by mark@levemus on 15-12-08.
 */
@SuppressLint("NewApi")
public class BluetoothBroadcaster extends FlightDataBroadcaster {

    private final String TAG = this.getClass().getSimpleName();

    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothScanner mScanner;
    Activity mActivity = null;

    IBluetoothDevice[] mSupportedDevices = new IBluetoothDevice[] {
            new XCTracer() // quick and dirty - only care about my vario
    };

    @Override
    public EnumSet<IFlightData.FlightDataType> supportedTypes() {
        return new XCTracer().SupportedTypes(); // TODO: union FlightData Types associated with mSupportedDevices
    }

    HashMap<String, IBluetoothDevice> mConnectedDevices = new HashMap<>();

    @Override
    public void init(Activity activity) {
        mActivity = activity;
        mScanner = new BluetoothScanner(activity);
    }

    @Override
    public void pause() {
        if (mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        if(mBluetoothAdapter != null){ mScanner.StopScan(); }
    }

    @Override
    public void resume() {
        mScanner.StartScan(mDeviceScanResult);
    }

    private Handler mDeviceScanResult = new Handler()
    {
        public void handleMessage(Message msg)
        {
            BluetoothDevice device = (BluetoothDevice) msg.obj;
            if(device != null && device.getAddress() != null) {
                if (!mConnectedDevices.containsKey(device.getAddress())) {
                    mConnectedDevices.put(device.getAddress(), null);
                    Log.i(TAG, "Address: " + device.getAddress());
                    connect(device.getAddress());
                }
            }
            super.handleMessage(msg);
        }
    };

    private void connect(String address)
    {
        mBluetoothManager = (BluetoothManager) mActivity.getSystemService(mActivity.getApplicationContext().BLUETOOTH_SERVICE);
        if (mBluetoothManager != null)
        {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
            {
                Log.e(TAG, "BT Adapter is null or not enabled!");
            }
        }
        else { Log.e(TAG, "Unable to retrieve BluetoothManager"); }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothGatt = device.connectGatt(mActivity, false, mGattCallback);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                Log.i(TAG, "Connected to GATT server.");
                if(gatt.getDevice().getAddress() != "") {
                    notifyGattEventHandler(new IBluetoothDevice.GattEvent(gatt.getDevice().getAddress(),
                            IBluetoothDevice.GattEvent.Type.CONNECTED));
                }
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                Log.i(TAG, "Disconnected from GATT server.");
                if(gatt.getDevice().getAddress() != "") {
                    notifyGattEventHandler(new IBluetoothDevice.GattEvent(gatt.getDevice().getAddress(),
                            IBluetoothDevice.GattEvent.Type.DISCONNECTED));
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                Log.i(TAG, "Services discovered.");
                boolean found = false;
                List<BluetoothGattService>	services = gatt.getServices();
                String address = gatt.getDevice().getAddress();
                for(BluetoothGattService service : services)
                {
                    UUID serviceID = service.getUuid();
                    for (IBluetoothDevice listener : mSupportedDevices) {
                        for (UUID uuid : listener.SupportedServices()) {
                            if (serviceID.compareTo(uuid) == 0) {
                                if(mConnectedDevices.get(address) == null) {
                                    mConnectedDevices.put(address, listener);
                                    notifyGattEventHandler(new IBluetoothDevice.GattEvent(gatt.getDevice().getAddress(),
                                            IBluetoothDevice.GattEvent.Type.CONNECTED));
                                }
                                notifyGattEventHandler(new IBluetoothDevice.GattEvent(address, IBluetoothDevice.GattEvent.Type.DISCOVERED));
                                List<BluetoothGattCharacteristic> characterics = service.getCharacteristics();
                                for (BluetoothGattCharacteristic characteristic : characterics) {
                                    int properties = characteristic.getProperties();
                                    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                        gatt.setCharacteristicNotification(characteristic, true);
                                    }
                                }
                                found = true;
                            }
                        }
                    }
                }
                if(found == false) {
                    gatt.disconnect();
                    mConnectedDevices.remove(address);
                }
                else {
                    mScanner.StopScan();
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            IBluetoothDevice listener = mConnectedDevices.get(gatt.getDevice().getAddress());

            if(listener != null) {
                final IFlightData flightData = listener.postData(characteristic.getValue());
                if(flightData != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyListeners(flightData);
                        }
                    });
                }
            }
        }
    };

    private void notifyGattEventHandler(final IBluetoothDevice.GattEvent event) {
        if (event.mAddress != null) {
            if (mConnectedDevices.containsKey(event.mAddress)) {
                IBluetoothDevice listener = mConnectedDevices.get(event.mAddress);

                if (listener != null) {
                    listener.handleEvent(event);
                }
            }
        }
    }

    private class BluetoothScanner
    {
        private Handler mScanStopHandler = new Handler();
        private static final long SCAN_PERIOD = 60000;

        private Handler mScanResultDeviceHandler = null;
        private BluetoothManager mBluetoothManager;
        private BluetoothAdapter mBluetoothAdapter;

        private Activity mActivity;

        public BluetoothScanner(Activity activity)
        {
            mActivity = activity;
            mBluetoothManager = (BluetoothManager) mActivity.getSystemService(mActivity.getApplicationContext().BLUETOOTH_SERVICE);
            if (mBluetoothManager != null) {
                mBluetoothAdapter = mBluetoothManager.getAdapter();
            }
        }

        private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
        {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
            {
                    if(mScanResultDeviceHandler != null) {
                        Message strMsg = new Message();
                        strMsg.obj = device;
                        mScanResultDeviceHandler.sendMessage(strMsg);
                    }
                }
        };

        public void StopScan() {
            Log.d(TAG, "stopLeScan()");
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        public void StartScan(Handler scanResultHandler)
        {
            if(mBluetoothAdapter != null) {
                mScanResultDeviceHandler = scanResultHandler;
                boolean scanStarted = mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.d(TAG, "startLeScan(), scanStarted: " + scanStarted);

                if (!scanStarted) {
                    Log.d(TAG, "Scan FAILED to Start.");
                    return;
                }

                mScanStopHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StopScan();
                    }
                }, SCAN_PERIOD);
            }
        }
    }
}
