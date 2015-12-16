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

import android.view.Window;
import android.app.Activity;

import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;
import com.levemus.gliderhud.FlightDisplay.FlightDisplayListener;
import com.levemus.gliderhud.FlightDisplay.IFlightDisplay;

/**
 * Created by mark@levemus on 15-11-29.
 */
public class ReconFlightDisplay extends FlightDisplayListener {

    private final String TAG = this.getClass().getSimpleName();

    private FlightDisplayListener mListenerDisplays[] =
    {
            new AltitudeDisplay(),
            new CompassDisplay(),
            new GroundSpeedDisplay(),
            new MultiFunctionDisplay(),
    };

    @Override
    public void init(Activity activity)
    {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.setContentView(com.levemus.gliderhud.R.layout.activity_compass);

        for(IFlightDisplay display : mListenerDisplays ) {
            display.init(activity);
        }
    }

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster)
    {
        for(IFlightDataListener listener : mListenerDisplays ) {
            listener.registerWith(broadcaster);
        }
    }

    @Override
    public void display() {}

    @Override
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {}
}
