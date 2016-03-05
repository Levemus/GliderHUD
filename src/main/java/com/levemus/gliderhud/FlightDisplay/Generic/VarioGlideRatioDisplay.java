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

/**
 * Created by mark@levemus on 15-12-01.
 */

public class VarioGlideRatioDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final Double MIN_VARIO = 0.10;

    private final Double MIN_CLIMB_TURN_RATE = 10.0;
    private final Double MIN_GLIDE = 0.00;

    // Displays
    private TextView mVarioGlideDisplay = null;

    public VarioGlideRatioDisplay() {
        mProcessors.put(ProcessorID.TURNRATE, ProcessorFactory.build(ProcessorID.TURNRATE));
        mProcessors.put(ProcessorID.VARIO, ProcessorFactory.build(ProcessorID.VARIO));
        mProcessors.put(ProcessorID.GLIDERATIO, ProcessorFactory.build(ProcessorID.GLIDERATIO));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.vario_glideratio_display, container, false);
    }

    private String displayVario(double climbRate) {
        double displayVario = 0;
        displayVario = Math.round(climbRate * 100);
        displayVario /= 100;
        displayVario = Math.max(MIN_VARIO, displayVario);
        return Double.toString(displayVario) + " m/s";
    }

    private String displayGlide(double glideRatio) {
        double displayVario = 0;
        displayVario = Math.round(glideRatio * 100);
        displayVario /= 100;
        return Double.toString(displayVario) + " L/D";
    }

    @Override
    protected void update() {
        try {

            if(mVarioGlideDisplay == null)
                mVarioGlideDisplay = (TextView) getActivity().findViewById(R.id.varioGlideRatioDisplay);

            String displayText = "";

            if(mResults.containsKey(ProcessorID.VARIO))
                displayText = displayVario((Double)mResults.get(ProcessorID.VARIO));
            if(mResults.containsKey(ProcessorID.TURNRATE) && (Double)mResults.get(ProcessorID.TURNRATE) < MIN_CLIMB_TURN_RATE
                    && mResults.containsKey(ProcessorID.GLIDERATIO) && (Double)mResults.get(ProcessorID.GLIDERATIO) < MIN_GLIDE)
                displayText = displayGlide((Double)mResults.get(ProcessorID.GLIDERATIO));

            mVarioGlideDisplay.setText(displayText);
        }catch (Exception e){
            mVarioGlideDisplay.setText("");
        }
    }
}
