package com.levemus.gliderhud.FlightData.Broadcasters;

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
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mark@levemus on 15-12-16.
 */
public class BroadcasterStatus {

    public enum Status {
        OFFLINE, ONLINE
    };

    private HashMap<UUID, Status> mStatus = new HashMap<UUID, Status>();
    public Status getStatus(UUID service) throws java.lang.UnsupportedOperationException {
        if(mStatus.containsKey(service))
            return mStatus.get(service);
        throw new java.lang.UnsupportedOperationException();
    }

    public void setStatus(UUID service, Status status) {
        mStatus.put(service, status);
    }

    public HashSet<UUID> affectedTypes() {
        return new HashSet(mStatus.keySet());
    }
}
