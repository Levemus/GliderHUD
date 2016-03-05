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
import com.levemus.gliderhud.Types.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-29.
 */

// this class is only applicable within the context of the compass display
class WindDisplay extends CompassSubDisplay {

    @Override
    public HashSet<UUID> processorIDs() {
        return new HashSet<>(Arrays.asList(
                ProcessorID.WINDDRIFT));
    }

    private double DEGREES_FULL_CIRCLE = 360;
    private double DEGREES_HALF_CIRCLE = DEGREES_FULL_CIRCLE / 2;

    @Override
    public void display(Fragment parent, HashMap<UUID, Object> results) {

        if (mImageView == null) {
            mImageView = (ImageView) parent.getActivity().findViewById(com.levemus.gliderhud.R.id.wind_pointer);
            mImageView.setVisibility(View.VISIBLE);
            mTextView = (TextView) parent.getActivity().findViewById(com.levemus.gliderhud.R.id.wind_strength);
        }
        double direction = (((Vector)results.get(ProcessorID.WINDDRIFT)).Direction() + DEGREES_HALF_CIRCLE) % DEGREES_FULL_CIRCLE;

        displayImage(parent, direction);
        displayText(parent, direction, Double.toString(Math.round(((Vector)results.get(ProcessorID.WINDDRIFT)).Magnitude() * 3.6)));
    }

    @Override
    public boolean canDisplay(HashMap<UUID, Object> results) {

        if (!results.containsKey(ProcessorID.WINDDRIFT))
            return false;

        return(((Vector)results.get(ProcessorID.WINDDRIFT)).Magnitude() > 0);
    }
}