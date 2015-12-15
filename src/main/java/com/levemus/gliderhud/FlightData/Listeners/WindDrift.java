package com.levemus.gliderhud.FlightData.Listeners;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */


import java.util.ArrayList;
import java.util.EnumSet;

import android.app.Activity;
import android.os.Handler;

import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.Utils.TaubinNewtonFitCircle;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.FlightData.Broadcasters.FlightDataBroadcaster;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class WindDrift extends FlightDataBroadcaster implements IFlightDataListener {

    private final String TAG = this.getClass().getSimpleName();

    private ArrayList<Vector> mGroundVelocities = new ArrayList<Vector>();
    private Handler mWindHandler = null;

    private int MAX_NUM_GRND_VELOCITIES = 3;
    private int WINDDRIFT_SAMPLE_INTERVAL_MS = 10000;

    // IFlightDataBroadcaster
    @Override
    public void init(Activity activity) {
        if(mWindHandler == null) {
            mWindHandler = new Handler();
            mWindHandler.postDelayed(new ProcessWindRunnable(), WINDDRIFT_SAMPLE_INTERVAL_MS);
        }
    }

    @Override
    public void pause(Activity activity) {}

    @Override
    public void resume(Activity activity) {}

    @Override
    public EnumSet<IFlightData.FlightDataType> supportedTypes() {
        return new WindFlightData().supportedTypes();
    }


    // IFlightDataListener
    @Override
    public void onData(IFlightData data)
    {
        Vector velocity = new Vector();
        try {
            if (data.get(IFlightData.FlightDataType.GROUNDSPEED) == 0)
                return;
            velocity.SetDirectionAndMagnitude(data.get(
                    IFlightData.FlightDataType.BEARING), data.get(IFlightData.FlightDataType.GROUNDSPEED));

            synchronized (mGroundVelocities) {
                mGroundVelocities.add(velocity);
                if (mGroundVelocities.size() > MAX_NUM_GRND_VELOCITIES) {
                    mGroundVelocities.remove(0);
                }
            }
        }
        catch(java.lang.UnsupportedOperationException e){}
    }

    EnumSet<IFlightData.FlightDataType> mSubscriptionFlags = EnumSet.of(
            IFlightData.FlightDataType.GROUNDSPEED,
            IFlightData.FlightDataType.BEARING);

    @Override
    public void registerWith(IFlightDataBroadcaster broadcaster)
    {
        if(!mSubscriptionFlags.isEmpty()) {
            EnumSet<IFlightData.FlightDataType> result = broadcaster.addListener(this, WINDDRIFT_SAMPLE_INTERVAL_MS, mSubscriptionFlags);
            mSubscriptionFlags.retainAll(EnumSet.complementOf(result));
        }
    }

    private class ProcessWindRunnable implements Runnable {

        private int WINDDRIFT_CALC_INTERVAL_MS = 30000;
        private double DEGREES_FULL_CIRCLE = 360;
        private int MAX_NUM_WIND_VELOCITIES = 20;
        private double WIND_WEIGHTING_FACTOR = 0.9;

        private ArrayList<Vector> mWindVelocities = new ArrayList<Vector>();

        private Vector WeightedAverage(ArrayList<Vector> values, double r)
        {
            double weightedMagnitude = 0;
            double weightedDirection = 0;

            for(Vector current : values)
            {
                weightedMagnitude +=  r * (current.Magnitude() - weightedMagnitude);
                weightedDirection += r * (current.Direction() - weightedDirection);
            }
            weightedDirection %= DEGREES_FULL_CIRCLE;
            Vector windVelocity = new Vector();
            windVelocity.SetDirectionAndMagnitude(weightedDirection, weightedMagnitude);
            return(windVelocity);
        }

        private void ProcessWind() {
            Vector wind;
            ArrayList<Vector> velocities;
            synchronized (mGroundVelocities) {
                velocities = new ArrayList<Vector>(mGroundVelocities);
            }

            wind = TaubinNewtonFitCircle.FitCircle(velocities);

            if (wind != null) {
                mWindVelocities.add(wind);
                if (mWindVelocities.size() > MAX_NUM_WIND_VELOCITIES)
                    mWindVelocities.remove(0);
                notifyListeners(new WindFlightData(WeightedAverage(mWindVelocities, WIND_WEIGHTING_FACTOR)));
            }
        }

        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mWindHandler.postDelayed(this, WINDDRIFT_CALC_INTERVAL_MS);
            ProcessWind();
        }
    }
}

class WindFlightData implements IFlightData
{
    private Vector mWind;
    public WindFlightData() {} // to get around lack of statics in interfaces while accessing supported types

    public WindFlightData(Vector wind)
    {
        mWind = wind;
    }

    @Override
    public double get(FlightDataType type) throws java.lang.UnsupportedOperationException
    {
        try {
            if (type == FlightDataType.WINDSPEED)
                return mWind.Magnitude();
            if (type == FlightDataType.WINDDIRECTION)
                return mWind.Direction();
        }
        catch(Exception e) {}
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public EnumSet<FlightDataType> supportedTypes() {
        return EnumSet.of(
                IFlightData.FlightDataType.WINDDIRECTION,
                IFlightData.FlightDataType.WINDSPEED);
    }
}
