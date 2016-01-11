package com.levemus.gliderhud.Utils.WifiDirect.Messages;

import com.levemus.gliderhud.Messages.IMessage;
import com.levemus.gliderhud.Messages.IPayload;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;

import java.util.HashMap;
import java.util.HashSet;

public abstract class WifiDirectMessage<E> extends SerializablePayloadMessage<E, String, String> {

    public WifiDirectMessage(E opCode) {
        super(opCode, new HashMap<String, String>());
    }

    public WifiDirectMessage(E opCode, HashMap<String, String> data) {
        super(opCode, data);
    }

}
