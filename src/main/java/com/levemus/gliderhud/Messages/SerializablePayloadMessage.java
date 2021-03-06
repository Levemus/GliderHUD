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
import java.util.HashSet;

/**
 * Created by mark@levemus on 16-01-06.
 */

public abstract class SerializablePayloadMessage<E, F, G> implements IMessage<E>, IPayload<F, G>, Serializable {
    private final String TAG = this.getClass().getSimpleName();

    protected HashMap<F, G> mValues;
    protected E mOpCode;
    public E opCode() { return mOpCode; }


    public SerializablePayloadMessage(E opCode, HashMap<F, G> values) {
        mOpCode = opCode;
        mValues = values;
    }

    public SerializablePayloadMessage(E opCode) {
        mOpCode = opCode;
        mValues = new HashMap<F, G>();
    }

    @Override
    public G get(F key) throws UnsupportedOperationException {
        try {
            if (mValues.containsKey(key))
                return mValues.get(key);
        } catch (Exception e) {
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public HashSet<F> keys() {
        return (new HashSet<F>(mValues.keySet()));
    }
}
