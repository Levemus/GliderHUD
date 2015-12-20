package com.levemus.gliderhud.FlightData.Broadcasters;

import java.util.HashSet;
import java.util.UUID;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by markcarter on 15-12-16.
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
