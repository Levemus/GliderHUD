package com.levemus.gliderhud.FlightDisplay.Generic.MFD.Elements;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus on 15-12-17.
 */
public abstract class MFDElement extends FlightDisplay {

    protected FlightDisplay mParentDisplay = null;
    public MFDElement(FlightDisplay parent) {
        mParentDisplay = parent;
    }
    public enum DisplayPriority {
         NONE, LOW, MEDIUM, HIGH, CRITICAL
    };
    public abstract DisplayPriority displayPriority();

    public void onDataReady() {
        if(mParentDisplay != null) {
            mParentDisplay.onDataReady();
        }
    }
}