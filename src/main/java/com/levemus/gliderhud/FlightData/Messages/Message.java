package com.levemus.gliderhud.FlightData.Messages;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-28.
 */
public abstract class Message<E> implements IMessage{

        @Override
        public abstract Type getType();

        protected HashMap<UUID, E> mValues;
        public Message(HashMap<UUID, E> values) {
            mValues = values;
        }
        public Message() {
        mValues = new HashMap<UUID, E>();
    }

        @Override
        public E get(UUID channel) throws UnsupportedOperationException {
            try {
                if(mValues.containsKey(channel))
                    return mValues.get(channel);
            }
            catch(Exception e) {}
            throw new UnsupportedOperationException();
        }

        @Override
        public HashSet<UUID> channels() {
            return(new HashSet<UUID>(mValues.keySet()));
        }
    }

