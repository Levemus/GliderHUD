package com.levemus.gliderhud.FlightDisplay;

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

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightDisplay.Generic.AltitudeDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.GroundSpeedDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.MultiFunctionManager;
import com.levemus.gliderhud.FlightDisplay.Recon.Compass.CompassDisplay;

/**
 * Created by mark@levemus on 15-11-29.
 */

public class MainDisplay extends FlightDisplay {

    private final String TAG = this.getClass().getSimpleName();

    private FlightDisplay mDisplays[] =
    {
            new CompassDisplay(),
            new AltitudeDisplay(),
            new GroundSpeedDisplay(),
            new MultiFunctionManager(),
    };

    @Override
    public void init(Activity activity)
    {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.setContentView(com.levemus.gliderhud.R.layout.activity_compass);

        for(IFlightDisplay display : mDisplays ) {
            display.init(activity);
        }
    }

    @Override
    public void deInit(Activity activity)
    {
        for(IFlightDisplay display : mDisplays ) {
            display.deInit(activity);
        }
    }

    @Override
    public void registerProvider(IChannelDataProvider provider)
    {
        for(FlightDisplay display : mDisplays ) {
            display.registerProvider(provider);
        }
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider)
    {
        for(FlightDisplay display : mDisplays ) {
            display.deRegisterProvider(provider);
        }
    }
}
