package com.levemus.gliderhud.FlightDisplay.Generic;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import android.app.Activity;
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

/**
 * Created by mark@levemus on 16-01-25.
 */

public class BatteryDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();

    // Displays
    private TextView mBatteryLevelDisplay = null;

    public BatteryDisplay() {
        mProcessors.put(ProcessorID.BATTERY, ProcessorFactory.build(ProcessorID.BATTERY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.battery_level_display, container, false);
    }

    @Override
    protected void update() {
        try {

            if(mBatteryLevelDisplay == null)
                mBatteryLevelDisplay = (TextView) getActivity().findViewById(R.id.batteryLevelDisplay);
            if (!mResults.containsKey(ProcessorID.BATTERY))
                mBatteryLevelDisplay.setText("-- %");
            else
                mBatteryLevelDisplay.setText(Long.toString(Math.round((Double)mResults.get(ProcessorID.BATTERY))) + " %");
        } catch (Exception e){}
    }
}
