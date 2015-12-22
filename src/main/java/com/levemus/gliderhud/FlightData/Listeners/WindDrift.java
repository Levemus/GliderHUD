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

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataID;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;
import com.levemus.gliderhud.Types.OffsetCircle;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.Angle;
import com.levemus.gliderhud.Utils.TaubinNewtonFitCircle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-20.
 */
public class WindDrift implements IFlightDataListener {

    IFlightDataClient mClient;

    public WindDrift(IFlightDataClient client) {
        mClient = client;
    }

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataID.GROUNDSPEED,
            FlightDataID.BEARING
    ));

    private int UPDATE_INTERVAl_MS = 5000;
    public HashSet<UUID> registerWith(IFlightDataBroadcaster broadcaster) {
        HashSet<UUID> result = new HashSet<>();
        if(!mSubscriptionFlags.isEmpty()) {
            result = broadcaster.addListener(this, UPDATE_INTERVAl_MS, mSubscriptionFlags);
            mSubscriptionFlags.removeAll(result);
        }

        return result;
    }

    private OffsetCircle mWind;

    private int MAX_NUM_WIND_RESULT = 3;
    private double MAX_WIND_RESULT_SPEED_VARIATION = 5;
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
    private int MAX_NUM_GRND_VELOCITIES = 3;
    private double MIN_SEPERATING_ANGLE = 45;
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        Vector velocity = new Vector();
        try {
            if (data.get(FlightDataID.GROUNDSPEED) == 0)
                return;
            velocity.SetDirectionAndMagnitude(data.get(
                    FlightDataID.BEARING), data.get(FlightDataID.GROUNDSPEED));

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

        if(mClient != null)
            mClient.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, HashMap<UUID, BroadcasterStatus.Status> status) {}
}
