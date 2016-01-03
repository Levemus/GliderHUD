package com.levemus.gliderhud.FlightDisplay.Recon.Components;

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

import com.levemus.gliderhud.Utils.Angle;

/**
 * Created by mark@levemus on 15-12-01.
 */

public class DirectionDisplayText extends DirectionDisplay{
    private final String TAG = this.getClass().getSimpleName();
    protected TextView mTextView = null;

    public DirectionDisplayText(TextView view)
    {
        mTextView = view;
    }

    @Override
    public void init(Activity activity) {}

    @Override
    public void display()
    {
        double angle = (mImageOffset != null ? Angle.delta(mCurrentDirection, mImageOffset.getParentDirection()) : mCurrentDirection);
        mTextView.setX((float)getScreenLocation(-angle).X());
    }

    public void setText(String value)
    {
        mTextView.setText(value);
    }
}
