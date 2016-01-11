package com.levemus.gliderhud.Utils.WifiDirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Providers.MessageService;
import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectEvent;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectRequest;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.WifiDirectEventMessage;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.WifiDirectRequestMessage;
import com.levemus.gliderhud.Utils.WifiDirect.Operations.WifiDirectOperationConfig;
import com.levemus.gliderhud.Utils.WifiDirect.Operations.WifiDirectSendFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


/**
 * Created by markcarter on 16-01-04.
 */
public class WifiDirectService extends MessageService {
    private final String TAG = this.getClass().getSimpleName();

    private WifiDirectServiceThread mWorkerThread = new WifiDirectServiceThread(TAG);
    protected ServiceProviderThread workerThread() {
        return mWorkerThread;
    }

    @Override
    public void stop(Activity activity, Class cls) {
        mWorkerThread.shutdown();
        super.stop(activity, cls);
    }
}
