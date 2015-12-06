package com.levemus.gliderhud.FlightDisplay.Recon;

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
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightDisplay.FlightDisplayListener;

import java.util.EnumSet;

/**
 * Created by mark@levemus on 15-12-01.
 */
public class MultiFunctionDisplay extends FlightDisplayListener {

    private enum MFD_MODE {
        NONE,
        VARIO,
        GLIDE,
    };

    private double mGlideRatio = 0;
    private double mVario = 0;
    private MFD_MODE mMode = MFD_MODE.NONE;
    private TextView mMFDDisplay = null;
    private TextView mMFDTitle = null;

    private int UPDATE_INTERVAl_MS = 500;

    EnumSet<IFlightData.FlightDataType> mSubscriptionFlags = EnumSet.of(
            IFlightData.FlightDataType.GLIDE,
            IFlightData.FlightDataType.VARIO);

    @Override
    public void init(Activity activity) {
        mMFDTitle = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.mfdTitle);
        mMFDDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.mfdDisplay);
    }

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster) {
        if(!mSubscriptionFlags.isEmpty()) {
            EnumSet<IFlightData.FlightDataType> result = broadcaster.AddListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.retainAll(EnumSet.complementOf(result));
        }
    }

    @Override
    public void display() {}

    @Override
    public void onData(IFlightData data) {
        try {
            mGlideRatio = data.getData(IFlightData.FlightDataType.GLIDE);
            mVario = data.getData(IFlightData.FlightDataType.VARIO);
            determineMode();
            /*
            switch(mMode)
            {
                case(VARIO):
                    mMFDTitle.setText("Climb (m/s)");
                    mMFDDisplay.setText(Double.toString(mVario));
                    break;
                case(GLIDE):
                    mMFDTitle.setText("Glide");
                    mMFDDisplay.setText(Double.toString(mGlideRatio));
                    break;
                default:
                    mMFDTitle.setText("");
                    mMFDDisplay.setText("");
            }
            */
            display();
        }
        catch(java.lang.UnsupportedOperationException e){}
    }

    private void determineMode() {
        if(mVario > 0 || mGlideRatio >= 0) {
            mMode = MFD_MODE.VARIO;
        }
        else {
            mMode = MFD_MODE.GLIDE;
        }
    }
}
