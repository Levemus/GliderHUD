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

import android.app.Activity;

import com.levemus.gliderhud.FlightData.Listeners.IListener;
import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightDisplay.Generic.AltitudeDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.BatteryDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.FlightTimeDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.GroundSpeedDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.MFD.MultiFunctionDisplay;
import com.levemus.gliderhud.FlightDisplay.Generic.Map.MapDisplay;
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
            new FlightTimeDisplay(),
            new BatteryDisplay(),
            new MultiFunctionDisplay(),
            new MapDisplay()
    };

    @Override
    public void init(Activity activity)
    {
        for(IListener listener : mDisplays ) {
            listener.init(activity);
        }
    }

    @Override
    public void deInit(Activity activity)
    {
        for(IListener listener : mDisplays ) {
            listener.deInit(activity);
        }
    }

    @Override
    public void registerProvider(IChannelDataSource provider)
    {
        for(FlightDisplay display : mDisplays ) {
            display.registerProvider(provider);
        }
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider)
    {
        for(FlightDisplay display : mDisplays ) {
            display.deRegisterProvider(provider);
        }
    }
}
