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
import java.util.Date;

import android.app.Activity;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.preference.PreferenceManager;

import com.levemus.gliderhud.FlightData.Configuration.Configuration;
import com.levemus.gliderhud.FlightData.Messages.Data.Bluetooth.BluetoothDataMessageFactory;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Providers.ServiceProvider;
import com.levemus.gliderhud.FlightData.Messages.Status.ChannelStatus;
import com.levemus.gliderhud.FlightData.Messages.Status.StatusMessage;

/**
 * Created by mark@levemus on 15-12-30.
 */
public class BluetoothService extends ServiceProvider {

    private final String TAG = this.getClass().getSimpleName();

    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private String mAddress = null;

    public int _onStartCommand(Intent intent, int flags, int startId) {
        if(mBluetoothGatt == null) {
            SharedPreferences sharedPref = getSharedPreferences(getString(com.levemus.gliderhud.R.string.full_app_name), Context.MODE_PRIVATE);
            mAddress = sharedPref.getString(getString(com.levemus.gliderhud.R.string.ble_address), mAddress);
            mId = UUID.fromString(intent.getStringExtra(getString(com.levemus.gliderhud.R.string.service_id)));
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    private void connect() {
        Log.i(TAG, "BLE Connect: Address: " + mAddress);

        // Attempt BLE Connection
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
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
        mBluetoothGatt = device.connectGatt(this, false, new BLECallback(this));
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
                editor.putString(getString(com.levemus.gliderhud.R.string.ble_address), mAddress);
                editor.commit();

                Log.i(TAG, "Connected to GATT server.");
                if (mBluetoothGatt != null)
                    Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");

                sendMsg(new Configuration(id(), channels()),
                        new Date().getTime(),
                        new StatusMessage(channels(), ChannelStatus.Status.OFFLINE));

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
                        IMessage msgToSend = null;
                        if (mLock.tryLock()) {
                            try {
                                for (byte[] data : mMsgData) {
                                    IMessage result = processMsgData(new String(data, "UTF-8"));
                                    msgToSend = (result != null ? result : msgToSend);
                                }
                            } catch (Exception e) {
                            } finally {
                                mLock.unlock();
                            }
                        }
                        if (msgToSend != null) {
                            sendMsg(new Configuration(id(), channels()),
                                    new Date().getTime(),
                                    msgToSend);
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

        private IMessage processMsgData(String data)
        {
            try {
                HashSet<UUID> channels = new HashSet<>(channels());
                IMessage btMsg = mMsgFactory.build(data);
                channels.removeAll(channels());
                if(btMsg != null){
                    return btMsg;
                }
                if(!channels.isEmpty()) {
                    sendMsg(new Configuration(id(), channels()),
                            new Date().getTime(),
                            new StatusMessage(channels, ChannelStatus.Status.OFFLINE));
                }
            }catch(Exception e){}
            return null;
        }
    };

    BluetoothDataMessageFactory mMsgFactory = new BluetoothDataMessageFactory();
    private HashSet<UUID> channels() {
        return mMsgFactory.supportedTypes();
    }

    @Override
    public void start(Activity activity, Class service, UUID id) {
        Log.d(TAG, "resume()");

        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(com.levemus.gliderhud.R.string.full_app_name), Context.MODE_PRIVATE);
        mAddress = sharedPref.getString(activity.getString(com.levemus.gliderhud.R.string.ble_address), mAddress);
        if (mAddress == null) {
            Intent intent = new Intent(activity, BluetoothScanner.class);
            intent.putExtra(activity.getString(com.levemus.gliderhud.R.string.ble_address), (String) mAddress);
            activity.startActivity(intent);
        }  else {
            Intent intent = new Intent(activity, service);
            intent.putExtra(activity.getString(com.levemus.gliderhud.R.string.ble_address), (String) mAddress);
            intent.putExtra(activity.getString(com.levemus.gliderhud.R.string.service_id), id.toString());
            activity.startService(intent);
            activity.bindService(new Intent(activity, service),
                    mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }
    }

    public void setHandler(Handler handler)
    {
        mRemoteHandler = handler;
        if (mAddress != null && !mAddress.isEmpty())
            connect();
        else {
            sendMsg(new Configuration(id(), channels()),
                    new Date().getTime(),
                    new StatusMessage(channels(), ChannelStatus.Status.OFFLINE));
        }
    }
}
