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
import android.util.Log;
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.R;
import com.levemus.gliderhud.Types.Point;
import com.levemus.gliderhud.Utils.Angle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-29.
 */

// this class is only applicable within the context of the compass display
class HeadingDisplay extends CompassSubDisplay {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public HashSet<UUID> processorIDs() {
        return new HashSet<>();
    }

    public void display(Fragment parent, HashMap<UUID, Object> results) {
        if(mImageView == null)
            mImageView = (ImageView) parent.getActivity().findViewById(R.id.compassBar);
        displayImage(parent, mHeading);
    }

    @Override
    public boolean canDisplay(HashMap<UUID, Object> results) {
        return true;
    }

    @Override
    protected double getScreenLocation(double angle)
    {
        int offset = (mHeading >= 315f && mHeading <= 360) ? -(int) PIXELS_PER_45_DEGREES * 7 : (int) PIXELS_PER_45_DEGREES;
        return (int) (mHeading / 360.0 * (8.0 * PIXELS_PER_45_DEGREES)) + offset;
    }
}

