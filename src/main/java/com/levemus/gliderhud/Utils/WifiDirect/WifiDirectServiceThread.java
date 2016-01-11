package com.levemus.gliderhud.Utils.WifiDirect;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectEvent;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectRequest;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.WifiDirectEventMessage;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.WifiDirectRequestMessage;
import com.levemus.gliderhud.Utils.WifiDirect.Operations.WifiDirectOperationConfig;
import com.levemus.gliderhud.Utils.WifiDirect.Operations.WifiDirectReceiveFile;
import com.levemus.gliderhud.Utils.WifiDirect.Operations.WifiDirectSendFile;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by mark@levemus on 16-01-02.
 */

public class WifiDirectServiceThread extends ServiceProviderThread implements WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener {

    private final String TAG = this.getClass().getSimpleName();

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    boolean mEnabled = false;
    boolean mRetryChannel = false;
    IntentFilter mIntentFilter;

    public WifiDirectServiceThread(String id) {
        super(id);
    }

    @Override
    public void run() {
        Looper.prepare();
        try {
            mRequestHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Bundle bundle = (Bundle)msg.obj;
                    SerializablePayloadMessage message = (SerializablePayloadMessage) bundle.getSerializable("MSG");
                    onRequest(message);
                }
            };

            mManager = (WifiP2pManager) mParent.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(mParent, Looper.myLooper(), this);

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
            mParent.registerReceiver(mReceiver, mIntentFilter, null, new Handler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Looper.loop();


    }

    void shutdown()
    {
        mParent.unregisterReceiver(mReceiver);
    }

    private boolean mConnect = false;

    @Override
    protected void onRequest(SerializablePayloadMessage message) {
        final WifiDirectRequestMessage request = (WifiDirectRequestMessage)message;

        if(request.opCode() == WifiDirectRequest.Request.DISCOVER_PEERS && mEnabled) {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.DISCOVER_PEERS_SUCCESS));
                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Discover peers failed. Reason :" + reasonCode);
                    sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.DISCOVER_PEERS_FAIL));
                }
            });
        }
        else if(request.opCode() == WifiDirectRequest.Request.CONNECT) {

            final WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = request.get("address");
            config.wps.setup = WpsInfo.PBC;
            if(mConnect == true)
                return;

            mConnect =  true;
            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.CONNECTING));
                }

                @Override
                public void onFailure(int reason) {
                    sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.DISCONNECTED));
                    reset();
                }
            });
        } else if(request.opCode() == WifiDirectRequest.Request.DISCONNECT) {
            if(mChannel != null) {
                mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onFailure(int reasonCode) {
                        Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                        reset();
                    }

                    @Override
                    public void onSuccess() {
                        sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.DISCONNECTED));
                        reset();
                    }

                });
            }
        } else if(request.opCode() == WifiDirectRequest.Request.TX_DATA) {
            HashMap<String, String> operationParams = new HashMap<String, String>(){{
                put("filename", request.get("filename"));
            }};
            sendResponse(new WifiDirectEventMessage(new WifiDirectSendFile().perform(new WifiDirectOperationConfig(mConnectionInfo), operationParams)));
        }else if(request.opCode() == WifiDirectRequest.Request.RX_DATA) {
            HashMap<String, String> operationParams = new HashMap<String, String>(){{
                put("filename", request.get("filename"));
            }};
            sendResponse(new WifiDirectEventMessage(new WifiDirectReceiveFile().perform(new WifiDirectOperationConfig(mConnectionInfo), operationParams)));
        }
    }

    @Override
    public void onChannelDisconnected() {
        if(!mRetryChannel) {
            Log.i(TAG, "onChannel Disconnected");
            reset();
            mRetryChannel = true;
            mManager.initialize(mParent, Looper.myLooper(), this);
        }
    }

    public void updatePeers(WifiP2pDeviceList peers) {

        HashSet<WifiP2pDevice> intersection = new HashSet<WifiP2pDevice>(peers.getDeviceList());
        intersection.removeAll(mPeerList);

        for(final WifiP2pDevice device : intersection) {
            HashMap<String, String> operationParams = new HashMap<String, String>() {{
                put("name", device.deviceName);
                put("address", device.deviceAddress);
            }};

            sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.PEER_ONLINE, operationParams));
        }

        mPeerList.clear();
        mPeerList.addAll(peers.getDeviceList());
        if (mPeerList.size() == 0) {
            Log.d(TAG, "No devices found");
            if(mConnect == true)
                onRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.DISCONNECT));
            reset();
            return;
        }
    }

    private HashSet<WifiP2pDevice> mPeerList = new HashSet<>();
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        updatePeers(peers);
    }

    WifiP2pInfo mConnectionInfo;
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        mConnectionInfo = info;
        mRetryChannel = false;
        sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.CONNECTED));
    }

    public void reset() {
        mConnect = false;
        mPeerList.clear();
    }

    public void onDeviceStatus(WifiP2pDevice device) {
        if(device.status == WifiP2pDevice.AVAILABLE)
            sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.AVAILABLE));
    }

    public void setEnabled(boolean enabled) {
        if(enabled)
            sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.ENABLED));
        else
            sendResponse(new WifiDirectEventMessage(WifiDirectEvent.Event.DISABLED));
        mEnabled = enabled;
    }
}