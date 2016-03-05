package com.levemus.gliderhud.FlightDisplay.Recon.Compass;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-18.
 */
// this class is only applicable within the context of the compass display
class LaunchDisplay extends CompassSubDisplay {

    @Override
    public HashSet<UUID> processorIDs() {
        return new HashSet<>(Arrays.asList(
                ProcessorID.BEARINGTO,
                ProcessorID.DISTANCEFR,
                ProcessorID.TURNPOINT));
    }

    @Override
    public void display(Fragment parent, HashMap<UUID, Object> results) {

        if(mImageView == null) {
            mImageView = (ImageView) parent.getActivity().findViewById(com.levemus.gliderhud.R.id.launch_pointer);
            mImageView.setVisibility(View.VISIBLE);
            mTextView = (TextView) parent.getActivity().findViewById(com.levemus.gliderhud.R.id.launch_distance);
        }

        double distance = (Double)results.get(ProcessorID.DISTANCEFR);
        double direction = (Double)results.get(ProcessorID.BEARINGTO);

        if(mImageView != null)
            displayImage(parent,direction);

        if(mTextView != null) {
            distance = Math.round(distance / 100);
            distance /= 10;
            displayText(parent, direction, Double.toString(distance));
        }
    }

    private Double MIN_DISTANCE = 200.0;

    @Override
    public boolean canDisplay(HashMap<UUID, Object> results) {

        if(!results.containsKey(ProcessorID.DISTANCEFR)
                || !results.containsKey(ProcessorID.BEARINGTO)
                || !results.containsKey(ProcessorID.TURNPOINT) )
            return false;

        if((Double)results.get(ProcessorID.DISTANCEFR) < MIN_DISTANCE)
            return false;

        return true;
    }
}
