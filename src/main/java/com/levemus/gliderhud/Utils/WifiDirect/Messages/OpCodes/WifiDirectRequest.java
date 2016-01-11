package com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes;

/**
 * Created by markcarter on 16-01-05.
 */
public interface WifiDirectRequest {

    enum Request {
        DISCOVER_PEERS,
        CONNECTION_INFO,
        CONNECT,
        DISCONNECT,
        TX_DATA,
        RX_DATA
    }
}
