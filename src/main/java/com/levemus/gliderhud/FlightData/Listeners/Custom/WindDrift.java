package com.levemus.gliderhud.FlightData.Listeners.Custom;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Listeners.Listener;
import com.levemus.gliderhud.FlightData.Messages.IMessage;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
import com.levemus.gliderhud.FlightData.Messages.Data.DataMessage;
import com.levemus.gliderhud.FlightDisplay.IClient;
import com.levemus.gliderhud.FlightData.Messages.IMessageNotify;
import com.levemus.gliderhud.FlightData.Listeners.IListener;
import com.levemus.gliderhud.FlightData.Configuration.IConfiguration;
import com.levemus.gliderhud.FlightData.Messages.Status.ChannelStatus;
import com.levemus.gliderhud.FlightData.Messages.Status.StatusMessage;
import com.levemus.gliderhud.Types.OffsetCircle;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.Angle;
import com.levemus.gliderhud.Utils.TaubinNewtonFitCircle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class WindDrift extends Listener <Vector> implements IMessageNotify, IConfiguration, IListener {

    // IConfiguration
    HashSet<UUID> mChannels = new HashSet(Arrays.asList(
            MessageChannels.GROUNDSPEED,
            MessageChannels.BEARING));
    @Override
    public HashSet<UUID> allChannels() {
        return mChannels;
    }

    HashSet<UUID> mOrphanedChannels = new HashSet(mChannels);
    @Override
    public HashSet<UUID> orphanedChannels() {
        return mOrphanedChannels;
    }

    @Override
    public UUID id() { return UUID.fromString("fe351c72-7eea-4b53-94e4-1bb91f78725f"); }

    @Override
    public long notificationInterval() {
        if(mWind != null) {
            return 60000;
        }
        return 20000;
    }

    // IMessageNotify

    private ArrayList<Vector> mGrndSpdVelocities = new ArrayList<Vector>();
    private final int MAX_NUM_GRND_VELOCITIES = 2;
    private final double MIN_SEPERATING_ANGLE = 45;

    protected void onData(IConfiguration config, DataMessage data) {
        Vector velocity = new Vector();
        try {
            if (data.get(MessageChannels.GROUNDSPEED) == 0)
                return;
            velocity.SetDirectionAndMagnitude(data.get(
                    MessageChannels.BEARING), data.get(MessageChannels.GROUNDSPEED));

            mGrndSpdVelocities.add(velocity);
            if (mGrndSpdVelocities.size() > MAX_NUM_GRND_VELOCITIES) {
                mGrndSpdVelocities.remove(0);
            }
            int count = mGrndSpdVelocities.size();
            for( int i = count - 1; i > 0; i--) {
                if (Angle.delta(mGrndSpdVelocities.get(0).Direction(),
                        mGrndSpdVelocities.get(1).Direction()) < MIN_SEPERATING_ANGLE)
                    mGrndSpdVelocities.remove(0);
            }

            if(mGrndSpdVelocities.size() < 3)
                return;

            OffsetCircle result = TaubinNewtonFitCircle.FitCircle(mGrndSpdVelocities);
            if(result != null) {
                mPreviousWindResults.add(result);
                if(mPreviousWindResults.size() > 1) {
                    if(mPreviousWindResults.size() > MAX_NUM_WIND_RESULT)
                        mPreviousWindResults.remove(0);
                    for(OffsetCircle previous : mPreviousWindResults){
                        if(Math.abs(previous.mRadius - result.mRadius) > MAX_WIND_RESULT_SPEED_VARIATION)
                            return;
                    }
                }

                if(mPreviousWindResults.size() == MAX_NUM_GRND_VELOCITIES)
                    mWind = result;
            }
        } catch (java.lang.UnsupportedOperationException e) {}

        if(mClient != null)
            mClient.onDataReady();
    }

    // Value
    private OffsetCircle mWind;

    private final int MAX_NUM_WIND_RESULT = 3;
    private final double MAX_WIND_RESULT_SPEED_VARIATION = 5;
    private ArrayList<OffsetCircle> mPreviousWindResults = new ArrayList<OffsetCircle>();

    public Vector invalid() { return null; }
    public Vector value() { return mWind.mCenterOffset; }
}
