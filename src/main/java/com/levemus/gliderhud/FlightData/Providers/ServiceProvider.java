package com.levemus.gliderhud.FlightData.Providers;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import java.util.UUID;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataClient;
import com.levemus.gliderhud.FlightData.Configuration.Configuration;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Messages.MessageEvent;

/**
 * Created by mark@levemus on 16-01-02.
 */

public abstract class ServiceProvider extends Service implements IServiceProvider {

    private final String TAG = this.getClass().getSimpleName();

    public class LocalBinder extends Binder {
        public ServiceProvider getInstance() {
            return ServiceProvider.this;
        }
    }

    IBinder mBinder = new LocalBinder();
    protected Handler mRemoteHandler;
    protected Handler mLocalHandler;

    public void setHandler(Handler handler)
    {
        mRemoteHandler = handler;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Thread mWorkerThread;
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        mWorkerThread = new Thread(TAG + "(" + startId + ")") {
            @Override
            public void run() {
                Looper.prepare();
                mLocalHandler = new Handler();
                mId = UUID.fromString(intent.getStringExtra(getString(com.levemus.gliderhud.R.string.service_id)));
                _onStartCommand(intent, flags, startId);
                Looper.loop();
            }
        };
        mWorkerThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mWorkerThread.start();
        return 0;
    }

    protected abstract int _onStartCommand(Intent intent, int flags, int startId);

    protected void sendMsg(Configuration config, Long time, IMessage msg) {
        if (mRemoteHandler != null) {
            Message message = mRemoteHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putSerializable("MSG", new MessageEvent(config, time, msg));
            message.obj = bundle;
            mRemoteHandler.sendMessage(message);
        }
    }

    protected boolean mIsBound = false;

    protected UUID mId;
    protected UUID id() {return mId;}

    @Override
    public void start(final Activity activity, Class service, UUID id) {
        Intent intent = new Intent(activity, service);
        String key = activity.getString(com.levemus.gliderhud.R.string.service_id);
        intent.putExtra(key, id.toString());
        activity.startService(intent);
        activity.bindService(new Intent(activity, service),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    @Override
    public void stop(Activity activity, Class service) {
        Intent intent = new Intent(activity, service);
        activity.stopService(intent);

        if (mIsBound) {
            activity.unbindService(mConnection);
            mIsBound = false;
        }
    }

    protected IChannelDataClient mClient;
    @Override
    public void registerClient(IChannelDataClient client) {mClient = client;}

    @Override
    public void deRegisterClient(IChannelDataClient client) {mClient = null;}


    protected ServiceProvider mService = null;
    protected Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = (Bundle)msg.obj;
            MessageEvent notify = (MessageEvent)bundle.getSerializable("MSG");
            if(mClient != null)
                mClient.pushTo(notify.mConfig, notify.mTime, notify.mMsg);
        }
    };

    protected ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mService = ((ServiceProvider.LocalBinder)iBinder).getInstance();
            mService.setHandler(mMsgHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mService = null;
        }
    };
}
