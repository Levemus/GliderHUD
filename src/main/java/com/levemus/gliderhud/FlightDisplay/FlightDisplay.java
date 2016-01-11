package com.levemus.gliderhud.FlightDisplay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;

/**
 * Created by mark@levemus on 15-12-20.
 */

public abstract class FlightDisplay implements IFlightDisplay {

    protected Context mContext;
    private Handler handler = new Handler();
    @Override
    public void init(final Activity activity) {
        mContext = activity;
        if(refreshPeriod() != Integer.MAX_VALUE) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            display(activity);
                        }});
                    handler.postDelayed(this, refreshPeriod());
                }
            }, refreshPeriod());
        }
    }

    @Override
    public void deInit(Activity activity) {
        handler.removeCallbacksAndMessages(null);
        mContext = null;
    }

    @Override
    public void display(Activity activity) {}

    @Override
    public void hide() {}

    @Override
    public void registerProvider(IChannelDataSource provider) {}

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {}

    protected int refreshPeriod() { return 500; } // ms
}
