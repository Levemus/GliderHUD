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
import android.widget.TextView;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Listeners.TurnRate;
import com.levemus.gliderhud.FlightData.Listeners.Glide;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-18.
 */
public class GlideRatioDisplay extends MFDElement {
    private final String TAG = this.getClass().getSimpleName();

    public GlideRatioDisplay(FlightDisplay parent) {
        super(parent);
    }

    private TurnRate mTurnRate = new TurnRate(this);
    private Glide mGlide = new Glide(this);

    private String title() {return "Glide";}
    private String value() {
        double displayGlide = Math.round(mGlide.value() * 100);
        displayGlide /= 100;
        return Double.toString(displayGlide);
    }

    private TextView mMFDDisplay = null;
    private TextView mMFDTitle = null;

    public void init(Activity activity) {
        mMFDTitle = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.mfdTitle);
        mMFDDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.mfdDisplay);
    }

    @Override
    public void display() {
        mMFDTitle.setText(title());
        mMFDDisplay.setText(value());
    }

    @Override
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster)
    {
        HashSet<UUID> result = new HashSet<>();
        result.addAll(mTurnRate.registerWith(broadcaster));
        result.addAll(mGlide.registerWith(broadcaster));

        return result;
    }

    private double MIN_GLIDE = -0.01;
    private double CRITICAL_GLIDE = -3.0;
    private double MAX_TURN_RATE = 10;

    @Override
    public MFDElement.DisplayPriority displayPriority() {
        if(mGlide.value() >= MIN_GLIDE || mTurnRate.value() > MAX_TURN_RATE)
            return MFDElement.DisplayPriority.NONE;
        else if(mGlide.value() >= CRITICAL_GLIDE &&  mTurnRate.value() < MAX_TURN_RATE)
            return MFDElement.DisplayPriority.CRITICAL;
        else
            return MFDElement.DisplayPriority.MEDIUM;
    }
}
