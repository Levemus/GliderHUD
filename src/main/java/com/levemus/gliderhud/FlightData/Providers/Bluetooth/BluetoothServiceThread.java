package com.levemus.gliderhud.FlightData.Providers.Bluetooth;

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
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.preference.PreferenceManager;

import com.levemus.gliderhud.FlightData.Configuration.ChannelConfiguration;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.Bluetooth.BluetoothDataMessageFactory;
import com.levemus.gliderhud.Messages.ChannelMessages.Status.ChannelStatus;
import com.levemus.gliderhud.Messages.ChannelMessages.Status.StatusMessage;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;

/**
 * Created by mark@levemus on 15-12-30.
 */
public class BluetoothServiceThread extends ServiceProviderThread implements IChannelized, IIdentifiable {

    private final String TAG = this.getClass().getSimpleName();

    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private IChannelized mChannels = this;
    public BluetoothServiceThread(String id) {
        super(id);
    }
    private String mAddress = null;

    Handler mLocalHandler;

    @Override
    public void run() {
        Looper.prepare();
        mLocalHandler = new Handler();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = (Bundle)msg.obj;
                SerializablePayloadMessage message = (SerializablePayloadMessage) bundle.getSerializable("MSG");
                onRequest(message);
            }
        };

        try {
            if(mBluetoothGatt == null) {
                SharedPreferences sharedPref = mParent.getSharedPreferences(mParent.getString(com.levemus.gliderhud.R.string.full_app_name), Context.MODE_PRIVATE);
                mAddress = sharedPref.getString(mParent.getString(com.levemus.gliderhud.R.string.ble_address), mAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Looper.loop();
    }

    public void shutdown() {
        if (mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    private void connect() {
        Log.i(TAG, "BLE Connect: Address: " + mAddress);

        // Attempt BLE Connection
        mBluetoothManager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
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
        mBluetoothGatt = device.connectGatt(mParent, false, new BLECallback(mParent));
    }

    // Various callback methods defined by the BLE API.
    private class BLECallback extends BluetoothGattCallback {
        private Context mContext;

        public BLECallback( Context ctx) { mContext = ctx; }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(mParent.getString(com.levemus.gliderhud.R.string.ble_address), mAddress);
                editor.commit();

                Log.i(TAG, "Connected to GATT server.");
                if (mBluetoothGatt != null)
                    Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");

                sendResponse(new StatusMessage(id(), channels(),
                        ChannelStatus.Status.OFFLINE));

                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }
        }

        final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        @Override
        // New services discovered
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Services discovered.");
                for (BluetoothGattService service : gatt.getServices()) {
                    for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
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
            }else{
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        private final long MSG_ASSEMBLY_PEDIOD = 200;
        private final int MAX_MSGDATA_COUNT = 20;
        ArrayList<byte[]> mMsgData = new ArrayList<>();
        ReentrantLock mLock = new ReentrantLock();
        Runnable rxAssembly;

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (mLock.tryLock()) {
                try {
                    if(mMsgData.size() > MAX_MSGDATA_COUNT)
                        mMsgData.clear();
                    mMsgData.add(characteristic.getValue());
                } catch (Exception e) {}
                finally { mLock.unlock(); }
            }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(rxAssembly == null) {
               rxAssembly = new Runnable() {
                    public void run() {
                        SerializablePayloadMessage msgToSend = null;
                        if (mLock.tryLock()) {
                            try {
                                for (byte[] data : mMsgData) {
                                    SerializablePayloadMessage result = processMsgData(new String(data, "UTF-8"));
                                    msgToSend = (result != null ? result : msgToSend);
                                }
                            } catch (Exception e) {
                            } finally {
                                mLock.unlock();
                            }
                        }
                        if (msgToSend != null) {
                            sendResponse(msgToSend);
                            mMsgData.clear();
                        }
                        else
                            gatt.readCharacteristic(characteristic);

                        mLocalHandler.postDelayed(this, MSG_ASSEMBLY_PEDIOD);
                    }
                };
                mLocalHandler.postDelayed(rxAssembly, MSG_ASSEMBLY_PEDIOD);
                Log.i(TAG, "onCharacteristicChanged launching RX Assembly worker.");
            }

            if (mLock.tryLock()) {
                try {
                    if(mMsgData.size() > MAX_MSGDATA_COUNT)
                        mMsgData.clear();
                    mMsgData.add(characteristic.getValue());

                } catch (Exception e) {}
                finally { mLock.unlock(); }
            }
        }

        private SerializablePayloadMessage processMsgData(String data)
        {
            try {
                HashSet<UUID> channels = new HashSet<>(channels());
                SerializablePayloadMessage btMsg = mMsgFactory.build(
                        new ChannelConfiguration(id(), channels()), data);
                channels.removeAll(channels());
                if(btMsg != null){
                    return btMsg;
                }
                if(!channels.isEmpty()) {
                    sendResponse(new StatusMessage(id(), channels(), ChannelStatus.Status.OFFLINE));
                }
            }catch(Exception e){}
            return null;
        }
    };

    BluetoothDataMessageFactory mMsgFactory = new BluetoothDataMessageFactory();

    @Override
    public HashSet<UUID> channels() {
        return mMsgFactory.supportedTypes();
    }

    @Override
    public UUID id() {
        return UUID.fromString("c2dc9a7a-d8fe-44b7-a0ee-b7201f6e7aa5");
    }

    @Override
    public void setResponseHandler(Handler handler)
    {
        mResponseHandler = handler;
        if (mAddress != null && !mAddress.isEmpty())
            connect();
        else {
            sendResponse(new StatusMessage(id(), channels(), ChannelStatus.Status.OFFLINE));
        }
    }
}
