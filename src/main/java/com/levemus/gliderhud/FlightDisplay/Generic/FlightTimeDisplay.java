package com.levemus.gliderhud.FlightDisplay.Generic;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class FlightTimeDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final Double MIN_GROUNDSPEED = 10.0 / 3.6; // m/s

    // Displays
    private TextView mFlightTimeDisplay = null;

    // Variables
    private long mStartTime = 0;

    public FlightTimeDisplay() {
        mProcessors.put(ProcessorID.GROUNDSPEED, ProcessorFactory.build(ProcessorID.GROUNDSPEED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.flight_time_display, container, false);
    }

    @Override
    protected void update() {
        try {
            if(mFlightTimeDisplay == null)
                mFlightTimeDisplay = (TextView) getActivity().findViewById(R.id.flightTimeDisplay);
            long currentTime = new Date().getTime();
            if (mStartTime == 0 && mResults.containsKey(ProcessorID.GROUNDSPEED) && (Double)mResults.get(ProcessorID.GROUNDSPEED) > MIN_GROUNDSPEED)
                mStartTime = currentTime;

            if (mStartTime == 0) {
                mFlightTimeDisplay.setText("00h 00m");
            } else {
                long deltaTime = currentTime - mStartTime;
                long hours = TimeUnit.MILLISECONDS.toHours(deltaTime);
                deltaTime -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(deltaTime);
                mFlightTimeDisplay.setText(String.format("%02dh %02dm", hours, minutes));
            }
        } catch (Exception e) {}
    }
}
