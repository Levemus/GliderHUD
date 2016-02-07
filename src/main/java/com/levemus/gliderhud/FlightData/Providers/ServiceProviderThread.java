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

import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.levemus.gliderhud.Messages.SerializablePayloadMessage;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public abstract class ServiceProviderThread extends Thread {

    private final String TAG = this.getClass().getSimpleName();

    public ServiceProviderThread(String id) {
        super(id);
    }

    protected Handler mRequestHandler;
    protected Handler mResponseHandler;
    public void setResponseHandler(Handler handler) {
        mResponseHandler = handler;
    }

    protected void sendMessage(Handler handler, SerializablePayloadMessage msg) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("MSG", msg);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    public void sendRequest(SerializablePayloadMessage msg) {
        sendMessage(mRequestHandler, msg);
    }

    protected void sendResponse(SerializablePayloadMessage msg) {
        sendMessage(mResponseHandler, msg);
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
        Looper.loop();
        // _onStartCommand(intent, flags, startId);
    }

    protected Service mParent;
    public void init(Service parent) {
        mParent = parent;
        setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        if(this.getState() == State.NEW)
            start();
    }

    protected void onRequest(SerializablePayloadMessage message) {}
}
