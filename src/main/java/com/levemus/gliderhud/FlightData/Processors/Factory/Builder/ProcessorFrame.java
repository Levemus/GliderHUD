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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.SelectConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IAdjuster;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.FlightData.Processors.Processor;

/**
 * Created by mark@levemus on 15-12-26.
 */

public class ProcessorFrame extends Processor<Double> {

    protected Double invalid() {return -Double.MAX_VALUE;}

    private HashMap<UUID, Double> mValues = new HashMap<>();
    private IConverter mConverter = null;
    private List<IAdjuster> mAdjusters = null;
    private long mPeriod = 500;

    @Override
    public void process() {
        try {
            mValues = mProvider.pullFrom(this);
            if(mConverter == null) {
                mConverter = new SelectConverter(mValues.keySet().iterator().next());
            }

            double value = mConverter.convert(mValues);

            if(mAdjusters != null) {
                for(IAdjuster adjuster : mAdjusters) {
                    value = adjuster.adjust(value);
                }
            }

            mValue = value;
            if(hasChanged()) {
                mLastValue = value;
            }
        } catch (Exception e){mValue = invalid();}
    }

    @Override
    protected boolean hasChanged() {
        return (mLastValue != mValue );
    }

    @Override
    public long refreshPeriod() { return mPeriod; }

    public void populate(ProcessorFrameConfig config) {
        mChannels = config.mChannels;
        mId = config.mId;
        mConverter = config.mConverter;
        mAdjusters = config.mAdjusters;
        mProvider = config.mProvider;
        mPeriod = config.mPeriod;
    }
}
