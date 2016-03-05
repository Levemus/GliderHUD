package com.levemus.gliderhud.FlightData.Processors;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Pipeline.ChannelDataSource;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

/**
 * Created by mark@levemus on 15-12-26.
 */
public interface IProcessor<E> {
    E onMsg(DataMessage msg);
    void process();
    boolean isValid(E value);
}
