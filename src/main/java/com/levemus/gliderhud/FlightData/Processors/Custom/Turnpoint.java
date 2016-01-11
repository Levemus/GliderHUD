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

import android.os.Environment;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.BearingToConverter;
import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters.DistanceFromConverter;
import com.levemus.gliderhud.FlightData.Processors.IProcessor;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Types.OffsetCircle;
import com.levemus.gliderhud.Types.Vector;
import com.levemus.gliderhud.Types.Waypoint;
import com.levemus.gliderhud.Utils.TaubinNewtonFitCircle;
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
public class Turnpoint extends Processor<Vector> implements IProcessor, IIdentifiable, IChannelized {

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
    public UUID id() { return UUID.fromString("aaca3a08-c55c-4331-8a7f-8ba0ff8c351e"); }

    private List<Waypoint> mWaypoints = new ArrayList<>();
    private String mWaypointPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/waypoints.txt";
    private Date mLastModifiedDate = new Date(0);

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
            }

            for (Iterator<Waypoint> iterator = mWaypoints.iterator(); iterator.hasNext(); ) {
                Waypoint wayPoint = iterator.next();
                DistanceFromConverter distanceFrom = new DistanceFromConverter(
                        wayPoint.mLatitude, wayPoint.mLongitude
                );

                Double distanceFr = distanceFrom.convert(mProvider.get(this));
                if(distanceFr < wayPoint.mRadius) {
                    iterator.remove();
                }
                else
                    break;
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

            Double distance = distanceFrom.convert(mProvider.get(this));
            Double bearing = bearingTo.convert(mProvider.get(this));
            mValue = new Vector();
            mValue.SetDirectionAndMagnitude(bearing, distance);
            if(mValue != null && hasChanged()) {
                mLastValue = mValue;
            }
        } catch(Exception e) {}
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
    public long refreshPeriod() { return 5000; }
}
