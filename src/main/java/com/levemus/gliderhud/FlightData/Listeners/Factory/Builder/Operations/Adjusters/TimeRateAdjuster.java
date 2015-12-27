package com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.Adjusters;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IAdjuster;

import java.util.Date;

/**
 * Created by markcarter on 15-12-26.
 */
public class TimeRateAdjuster implements IAdjuster {
    private long mPreviousTime = -1;
    private final double INVALID = Double.MIN_VALUE;
    private double mPreviousValue = INVALID;

    @Override
    public double adjust(double value) {
        if(mPreviousValue == INVALID)
            mPreviousValue = value;
        double deltaValue = value - mPreviousValue;

        if(mPreviousTime == -1) {
            mPreviousTime = new Date().getTime();
            return 0;
        }

        long deltaTime = new Date().getTime() - mPreviousTime;
        return deltaValue / deltaTime;
    }
}
