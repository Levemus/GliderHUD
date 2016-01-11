package com.levemus.gliderhud.FlightData.Providers.Bluetooth;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Providers.MessageService;
import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public class BluetoothService extends MessageService implements IIdentifiable, IChannelized {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public HashSet<UUID> channels() { return mWorkerThread.channels(); }

    @Override
    public UUID id() { return mWorkerThread.id(); }

    private BluetoothServiceThread mWorkerThread = new BluetoothServiceThread(TAG);
    protected ServiceProviderThread workerThread() {
        return mWorkerThread;
    }

    @Override
    public void start(final Activity activity, Class service, UUID id) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(com.levemus.gliderhud.R.string.full_app_name), Context.MODE_PRIVATE);
        String address = sharedPref.getString(activity.getString(com.levemus.gliderhud.R.string.ble_address), "");

        if (address == null) {
            Intent intent = new Intent(activity, BluetoothScanner.class);
            intent.putExtra(activity.getString(com.levemus.gliderhud.R.string.ble_address), (String) address);
            activity.startActivity(intent);
        }  else {
            Intent intent = new Intent(activity, service);
            String key = activity.getString(com.levemus.gliderhud.R.string.service_id);
            intent.putExtra(key, id.toString());
            intent.putExtra(activity.getString(com.levemus.gliderhud.R.string.ble_address), (String) address);
            intent.putExtra(activity.getString(com.levemus.gliderhud.R.string.service_id), id.toString());
            activity.bindService(new Intent(activity, service),
                    mConnection, Context.BIND_AUTO_CREATE);
        }
    }

}
