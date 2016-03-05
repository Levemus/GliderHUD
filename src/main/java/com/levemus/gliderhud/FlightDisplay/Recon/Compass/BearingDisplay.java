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
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-29.
 */

// this class is only applicable within the context of the compass display
class BearingDisplay extends CompassSubDisplay {

    @Override
    public HashSet<UUID> processorIDs() {
        return new HashSet<>(Arrays.asList(
                ProcessorID.BEARING,
                ProcessorID.GROUNDSPEED));
    }

    public void display(Fragment parent, HashMap<UUID, Object> results) {
        if(mImageView == null) {
            mImageView = (ImageView) parent.getActivity().findViewById(com.levemus.gliderhud.R.id.bearing_pointer);
            mImageView.setVisibility(View.VISIBLE);
        }
        
        displayImage(parent, (double)results.get(ProcessorID.BEARING));
    }

    private double MIN_GROUND_SPEED = 0.3;

    @Override
    public boolean canDisplay(HashMap<UUID, Object> results) {
        return results.containsKey(ProcessorID.BEARING) && results.containsKey(ProcessorID.GROUNDSPEED) && (Double)results.get(ProcessorID.GROUNDSPEED) > MIN_GROUND_SPEED;
    }
}

