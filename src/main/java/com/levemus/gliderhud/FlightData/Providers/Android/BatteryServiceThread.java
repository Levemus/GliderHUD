package com.levemus.gliderhud.FlightData.Providers.Android;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.metrics.HUDMetricsID;
import com.reconinstruments.os.metrics.HUDMetricsManager;
import com.reconinstruments.os.metrics.MetricsValueChangedListener;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public class BatteryServiceThread extends ServiceProviderThread implements IChannelized, IIdentifiable {

    private final String TAG = this.getClass().getSimpleName();
    private BroadcastReceiver mBatInfoReceiver;

    public BatteryServiceThread(String id) {
        super(id);
    }

    @Override
    public void run() {
        Looper.prepare();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = (Bundle)msg.obj;
                SerializablePayloadMessage message = (SerializablePayloadMessage) bundle.getSerializable("MSG");
                onRequest(message);
            }
        };

        try {
            mBatInfoReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent intent) {
                    int value = intent.getIntExtra("level", 0);
                    double scale = intent.getIntExtra("scale", 0);
                    if(scale > 0) {
                        HashMap<UUID, Double> values = new HashMap<>();
                        values.put(Channels.BATTERY, ((double) value / scale * 100.0));
                        sendResponse(new DataMessage(id(), new HashSet(values.keySet()), new Date().getTime(), values));
                    }
                }
            };
            Intent intent = mParent.registerReceiver(this.mBatInfoReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            int value = intent.getIntExtra("level", 0);
            double scale = intent.getIntExtra("scale", 0);
            if(scale > 0) {
                HashMap<UUID, Double> values = new HashMap<>();
                values.put(Channels.BATTERY, ((double) value / scale * 100.0));
                sendResponse(new DataMessage(id(), new HashSet(values.keySet()), new Date().getTime(), values));
            }
            Log.d(TAG, "Battery Enabled");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Looper.loop();
    }

    public UUID id() {
        return UUID.fromString("7a15c5d3-b136-4f3d-b809-1395c9cc9f83");
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                Channels.BATTERY));
    }
}
