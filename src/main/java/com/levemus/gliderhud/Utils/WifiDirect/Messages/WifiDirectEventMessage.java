package com.levemus.gliderhud.Utils.WifiDirect.Messages;

import com.levemus.gliderhud.Messages.IMessage;
import com.levemus.gliderhud.Messages.IPayload;
import com.levemus.gliderhud.Utils.WifiDirect.Messages.OpCodes.WifiDirectEvent;

import java.util.HashMap;

public class WifiDirectEventMessage extends WifiDirectMessage<WifiDirectEvent.Event> {

    public WifiDirectEventMessage(WifiDirectEvent.Event event) {
        super(event, new HashMap<String, String>());
    }

    public WifiDirectEventMessage(WifiDirectEvent.Event event, HashMap<String, String> data) {
        super(event, data);
    }

    @Override
    public String toString() {
        return "WifiDirectEventMessage [opCode=" + mOpCode + "]";
    }

}
