package com.levemus.gliderhud.FlightData.Listeners;

import com.levemus.gliderhud.FlightData.Broadcasters.BroadcasterStatus;
import com.levemus.gliderhud.FlightData.Broadcasters.IFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.FlightDataType;
import com.levemus.gliderhud.FlightData.IFlightData;
import com.levemus.gliderhud.FlightData.IFlightDataClient;
import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Utils.Angle;
import com.levemus.gliderhud.Utils.TaubinNewtonFitCircle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by markcarter on 15-12-20.
 */
public class WindDrift implements IFlightDataListener {

    IFlightDataClient mClient;

    public WindDrift(IFlightDataClient client) {
        mClient = client;
    }

    HashSet<UUID> mSubscriptionFlags = new HashSet(Arrays.asList(
            FlightDataType.GROUNDSPEED,
            FlightDataType.BEARING
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

    private double mDirection = 0;
    private double mSpeed = 0;

    public double speed() {
        return mSpeed;
    }

    public double direction() {
        return mDirection;
    }

    private ArrayList<Vector> mGrndSpdVelocities = new ArrayList<Vector>();
    private int MAX_NUM_GRND_VELOCITIES = 3;
    private double MIN_SEPERATING_ANGLE = 45;
    public void onData(IFlightDataBroadcaster broadcaster, IFlightData data) {
        Vector velocity = new Vector();
        try {
            if (data.get(FlightDataType.GROUNDSPEED) == 0)
                return;
            velocity.SetDirectionAndMagnitude(data.get(
                    FlightDataType.BEARING), data.get(FlightDataType.GROUNDSPEED));

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
            Vector wind = TaubinNewtonFitCircle.FitCircle(mGrndSpdVelocities);
            if(wind != null) {
                mSpeed = wind.Magnitude();
                mDirection = wind.Direction();
            }
        } catch (java.lang.UnsupportedOperationException e) {}

        if(mClient != null)
            mClient.onDataReady();
    }

    @Override
    public void onStatus(IFlightDataBroadcaster broadcaster, BroadcasterStatus status) {}
}
