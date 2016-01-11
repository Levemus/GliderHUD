package com.levemus.gliderhud.Messages;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by mark@levemus on 16-01-06.
 */

public abstract class SerializablePayloadMessage<E, F, G> extends PayloadMessage<E, F, G> implements Serializable {

    public SerializablePayloadMessage(E opCode, HashMap<F, G> values) {
        super(opCode, values);
    }

    public SerializablePayloadMessage(E opCode) {
        super(opCode, new HashMap<F, G>());
    }
}