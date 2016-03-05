package com.levemus.gliderhud.FlightData.Providers.Android;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class BatteryProvider extends Provider {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();
    private BroadcastReceiver mBatInfoReceiver;

    public void start(Context ctx) {
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                int value = intent.getIntExtra("level", 0);
                double scale = intent.getIntExtra("scale", 0);
                if(scale > 0) {
                    HashMap<UUID, Double> values = new HashMap<>();
                    values.put(Channels.BATTERY, ((double) value / scale * 100.0));
                    if(mClient != null)
                        mClient.onMsg(new DataMessage(id(), new HashSet(values.keySet()), new Date().getTime(), values));
                }
            }
        };
        Intent intent = ctx.registerReceiver(mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int value = intent.getIntExtra("level", 0);
        double scale = intent.getIntExtra("scale", 0);
        if(scale > 0) {
            HashMap<UUID, Double> values = new HashMap<>();
            values.put(Channels.BATTERY, ((double) value / scale * 100.0));
            if(mClient != null)
                mClient.onMsg(new DataMessage(id(), new HashSet(values.keySet()), new Date().getTime(), values));
        }
    }

    @Override
    public void stop(Context ctx) {
        ctx.unregisterReceiver(mBatInfoReceiver);
    }

    @Override
    public UUID id() {
        return UUID.fromString("7a15c5d3-b136-4f3d-b809-1395c9cc9f83");
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                Channels.BATTERY));
    }
}
