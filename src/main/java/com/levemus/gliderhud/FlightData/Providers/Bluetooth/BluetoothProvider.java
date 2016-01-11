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
import android.content.Context;
import android.content.Intent;

import com.levemus.gliderhud.FlightData.Managers.IClient;
import com.levemus.gliderhud.FlightData.Providers.Provider;

import android.annotation.SuppressLint;

/**
 * Created by mark@levemus on 15-12-08.
 */

@SuppressLint("NewApi")
public class BluetoothProvider extends Provider {
    private final String TAG = this.getClass().getSimpleName();
    private String mAddress = null;

    @Override
    public void start(Activity activity) {

        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(com.levemus.gliderhud.R.string.full_app_name), Context.MODE_PRIVATE);
        mAddress = sharedPref.getString(activity.getString(com.levemus.gliderhud.R.string.ble_address), mAddress);
        if (mAddress == null) {
            activity.startActivity(new Intent(activity, BluetoothScanner.class));
        }  else {
            mService.start(activity, BluetoothService.class, id());
        }
    }

    private BluetoothService mService = new BluetoothService();

    @Override
    public void registerClient(IClient client) {
        mService.registerClient(client);
    }

    @Override
    public void deRegisterClient(IClient client) {
        mService.deRegisterClient(client);
    }

    @Override
    public UUID id() {
        return(mService.id());
    }

    @Override
    public HashSet<UUID> channels() {
        return mService.channels();
    }

}


