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

import com.levemus.gliderhud.FlightData.Processors.Custom.Turnpoint;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Types.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-29.
 */
// this class is only applicable within the context of the compass display
class WaypointDisplay extends CompassSubDisplay {

    @Override
    public HashSet<UUID> processorIDs() {
        return new HashSet<>(Arrays.asList(
                ProcessorID.TURNPOINT));
    }

    private static final double MIN_DISTANCE = 0.1;

    @Override
    public void display(Fragment parent, HashMap<UUID, Object> results) {

        if (mImageView == null) {
            mImageView = (ImageView) parent.getActivity().findViewById(com.levemus.gliderhud.R.id.waypoint_pointer);
            mImageView.setVisibility(View.VISIBLE);
            mTextView = (TextView) parent.getActivity().findViewById(com.levemus.gliderhud.R.id.waypoint_distance);
        }

        Vector turnPoint = (Vector)results.get(ProcessorID.TURNPOINT);

        double distance = turnPoint.Magnitude();
        if(distance <= MIN_DISTANCE )
            return;

        double direction = turnPoint.Direction();
        if(mImageView != null)
            displayImage(parent,direction);

        if(mTextView != null) {
            distance = Math.round(distance / 100);
            distance /= 10;

            StringBuilder builder = new StringBuilder();
            builder.append(Double.toString(distance));
            displayText(parent, direction, Double.toString(distance));
        }
    }

    @Override
    public boolean canDisplay(HashMap<UUID, Object> results) {
        return(results.containsKey(ProcessorID.TURNPOINT));
    }
}
