package com.levemus.gliderhud.Messages.ServiceMessages;

import com.levemus.gliderhud.FlightData.Configuration.ChannelConfiguration;
import com.levemus.gliderhud.Messages.IMessage;
import com.levemus.gliderhud.Messages.IPayload;
import com.levemus.gliderhud.Messages.SerializablePayloadMessage;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by markcarter on 16-01-07.
 */
public class ServiceEventMessage extends SerializablePayloadMessage<ServiceEvent.Events, String, String> {

    public ServiceEventMessage(ServiceEvent.Events opCode) {
        super(opCode, new HashMap<String, String>());
    }

    public ServiceEventMessage(ServiceEvent.Events opCode, HashMap<String, String> values) {
        super(opCode, values);
    }
}

