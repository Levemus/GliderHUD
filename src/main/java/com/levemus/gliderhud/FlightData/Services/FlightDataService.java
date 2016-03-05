package com.levemus.gliderhud.FlightData.Services;

import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;

import com.levemus.gliderhud.FlightData.Pipeline.MessageCache;
import com.levemus.gliderhud.FlightData.Pipeline.MessageMultiplexer;
import com.levemus.gliderhud.FlightData.Providers.Android.BatteryProvider;
import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.FlightData.Providers.Test.TestProvider;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 16-02-10.
 */
public class FlightDataService extends Service {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();

    private final int MSG_POST_INTERVAL = 500; // ms

    private Provider[] mProviders = {
            //new AltitudeProvider(),
            new BatteryProvider(),
            //new InternalGPSProvider(),
            new TestProvider()
    };

    private MessageMultiplexer mMultiplexer = new MessageMultiplexer();
    private MessageCache mMessageCache = new MessageCache(true);

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private final IBinder mBinder = new FlightDataServiceBinder();

    public static final String intentFilter() {
        return("com.levemus.gliderhud.FlightData.Services.FlightDataService");
    }

    public UUID id() {
       return UUID.fromString("db8c5387-b273-45ec-a59e-b94d19cd17ba");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMultiplexer.add(mMessageCache);
        for(Provider provider : mProviders) {
            mMultiplexer.add(provider);
            provider.add(mMultiplexer);
            provider.start(this);
        }

        mHandlerThread = new HandlerThread("LocalServiceThread");
        mHandlerThread.start();
        mHandler = new Handler (mHandlerThread.getLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HashMap<UUID, Double> data = mMessageCache.data();
                if(data.size() > 0) {
                    DataMessage msg = new DataMessage(id(), new HashSet<>(data.keySet()), new Date().getTime(), data);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("MSG", msg);

                    Intent intent = new Intent(intentFilter());
                    intent.putExtra("MSG", bundle);

                    sendBroadcast(intent);
                }
                mHandler.postDelayed(this, MSG_POST_INTERVAL);
            }
        };
        runnable.run();
    }

    @Override
    public void onDestroy() {

        mHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quit();
        mHandlerThread = null;

        for(Provider provider : mProviders) {
            provider.stop(this);
            provider.remove(mMultiplexer);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class FlightDataServiceBinder extends Binder {
        public FlightDataService getService() {
            return FlightDataService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
