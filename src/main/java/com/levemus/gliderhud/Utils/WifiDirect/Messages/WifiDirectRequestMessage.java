package com.levemus.gliderhud.Utils.WifiDirect.Messages;

import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectRequest;

import java.util.HashMap;

public class WifiDirectRequestMessage extends WifiDirectMessage<WifiDirectRequest.Request> {

    public WifiDirectRequestMessage(WifiDirectRequest.Request request) {
        super(request, new HashMap<String, String>());
    }

    public WifiDirectRequestMessage(WifiDirectRequest.Request request, HashMap<String, String> data) {
        super(request, data);
    }
}
