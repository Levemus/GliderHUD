package com.levemus.gliderhud.FlightData.Messages;

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

import com.levemus.gliderhud.FlightData.Configuration.Configuration;

/**
 * Created by mark@levemus on 16-01-01.
 */

public class MessageEvent implements Serializable {
    public Configuration mConfig;
    public Long mTime;
    public IMessage mMsg;
    public MessageEvent(Configuration config, Long time, IMessage msg) {
        mConfig = config;
        mTime = time;
        mMsg = msg;
    }

    @Override
    public String toString() {
        return "MessageEvent [mConfig=" + mConfig.toString() + " mTime= "+ mTime.toString() + " mMsg= "+ mMsg.toString() + "]";
    }
}
