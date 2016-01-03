package com.levemus.gliderhud;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.Window;

import com.levemus.gliderhud.FlightData.Managers.DataManager;
import com.levemus.gliderhud.FlightData.Providers.Bluetooth.BluetoothProvider;
import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.FlightData.Providers.Test.TestProvider;
import com.levemus.gliderhud.FlightDisplay.IFlightDisplay;
import com.levemus.gliderhud.FlightDisplay.MainDisplay;

import java.util.UUID;

/**
 * Created by markcarter on 16-01-02.
 */
public class HUD {

    private final String TAG = this.getClass().getSimpleName();

    DataManager mFlightManager = new DataManager();

    private Provider[] mProviderServices = {
            new BluetoothProvider(),
            //new InternalGPSFlightDataProvider(),
            new TestProvider()
    };

    private IFlightDisplay[] mDisplays = {
            new MainDisplay(),
    };

    private Context mContext;
    private HandlerThread mWorkerThread;
    private Handler mHandler;

    // Activity side
    public void init(final Context context) {
        ((Activity)context).requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((Activity)context).setContentView(com.levemus.gliderhud.R.layout.activity_compass);

        mContext = context;
        if(mWorkerThread == null) {
            mWorkerThread = new HandlerThread(TAG);
            mWorkerThread.start();
            Looper looper = mWorkerThread.getLooper();
            mHandler = new Handler(looper, new ThreadWorker());
        }
    }

    public void deInit() {
    }

    void start() {

        Message message = mHandler.obtainMessage();
        message.arg1 = ThreadWorkerCommand.START;
        mHandler.sendMessage(message);
    }

    void stop() {
        Message message = mHandler.obtainMessage();
        message.arg1 = ThreadWorkerCommand.STOP;
        mHandler.sendMessage(message);
    }

    void pause() {
        Message message = mHandler.obtainMessage();
        message.arg1 = ThreadWorkerCommand.PAUSE;
        mHandler.sendMessage(message);
    }

    void resume() {
        Message message = mHandler.obtainMessage();
        message.arg1 = ThreadWorkerCommand.RESUME;
        mHandler.sendMessage(message);
    }

    // Thread side
    Context mThreadContext;
    private void _start() {
        if(mContext == null)
            return;

        for(Provider provider : mProviderServices) {
            mFlightManager.registerProvider(provider);
            provider.registerClient(mFlightManager);
        }

        for(IFlightDisplay display : mDisplays) {
            display.init((Activity) mContext);
        }
    }

    private void _resume() {
        if(mContext == null)
            return;

        for(Provider provider : mProviderServices) {
            provider.start((Activity) mContext);
        }

        for(IFlightDisplay display : mDisplays) {
            display.registerProvider(mFlightManager);
        }
    }

    private void _pause()  {
        if(mContext == null)
            return;

        for(IFlightDisplay display : mDisplays) {
            display.deRegisterProvider(mFlightManager);
        }

        for(Provider broadcaster : mProviderServices) {
            broadcaster.stop((Activity) mContext);
        }
    }

    private void _stop(){
        if(mContext == null)
            return;

        for(IFlightDisplay display : mDisplays) {
            display.deInit((Activity) mContext);
        }

        for(Provider provider : mProviderServices) {
            mFlightManager.deRegisterProvider(provider);
            provider.deRegisterClient(mFlightManager);
        }
    }

    public static class ThreadWorkerCommand {
        public static final int START = 0;
        public static final int STOP = 1;
        public static final int PAUSE = 2;
        public static final int RESUME = 3;
    }

    public class ThreadWorker implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.arg1) {
                case(ThreadWorkerCommand.START):
                    _start();
                    break;
                case(ThreadWorkerCommand.STOP):
                    _stop();
                    break;
                case(ThreadWorkerCommand.PAUSE):
                    _pause();
                    break;
                case(ThreadWorkerCommand.RESUME):
                    _resume();
                    break;
            }
            return true;
        }
    }
}
