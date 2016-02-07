package com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Adjusters;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.Date;

import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IAdjuster;

/**
 * Created by mark@levemus on 15-12-26.
 */

public class TimeRateAdjuster implements IAdjuster {
    private long mPreviousTime = -1;
    private final double INVALID = Double.MIN_VALUE;
    private double mPreviousValue = INVALID;
    private double mMaxValue = -1 * Double.MAX_VALUE;

    public TimeRateAdjuster() {}
    public TimeRateAdjuster(Double maxValue) {
        mMaxValue = maxValue;
    }

    @Override
    public double adjust(double value) {
        if(mPreviousValue == INVALID)
            mPreviousValue = value;

        double deltaValue = 0;

        if(mMaxValue != -1 * Double.MAX_VALUE)
            deltaValue = ((value + mMaxValue) - mPreviousValue ) % mMaxValue;
        else
            deltaValue = value - mPreviousValue;

        if(mPreviousTime == -1) {
            mPreviousTime = new Date().getTime();
            return 0;
        }

        long currentTime = new Date().getTime();
        long deltaTime = currentTime - mPreviousTime;
        mPreviousTime = currentTime;
        return (deltaValue * 1000) / deltaTime;
    }
}
