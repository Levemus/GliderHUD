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

import android.location.Location;
import android.os.Environment;
import android.os.Handler;

import com.levemus.gliderhud.FlightData.Configuration.ChannelEntity;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.BearingToConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.DistanceFromConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.IProcessor;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Types.Waypoint;
import com.levemus.gliderhud.Utils.WaypointReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


/**
 * Created by mark@levemus on 15-12-20.
 */
public class Turnpoint extends Processor<Vector> implements IProcessor<Vector>, ChannelEntity {

    private final String TAG = this.getClass().getSimpleName();

    // IConfiguration
    HashSet<UUID> mChannels = new HashSet(Arrays.asList(
            Channels.LATITUDE,
            Channels.LONGITUDE));

    @Override
    public HashSet<UUID> channels() {
        return mChannels;
    }

    @Override
    public UUID id() { return ProcessorID.TURNPOINT; }

    private List<Waypoint> mWaypoints = new ArrayList<>();
    private String mWaypointPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/waypoints.txt";
    private Date mLastModifiedDate = new Date(0);
    private int mWaypointIndex = 0;

    private Handler mLocalHandler;

    private final int OPTIMIZE_PERIOD = 60 * 1000; // ms

    public void process() {

        // update waypoints list
        try {
            File waypointFile = new File(mWaypointPath);
            if(waypointFile == null)
                return;
            Date lastModified = new Date(waypointFile.lastModified());
            if(mLastModifiedDate.before(lastModified)) {
                mLastModifiedDate = lastModified;
                mWaypoints = WaypointReader.load(mWaypointPath);
                mLocalHandler = new Handler();
                Runnable dataGenerator = new Runnable() {
                    public void run() {
                        optimize();
                        mLocalHandler.postDelayed(this, OPTIMIZE_PERIOD);
                    }
                };
                mLocalHandler.postDelayed(dataGenerator, OPTIMIZE_PERIOD);
            }

            for (Iterator<Waypoint> iterator = mWaypoints.iterator(); iterator.hasNext(); ) {
                Waypoint wayPoint = iterator.next();
                DistanceFromConverter distanceFrom = new DistanceFromConverter(
                        wayPoint.mLatitude, wayPoint.mLongitude
                );

                Double distanceFr = distanceFrom.convert(mCache.data(this));
                if(distanceFr < wayPoint.mRadius) {
                    iterator.remove();
                    optimize();
                    mWaypointIndex++;
                }
                else
                    break;
            }
            if(mWaypoints.isEmpty()) {
                mLocalHandler.removeCallbacksAndMessages(null);
            }
        } catch(Exception e){}


        try {
            if(mWaypoints.isEmpty())
                mValue = invalid();

            Waypoint wayPoint = mWaypoints.get(0);
            DistanceFromConverter distanceFrom = new DistanceFromConverter(
                    wayPoint.mLatitude, wayPoint.mLongitude
            );

            BearingToConverter bearingTo = new BearingToConverter(
                    wayPoint.mLatitude, wayPoint.mLongitude
            );

            Double distance = distanceFrom.convert(mCache.data(this));
            Double bearing = bearingTo.convert(mCache.data(this));
            mValue = new Vector();
            mValue.setDirectionAndMagnitude(bearing, distance);

            if(mValue != null && hasChanged()) {
                mLastValue = mValue;
            }
        } catch(Exception e) {}
    }

    @Override
    public Vector invalid() { return null; }

    @Override
    public boolean isValid(Vector value) { return value != null; }

    protected boolean hasChanged() {
        return (mLastValue == null || ((mLastValue.Direction() == mValue.Direction())
                        && (mLastValue.Magnitude() == mValue.Magnitude())));
    }

    @Override
    public long refreshPeriod() { return 5000; }

    public int currentIndex() {
        if(mWaypoints.size() < 2) // goal
            return -1;

        return mWaypointIndex;
    }


    private void updateDistances() {
        Waypoint previous =  null;
        for(Waypoint waypoint : mWaypoints) {
            waypoint.updateDistance(previous);
            previous = waypoint;
        }
    }

    HashMap<Waypoint, HashMap<Double, Location>> mWapointRadiasPoints = new HashMap<>(); // result cache

    private Location findPointOnCylinder(Waypoint waypoint, Double radian) {

        if(!mWapointRadiasPoints.containsKey(waypoint))
            mWapointRadiasPoints.put(waypoint, new HashMap<Double, Location>());

        if(mWapointRadiasPoints.get(waypoint).containsKey(radian))
            return mWapointRadiasPoints.get(waypoint).get(radian);

        double lat = waypoint.mTurnPoint.mLatitude * (Math.PI / 180);

        double radius = (waypoint.mRadius / 1852.0) / (180.0 * 60.0 / Math.PI); // to nm to radians
        double latPartA = Math.sin(lat) * Math.cos(radius);
        double latPartB = Math.cos(lat) * Math.sin(radius) * Math.cos(radian);
        double newLat = Math.asin(latPartA + latPartB);

        double lng = waypoint.mTurnPoint.mLongitude * (Math.PI / 180);
        double newLong = lng;
        if(Math.cos(newLat)!=0) {
            double longPartA = Math.asin((Math.sin(radian) * Math.sin(radius) / Math.cos(newLat))) + Math.PI;
            double longPartB = (2 * Math.PI);
            newLong = (lng - longPartA) % longPartB;
            newLong -= (newLong > 0 ? Math.PI : -Math.PI);
        }

        newLat *= (180/Math.PI);
        newLong *= (180/Math.PI);

        Location loc = new Location("waypoint");
        loc.setLatitude(newLat);
        loc.setLongitude(newLong);
        mWapointRadiasPoints.get(waypoint).put(radian, loc);

        return mWapointRadiasPoints.get(waypoint).get(radian);
    }

    private final double RADIAN_INCREMENT = 0.01;
    private void optimize()
    {
        boolean notDone = true;

        while(notDone) {
            double smallestDistance = mWaypoints.get(mWaypoints.size() - 1).mDistance;
            notDone = false;
            for (Waypoint waypoint : mWaypoints) {
                if(mWaypoints.indexOf(waypoint) != mWaypoints.size() - 1) {
                    Location smallestPoint= new Location("smallest");
                    smallestPoint.setLatitude(waypoint.mLatitude);
                    smallestPoint.setLongitude(waypoint.mLongitude);

                    for (double radian = 0; radian < Math.PI; radian += RADIAN_INCREMENT) {
                        Location point = findPointOnCylinder(waypoint, radian);
                        waypoint.mLatitude = point.getLatitude();
                        waypoint.mLongitude = point.getLongitude();
                        updateDistances();
                        double totalDistance = mWaypoints.get(mWaypoints.size() - 1).mDistance;
                        if (totalDistance < smallestDistance) {
                            smallestDistance = totalDistance;
                            smallestPoint = point;
                            notDone = true;
                        }
                    }
                    waypoint.mLatitude = smallestPoint.getLatitude();
                    waypoint.mLongitude = smallestPoint.getLongitude();
                    updateDistances();
                }
            }
        }
    }

}
