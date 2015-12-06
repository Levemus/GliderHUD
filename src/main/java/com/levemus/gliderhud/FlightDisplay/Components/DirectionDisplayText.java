package com.levemus.gliderhud.FlightDisplay.Components;

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
import android.widget.TextView;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;

/**
 * Created by markcarter on 15-12-01.
 */
public class DirectionDisplayText extends DirectionDisplay{
    private final String TAG = this.getClass().getSimpleName();
    protected TextView mTextView = null;

    protected DirectionDisplayText() {}
    public void init(Activity activity, IFlightDataBroadcaster broadcaster) {}

    public DirectionDisplayText(TextView view)
    {
        mTextView = view;
    }

    @Override
    public void init(Activity activity) {}

    @Override
    public void display()
    {
        double angle = (mImageOffset != null ? mImageOffset.deltaAngle(mCurrentDirection) : mCurrentDirection);
        mTextView.setX((float)getScreenLocation(-angle).X());
    }

    public void setText(String value)
    {
        mTextView.setText(value);
    }
}
