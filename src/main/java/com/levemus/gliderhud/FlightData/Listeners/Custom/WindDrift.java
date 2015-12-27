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

import com.levemus.gliderhud.FlightData.FlightDataChannel;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;
import com.levemus.gliderhud.FlightData.Listeners.IListenerClients;
import com.levemus.gliderhud.FlightData.Listeners.IListenerConfig;
import com.levemus.gliderhud.FlightData.Listeners.IListenerData;
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
public class WindDrift implements IListenerData, IListenerConfig, IListenerClients {

    private HashSet<IFlightDataClient> mClients;

    @Override
    public HashSet<IFlightDataClient> clients() {return mClients;}

    HashSet<UUID> mRequiredChannels = new HashSet(Arrays.asList(
            FlightDataChannel.GROUNDSPEED,
            FlightDataChannel.BEARING
    ));

    @Override
    public HashSet<UUID> requiredChannels() {
        return mRequiredChannels;
    }

    @Override
    public long notificationInterval() { return 5000; }

    private OffsetCircle mWind;

    private final int MAX_NUM_WIND_RESULT = 3;
    private final double MAX_WIND_RESULT_SPEED_VARIATION = 5;
    private ArrayList<OffsetCircle> mPreviousWindResults = new ArrayList<OffsetCircle>();

    public double speed() {
        if(mWind != null)
            return mWind.mCenterOffset.Magnitude();
        return 0;
    }

    public double direction() {
        if(mWind != null)
            return mWind.mCenterOffset.Direction();
        return 0;
    }

    private ArrayList<Vector> mGrndSpdVelocities = new ArrayList<Vector>();
    private final int MAX_NUM_GRND_VELOCITIES = 3;
    private final double MIN_SEPERATING_ANGLE = 45;

    @Override
    public void onData(HashSet<UUID> channels, IFlightData data) {
        Vector velocity = new Vector();
        try {
            if (data.get(FlightDataChannel.GROUNDSPEED) == 0)
                return;
            velocity.SetDirectionAndMagnitude(data.get(
                    FlightDataChannel.BEARING), data.get(FlightDataChannel.GROUNDSPEED));

            mGrndSpdVelocities.add(velocity);
            if (mGrndSpdVelocities.size() > MAX_NUM_GRND_VELOCITIES) {
                mGrndSpdVelocities.remove(0);
            }

            for (int i = 0; i < mGrndSpdVelocities.size() - 1; i++) {
                for (int j = i + 1; j < mGrndSpdVelocities.size() - 1; j++) {
                    if (Angle.delta(mGrndSpdVelocities.get(i).Direction(),
                            mGrndSpdVelocities.get(j).Direction()) < MIN_SEPERATING_ANGLE) {
                        mGrndSpdVelocities.clear();
                        return;
                    }
                }
            }
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

        for(IFlightDataClient client : mClients)
            client.onDataReady();
    }

    @Override
    public UUID id() { return UUID.fromString("fe351c72-7eea-4b53-94e4-1bb91f78725f");}
}
