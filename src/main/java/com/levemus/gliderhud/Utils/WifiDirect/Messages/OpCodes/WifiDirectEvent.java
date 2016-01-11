package com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes;

/**
 * Created by markcarter on 16-01-05.
 */
public interface WifiDirectEvent {

    enum Event {
        ENABLED,
        DISABLED,
        DISCOVER_PEERS_SUCCESS,
        DISCOVER_PEERS_FAIL,
        CONNECTION_INFO_AVAILABLE,
        PEER_ONLINE,
        PEER_OFFLINE,
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        OPERATION_SUCCESS,
        OPERATION_FAIL,
        AVAILABLE,
        DEVICE_OFFLINE
    };
}
