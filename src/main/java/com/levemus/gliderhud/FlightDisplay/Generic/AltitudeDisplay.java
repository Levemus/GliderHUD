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

import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.R;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class AltitudeDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final double MIN_ALTITUDE = 0.0;

    // Displays
    private TextView mAltiDisplay = null;

    public AltitudeDisplay() {
        mProcessors.put(ProcessorID.ALTITUDE, ProcessorFactory.build(ProcessorID.ALTITUDE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.altitude_display, container, false);
    }

    @Override
    protected void update() {
        try {
            if(mAltiDisplay == null)
                mAltiDisplay = (TextView) getActivity().findViewById(R.id.altitudeDisplay);
            if(!mResults.containsKey(ProcessorID.ALTITUDE))
                mAltiDisplay.setText("---");
            else
                mAltiDisplay.setText(Integer.toString((int) Math.max((Double)mResults.get(ProcessorID.ALTITUDE), MIN_ALTITUDE)));
        }catch (Exception e){
            mAltiDisplay.setText("---");
        }
    }
}
