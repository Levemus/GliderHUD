package com.levemus.gliderhud.Utils;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import java.util.Queue;
import java.util.LinkedList;

import android.location.Location;

import com.levemus.gliderhud.Types.Vector;

/**
 * Created by mark@levemus on 15-12-29.
 */

public class WaypointManager {
    private class Turnpoint {
        Double mLongitude;
        Double mLatitude;
        Double mRadius;

        public Turnpoint(Double longtitude, Double latitude, Double radius){
            mLongitude = longtitude;
            mLatitude = latitude;
            mRadius = radius;
        }
    }

    Queue<Turnpoint> mTurnpoints = new LinkedList<>();

    public void add(Double longitude, Double latitude, Double radius)
    {
        mTurnpoints.add(new Turnpoint(longitude, latitude, radius));
    }

    public Vector getBearingAndDistance(Double currentLongitude, Double currentLatitude) {
        if(mTurnpoints.isEmpty())
            return null;

        Vector bearingAndDistance = new Vector();

        Location currentLocation = new Location("initial");
        currentLocation.setLatitude(currentLatitude);
        currentLocation.setLongitude(currentLongitude);
        Location targetLocation = new Location("target");
        targetLocation.setLatitude(mTurnpoints.peek().mLatitude);
        targetLocation.setLongitude(mTurnpoints.peek().mLongitude);
        Double distance = (double)currentLocation.distanceTo(targetLocation);
        Double bearing = (currentLocation.bearingTo(targetLocation) * 180) / Math.PI;
        bearingAndDistance.SetDirectionAndMagnitude(bearing, distance);

        if(distance < mTurnpoints.peek().mRadius)
            mTurnpoints.remove();

        return bearingAndDistance;
    }

}
