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
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.ClimbRateDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.GlideRatioDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements.MFDElement;

import java.util.Date;
import java.util.ArrayList;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class MultiFunctionDisplay extends FlightDisplay {

    MFDElement[] mDisplayElements = new MFDElement[] {
            new ClimbRateDisplay(this),
            new GlideRatioDisplay(this),
    };

    private TextView mVarioGlideDisplay;

    @Override
    public void init(Activity activity) {
        for(MFDElement element : mDisplayElements) {
            element.init(activity);
        }
        super.init(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider) {
        for(MFDElement element : mDisplayElements) {
            element.registerProvider(provider);
        }
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {
        for(MFDElement element : mDisplayElements) {
            element.deRegisterProvider(provider);
        }
    }

    private ArrayList<MFDElement> mDisplayQueue = new ArrayList<MFDElement>();
    private long mCurrentDisplayStart = 0;

    @Override
    public void display(Activity activity) {
        long currentTime = new Date().getTime();

        if(mCurrentDisplayStart == 0)
            mCurrentDisplayStart = currentTime;

        for(MFDElement element : mDisplayElements)
            element.hide();

        determineDisplayElement();

        if(mDisplayQueue.size() > 0)
            mDisplayQueue.get(0).display(activity);
    }

    private void determineDisplayElement() {
        MFDElement.DisplayPriority priority = displayPriority();
        long currentTime = new Date().getTime();

        if(priority == MFDElement.DisplayPriority.NONE) {
            mDisplayQueue.clear();
            mCurrentDisplayStart = currentTime;
            return;
        }

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
                (currentTime - mCurrentDisplayStart) > displayQueue.get(0).displayDuration())
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

    protected int refreshPeriod() {
        if(!mDisplayQueue.isEmpty())
            return mDisplayQueue.get(0).refreshPeriod();
        else
            return 500;
    } // ms
}
