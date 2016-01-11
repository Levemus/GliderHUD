package com.levemus.gliderhud.FlightData.Processors.Custom;

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
import java.util.HashMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.FlightData.Processors.IProcessor;
import com.levemus.gliderhud.Types.OffsetCircle;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.TaubinNewtonFitCircle;



/**
 * Created by mark@levemus on 15-12-20.
 */
public class WindDrift extends Processor<Vector> implements IProcessor, IIdentifiable, IChannelized {

    private final String TAG = this.getClass().getSimpleName();

    // IConfiguration
    HashSet<UUID> mChannels = new HashSet(Arrays.asList(
            Channels.GROUNDSPEED,
            Channels.BEARING));
    @Override
    public HashSet<UUID> channels() {
        return mChannels;
    }

    @Override
    public UUID id() { return UUID.fromString("fe351c72-7eea-4b53-94e4-1bb91f78725f"); }

    // IMessageNotify
    private HashMap<Double, Vector> mGrndSpdVelocities = new HashMap<>();

    private final double MIN_SEPERATING_ANGLE = 30;
    private final double MIN_NUM_ANGLES = (0.75 * (360 / MIN_SEPERATING_ANGLE));
    private ArrayList<OffsetCircle> mResults = new ArrayList<>();
    private final int MAX_RESULTS = 5;
    private double MAX_DELTA_AIRSPEED = 3.0;

    public void process() {
        Vector velocity = new Vector();
        try {
            HashMap<UUID, Double> values = mProvider.get(this);
            if (values.get(Channels.GROUNDSPEED) == 0)
                return;

            velocity.SetDirectionAndMagnitude(
                    values.get(Channels.BEARING),
                    values.get(Channels.GROUNDSPEED));
            mGrndSpdVelocities.put(velocity.Direction() / MIN_SEPERATING_ANGLE, velocity);
            if(mGrndSpdVelocities.size() < MIN_NUM_ANGLES) {
                return;
            }

            OffsetCircle result = TaubinNewtonFitCircle.FitCircle(new ArrayList<>(mGrndSpdVelocities.values()));
            mResults.add(result);
            if(mResults.size() > MAX_RESULTS)
                mResults.remove(0);
            else {
                return;
            }

            for(OffsetCircle circle : mResults) {
                if(Math.abs(circle.mRadius - result.mRadius) > MAX_DELTA_AIRSPEED)
                    return;
            }
            if(result != null) {
                mValue = result.mCenterOffset;
            }
        } catch (Exception e) {}

        if(mValue != null && hasChanged()) {
            mLastValue = mValue;
        }
    }

    @Override
    public Vector invalid() { return null; }

    @Override
    public boolean isValid() { return mValue != null; }

    protected boolean hasChanged() {
        return (mLastValue == null || ((mLastValue.Direction() == mValue.Direction())
                        && (mLastValue.Magnitude() == mValue.Magnitude())));
    }

    @Override
    public long refreshPeriod() { return 20000; }
}
