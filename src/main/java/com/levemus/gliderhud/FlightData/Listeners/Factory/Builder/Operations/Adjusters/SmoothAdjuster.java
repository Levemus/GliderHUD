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
/**
 * Created by mark@levemus on 15-12-26.
 */
public class SmoothAdjuster implements IAdjuster {

    private int mWeight = 1;
    private final double INVALID = Double.MIN_VALUE;
    private double mPrevious = INVALID;

    public SmoothAdjuster(int weight) {
        mWeight = weight;
    }

    @Override
    public double adjust(double value) {
        if(mPrevious == INVALID)
            mPrevious = value;

        value += (mPrevious * mWeight);
        value /= (mWeight + 1);
        mPrevious = value;

        return value;
    }
}
