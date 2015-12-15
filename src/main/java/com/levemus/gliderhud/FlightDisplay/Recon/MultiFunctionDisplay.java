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
import com.levemus.gliderhud.FlightData.Listeners.Vario;

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
    private double mVarioAvg = 0;
    private MFD_MODE mMode = MFD_MODE.NONE;
    private TextView mMFDDisplay = null;
    private TextView mMFDTitle = null;

    private Vario mVario;
    private int UPDATE_INTERVAl_MS = 500;

    EnumSet<IFlightData.FlightDataType> mSubscriptionFlags = EnumSet.of(
            IFlightData.FlightDataType.GLIDE,
            IFlightData.FlightDataType.VARIO);

    @Override
    public void init(Activity activity) {
        mMFDTitle = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.mfdTitle);
        mMFDDisplay = (TextView) activity.findViewById(com.levemus.gliderhud.R.id.mfdDisplay);
        mVario = new Vario();
        mVario.init(activity);
    }

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster) {
        mVario.registerWith(broadcaster);
        if(!mSubscriptionFlags.isEmpty()) {
            EnumSet<IFlightData.FlightDataType> result = mVario.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.retainAll(EnumSet.complementOf(result));
            result = broadcaster.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.retainAll(EnumSet.complementOf(result));
        }
    }

    @Override
    public void display() {}

    @Override
    public void onData(IFlightData data) {
        try {
            mGlideRatio = data.get(IFlightData.FlightDataType.GLIDE);
        } catch(java.lang.UnsupportedOperationException e){}
        try {
            mVarioAvg = data.get(IFlightData.FlightDataType.VARIO);
            mMFDTitle.setText("Climb (m/s)");
            mMFDDisplay.setText(Double.toString(mVarioAvg));
        } catch(java.lang.UnsupportedOperationException e){}
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

    private void determineMode() {
        if(mVarioAvg > 0 || mGlideRatio >= 0) {
            mMode = MFD_MODE.VARIO;
        }
        else {
            mMode = MFD_MODE.GLIDE;
        }
    }
}
