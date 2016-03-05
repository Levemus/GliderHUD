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

import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.R;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class GroundSpeedDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_GROUND_SPEED = 10.0 / 3.6; // m/s

    // Displays
    private TextView mGroundSpeedDisplay = null;

    // Variables
    private boolean mIsFlying = false;

    public GroundSpeedDisplay() {
        mProcessors.put(ProcessorID.GROUNDSPEED, ProcessorFactory.build(ProcessorID.GROUNDSPEED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ground_speed_display, container, false);
    }

    @Override
    protected void update() {
        try {
            if(mGroundSpeedDisplay == null)
                mGroundSpeedDisplay = (TextView) getActivity().findViewById(R.id.groundSpeedDisplay);

            if(!mResults.containsKey(ProcessorID.GROUNDSPEED))
                mGroundSpeedDisplay.setText("---");
            else {
                double value = (Double)mResults.get(ProcessorID.GROUNDSPEED);
                if (!mIsFlying) {
                    mGroundSpeedDisplay.setText("0");
                    if (value > MIN_GROUND_SPEED)
                        mIsFlying = true;
                } else
                    mGroundSpeedDisplay.setText(Integer.toString((int)(value * 3.6)));
            }
        }catch (Exception e){
            mGroundSpeedDisplay.setText("---");
        }
    }
}
