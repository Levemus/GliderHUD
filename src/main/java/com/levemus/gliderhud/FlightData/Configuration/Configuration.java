package com.levemus.gliderhud.FlightData.Configuration;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-29.
 */
public class Configuration implements IIdentifiable,Serializable {

    protected UUID mId;
    @Override
    public UUID id() { return mId; }

    public Configuration(UUID id) {
        mId = id;
    }

    @Override
    public String toString() {
        return "Configuration [mId=" + mId.toString() + "]";
    }
}
