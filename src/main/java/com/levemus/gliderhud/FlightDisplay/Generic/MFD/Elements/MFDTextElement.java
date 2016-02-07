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

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

/**
 * Created by mark@levemus  on 15-12-22.
 */

public abstract class MFDTextElement extends MFDElement {

    private final String TAG = this.getClass().getSimpleName();

    public MFDTextElement(FlightDisplay parent) {
        super(parent);
    }

    protected String value() {return "";}

    @Override
    public void display(Activity activity) {
        mVarioGlideDisplay.setVisibility(View.VISIBLE);
        try {
            mVarioGlideDisplay.setText(value());
        } catch(Exception e) {
            mVarioGlideDisplay.setText("");
        }
    }

    @Override
    public void hide() {
        mVarioGlideDisplay.setVisibility(View.GONE);
    }

    private TextView mVarioGlideDisplay = null;

    @Override
    public void init(Activity activity) {
        mVarioGlideDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.varioGlideDisplay);
    }
}
