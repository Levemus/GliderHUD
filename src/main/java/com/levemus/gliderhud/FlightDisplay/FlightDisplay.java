package com.levemus.gliderhud.FlightDisplay;

import android.app.Activity;
import android.os.Handler;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;

/**
 * Created by mark@levemus on 15-12-20.
 */

public abstract class FlightDisplay implements IFlightDisplay {

    Handler handler = new Handler();
    @Override
    public void init(Activity activity) {
        if(refreshPeriod() != Integer.MAX_VALUE) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    display();
                    handler.postDelayed(this, refreshPeriod());
                }
            }, refreshPeriod());
        }
    }

    @Override
    public void deInit(Activity activity) {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void display() {}

    @Override
    public void hide() {}

    @Override
    public void registerProvider(IChannelDataProvider provider) {}

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {}

    protected int refreshPeriod() { return 500; } // ms
}
