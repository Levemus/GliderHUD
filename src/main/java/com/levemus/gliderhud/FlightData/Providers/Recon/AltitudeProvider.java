package com.levemus.gliderhud.FlightData.Providers.Recon;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.content.Context;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Configuration.ChannelEntity;
import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.metrics.HUDMetricsID;
import com.reconinstruments.os.metrics.HUDMetricsManager;
import com.reconinstruments.os.metrics.MetricsValueChangedListener;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-11-23.
 */
public class AltitudeProvider extends Provider implements MetricsValueChangedListener, ChannelEntity {

    // logcat class id
    private final String TAG = this.getClass().getSimpleName();
    HUDMetricsManager mHUDMetricsManager = null;


    public void start(Context ctx) {
        try {
            mHUDMetricsManager = (HUDMetricsManager) HUDOS.getHUDService(HUDOS.HUD_METRICS_SERVICE);
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.ALTITUDE_CALIBRATED);
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.ALTITUDE_PRESSURE);

            Log.d(TAG, "Altitude Enabled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long mTimeOfLastUpdate = 0;
    private double mLastAltitude = 0;
    private final double MIN_VARIO_DELTA = 0.01;

    @Override
    public void onMetricsValueChanged(int metricID, float value, long changeTime, boolean isValid) {
        try {
            if (metricID == HUDMetricsID.ALTITUDE_CALIBRATED && isValid == true) {
                HashMap<UUID, Double> values = new HashMap<>();
                values.put(Channels.ALTITUDE, (double) value);
                if(mTimeOfLastUpdate == 0)
                    mTimeOfLastUpdate = changeTime;
                if(changeTime != mTimeOfLastUpdate) {
                    double deltaAltitude = ((double) value - mLastAltitude);
                    double deltaTime = (changeTime - mTimeOfLastUpdate) / 1000000000.0;
                    values.put(Channels.VARIO,
                            deltaAltitude / deltaTime);
                    if (Math.abs(values.get(Channels.VARIO)) < MIN_VARIO_DELTA)
                        values.put(Channels.VARIO, 0.0);
                    mTimeOfLastUpdate = changeTime;
                }

                mClient.onMsg(new DataMessage(id(), new HashSet(values.keySet()), changeTime, values));

                mLastAltitude = value;
            } else if (metricID == HUDMetricsID.ALTITUDE_PRESSURE && isValid == true) {
                HashMap<UUID, Double> values = new HashMap<>();
                values.put(Channels.PRESSUREALTITUDE, (double) value);

                mClient.onMsg(new DataMessage(id(), new HashSet(values.keySet()), changeTime, values));
            }
        }
        catch(Exception e) {}
    }

    public UUID id() {
        return UUID.fromString("b729d92a-2fd8-44e4-bac7-774bddfed681");
    }

    @Override
    public HashSet<UUID> channels() {
        return new HashSet(Arrays.asList(
                Channels.ALTITUDE,
                Channels.VARIO,
                Channels.PRESSUREALTITUDE));
    }
}
