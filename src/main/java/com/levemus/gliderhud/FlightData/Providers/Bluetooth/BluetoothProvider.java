package com.levemus.gliderhud.FlightData.Providers.Bluetooth;

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

import java.util.HashSet;
import java.util.UUID;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.content.Context;
import android.content.Intent;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataClient;
import com.levemus.gliderhud.FlightData.Messages.Data.Bluetooth.BluetoothDataMessageFactory;
import com.levemus.gliderhud.FlightData.Providers.Provider;

import android.annotation.SuppressLint;

/**
 * Created by mark@levemus on 15-12-08.
 */

@SuppressLint("NewApi")
public class BluetoothProvider extends Provider {
    private final String TAG = this.getClass().getSimpleName();
    private String mAddress = null;

    BluetoothDataMessageFactory mMsgFactory = new BluetoothDataMessageFactory();

    @Override
    public void start(Activity activity) {
        Log.d(TAG, "resume()");

        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(com.levemus.gliderhud.R.string.full_app_name), Context.MODE_PRIVATE);
        mAddress = sharedPref.getString(activity.getString(com.levemus.gliderhud.R.string.ble_address), mAddress);
        if (mAddress == null) {
            activity.startActivity(new Intent(activity, BluetoothScanner.class));
        }  else {
            service.start(activity, BluetoothService.class, id());
        }
    }

    private BluetoothService service = new BluetoothService();

    @Override
    public void registerClient(IChannelDataClient client) {
        service.registerClient(client);
    }

    @Override
    public void deRegisterClient(IChannelDataClient client) {
        service.deRegisterClient(client);
    }

    @Override
    public UUID id() {
        return UUID.fromString("e972af0a-1936-4d24-8a7d-dcf561e08f6b");
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet<>(mMsgFactory.supportedTypes());
    }
}


