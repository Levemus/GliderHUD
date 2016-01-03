package com.levemus.gliderhud.FlightData.Processors.Factory.Builder;

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
import java.util.List;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IAdjuster;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IConverter;

/**
 * Created by mark@levemus on 15-12-28.
 */
public class ProcessorFrameConfig {
    public UUID mId = UUID.randomUUID();
    public HashSet<UUID> mChannels = new HashSet<>();
    public IConverter mConverter = null;
    public List<IAdjuster> mAdjusters = null;
    public Long mPeriod = 500L;
}
