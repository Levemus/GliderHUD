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

import java.util.UUID;
import java.util.HashSet;
import java.util.List;

import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IAdjuster;

/**
 * Created by mark@levemus on 15-12-26.
 */

public class ProcessorBuilder {
    private ProcessorFrameConfig mConfig = new ProcessorFrameConfig();

    public ProcessorBuilder id(UUID id) {
        mConfig.mId = id;
        return this;
    }

    public ProcessorBuilder channels(HashSet channels) {
        mConfig.mChannels = channels;
        return this;
    }

    public ProcessorBuilder converter(IConverter op) {
        mConfig.mConverter = op;
        return this;
    }

    public ProcessorBuilder adjusters(List<IAdjuster>ops) {
        mConfig.mAdjusters = ops;
        return this;
    }

    public ProcessorBuilder period(Long period) {
        mConfig.mPeriod = period;
        return this;
    }

    public Processor build() {
        // create and populate
        ProcessorFrame result = new ProcessorFrame();
        result.populate(mConfig);

        // reset defaults
        mConfig = new ProcessorFrameConfig();

        return result;
    }
}


