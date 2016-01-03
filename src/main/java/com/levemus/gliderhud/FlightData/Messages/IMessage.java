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

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-05.
 */

public interface IMessage <E> {

    enum Type { DATA, STATUS };
    Type getType();

    HashSet<UUID> channels();

    E get(UUID channel) throws java.lang.UnsupportedOperationException;

}
