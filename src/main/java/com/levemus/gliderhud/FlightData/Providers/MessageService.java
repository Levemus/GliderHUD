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
import android.os.Message;

import com.levemus.gliderhud.FlightData.Managers.IClient;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;
import com.levemus.gliderhud.Messages.ServiceMessages.ServiceEvent;
import com.levemus.gliderhud.Messages.ServiceMessages.ServiceEventMessage;

import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public abstract class MessageService extends Service implements IMessageService {

    private final String TAG = this.getClass().getSimpleName();

    IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public MessageService getInstance() {
            return MessageService.this;
        }
    }

    protected MessageService mService = null;

    protected ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mService = ((MessageService.LocalBinder)iBinder).getInstance();

            if(mClient != null)
                mClient.onMsg(new ServiceEventMessage(ServiceEvent.Events.BOUND, mService.id()));

            workerThread().setResponseHandler(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Bundle bundle = (Bundle)msg.obj;
                    SerializablePayloadMessage message = (SerializablePayloadMessage) bundle.getSerializable("MSG");
                    if(mClient != null)
                        mClient.onMsg(message);
                }});

            workerThread().init(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mService = null;
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract ServiceProviderThread workerThread();
    protected UUID mId;
    public UUID id() {return mId;}

    @Override
    public void start(final Activity activity, Class service, UUID id) {
        Intent intent = new Intent(activity, service);
        String key = activity.getString(com.levemus.gliderhud.R.string.service_id);
        intent.putExtra(key, id.toString());

        activity.bindService(intent,
                mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stop(Activity activity, Class service) {
        Intent intent = new Intent(activity, service);
        activity.stopService(intent);

        if (mService != null) {
            activity.unbindService(mConnection);
        }
    }

    protected IClient mClient;
    @Override
    public void registerClient(IClient client) {mClient = client;}

    @Override
    public void deRegisterClient(IClient client) {mClient = null;}

    @Override
    public void resume(Activity activity) {}
    @Override
    public void pause(Activity activity) {}

    @Override
    public void sendRequest(SerializablePayloadMessage msg) { workerThread().sendRequest(msg); }
}
