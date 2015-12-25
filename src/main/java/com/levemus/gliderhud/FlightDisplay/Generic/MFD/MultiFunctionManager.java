package com.levemus.gliderhud.FlightDisplay.Generic.MFD;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.app.Activity;
import android.widget.RelativeLayout;
import android.view.View;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.ClimbRateDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.DistanceFrLaunchDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.GlideRatioDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.HeightAbvLaunchDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.MFDElement;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class MultiFunctionManager extends FlightDisplay {

    MFDElement[] mDisplayElements = new MFDElement[] {
            new ClimbRateDisplay(this),
            new GlideRatioDisplay(this),
            new HeightAbvLaunchDisplay(this),
            new DistanceFrLaunchDisplay(this)
    };

    private RelativeLayout mMFDDisplay;

    @Override
    public void init(Activity activity) {
        mMFDDisplay = (RelativeLayout) activity.findViewById(com.levemus.gliderhud.R.id.mfd);
        for(MFDElement element : mDisplayElements) {
            element.init(activity);
        }
    }

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster) {
        for(MFDElement element : mDisplayElements) {
            element.registerWith(broadcaster);
        }
    }

    private ArrayList<MFDElement> mDisplayQueue = new ArrayList<MFDElement>();
    private long mCurrentDisplayStart = 0;
    private long DISPLAY_TIME_SLICE = 10 * 1000; // ms
    private long DISPLAY_FADE_OUT = 5 * 1000; // ms

    @Override
    public void display() {
        long currentTime = new Date().getTime();

        if(mCurrentDisplayStart == 0)
            mCurrentDisplayStart = currentTime;

        for(MFDElement element : mDisplayElements)
            element.hide();

        determineDisplayElement();

        if(mDisplayQueue.size() > 0)
            mDisplayQueue.get(0).display();
    }

    private void determineDisplayElement() {
        MFDElement.DisplayPriority priority = displayPriority();
        long currentTime = new Date().getTime();

        if(priority == MFDElement.DisplayPriority.NONE) {
            mDisplayQueue.clear();
            mCurrentDisplayStart = currentTime;
            mMFDDisplay.setVisibility(View.GONE);
            return;
        }

        mMFDDisplay.setVisibility(View.VISIBLE);
        ArrayList<MFDElement> displayQueue = new ArrayList<>();

        if(mDisplayQueue.size() > 0 && mDisplayQueue.get(0).displayPriority() != MFDElement.DisplayPriority.NONE)
            displayQueue.add(mDisplayQueue.get(0));

        for(MFDElement element : mDisplayQueue) {
            if(element.displayPriority().ordinal() >= priority.ordinal()
                    && !displayQueue.contains(element))
                displayQueue.add(element);
        }

        for(MFDElement element : mDisplayElements) {
            if(element.displayPriority().ordinal() >= priority.ordinal()
                    && !displayQueue.contains(element))
                displayQueue.add(element);
        }

        if(displayQueue.size() > 1 &&
                ((displayQueue.get(0).displayPriority().ordinal() < priority.ordinal() &&
                        ((currentTime - mCurrentDisplayStart) > DISPLAY_FADE_OUT))
                || (currentTime - mCurrentDisplayStart) > DISPLAY_TIME_SLICE))
        {
            displayQueue.remove(0);
            mCurrentDisplayStart = currentTime;
        }

        if(displayQueue.size() == 1)
            mCurrentDisplayStart = currentTime;

        mDisplayQueue = displayQueue;
    }

    private MFDElement.DisplayPriority displayPriority() {
        MFDElement.DisplayPriority priority = MFDElement.DisplayPriority.NONE;
        for(MFDElement element : mDisplayElements) {
            if(element.displayPriority().ordinal() > priority.ordinal())
                priority = element.displayPriority();
        }
        return priority;
    }
}
