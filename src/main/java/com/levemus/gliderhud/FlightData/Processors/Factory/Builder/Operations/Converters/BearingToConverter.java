package com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.Converters;

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

import com.levemus.gliderhud.FlightData.Processors.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;

import java.util.HashMap;
import java.util.UUID;


/**
 * Created by mark@levemus on 15-12-26.
 */
public class BearingToConverter implements IConverter {

    private final double INVALID = Double.MIN_VALUE;
    private double mStartLongitude = INVALID;
    private double mStartLatitude = INVALID;

    public BearingToConverter() {}
    public BearingToConverter(double latitude, double longitude) {
        mStartLongitude = longitude;
        mStartLatitude = latitude;
    }

    @Override
    public double convert(HashMap<UUID, Double> values) {
        double latitude = values.get(Channels.LATITUDE);
        double longtitude = values.get(Channels.LONGITUDE);

        if (mStartLongitude == INVALID ||
                mStartLatitude == INVALID) {
            mStartLatitude = latitude;
            mStartLongitude = longtitude;
        }

        Location launchLocation = new Location("initial");
        launchLocation.setLatitude(mStartLatitude);
        launchLocation.setLongitude(mStartLongitude);
        Location currentLocation = new Location("current");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longtitude);
        return((currentLocation.bearingTo(launchLocation) + 720.0 ) % 360.0);
    }
}
