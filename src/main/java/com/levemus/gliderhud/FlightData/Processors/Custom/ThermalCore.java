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
import android.os.AsyncTask;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.IProcessor;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Types.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by mark@levemus on 16-01-30.
 */
public class ThermalCore extends Processor<Vector> implements IProcessor, IIdentifiable, IChannelized {

    private final String TAG = this.getClass().getSimpleName();

    // IConfiguration
    List<UUID> mProcessorIDs= new ArrayList<>(Arrays.asList(
            ProcessorID.VARIO,
            ProcessorID.LATITUDE,
            ProcessorID.LONGITUDE,
            ProcessorID.TURNRATE,
            ProcessorID.BEARING,
            ProcessorID.ALTITUDE));

    HashMap<UUID, Processor<Double>> mProcessors = new HashMap<UUID, Processor<Double>>();

    @Override
    public HashSet<UUID> channels() {
        return new HashSet<UUID>();
    }

    @Override
    public UUID id() { return UUID.fromString("01292183-7785-4558-9663-eb7edcb451c1"); }

    private class ThermalSample {
        public Location mLocation;
        public Double mVario;

        public ThermalSample(Location location, Double vario) {
            mLocation = location;
            mVario = vario;
        }
    }

    private class ThermalCentroid {
        public Location mLocation = null;
        public Double mVario = -1 * Double.MAX_VALUE;
        private final int MIN_SAMPLE_SIZE = 10; // count

        private HashMap<Double, ThermalSample> mSamples = new HashMap<>();

        public boolean add(ThermalSample sample) {
            Double altitude = (double)Math.round(sample.mLocation.getAltitude());
            if(!mSamples.containsKey(altitude) || mSamples.get(altitude).mVario < sample.mVario) {
                mSamples.put(altitude, sample);
            }

            if (mSamples.size() >= MIN_SAMPLE_SIZE) {
                process();
                prune();
                return true;
            }

            return false;
        }

        public boolean isValid() {
            return (mLocation != null);
        }

        private Double MAX_SAMPLE_DISTANCE = 200.0; // m
        private long MAX_SAMPLE_AGE = 60000; // ms
        private void prune() {
            Location currentLoc = new Location("Current");
            currentLoc.setLatitude(mProcessors.get(ProcessorID.LATITUDE).value());
            currentLoc.setLongitude(mProcessors.get(ProcessorID.LONGITUDE).value());

            long time = new Date().getTime();
            for(Iterator<Map.Entry<Double, ThermalSample>> it = mSamples.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Double, ThermalSample> entry = it.next();
                Location location = entry.getValue().mLocation;
                if(location.distanceTo(currentLoc) > MAX_SAMPLE_DISTANCE
                        || time - location.getTime() > MAX_SAMPLE_AGE) {
                    it.remove();
                }
            }
        }

        private void process() {
            Double altitudeSum = 0.0;
            Double latitudeSum = 0.0;
            Double longitudeSum = 0.0;
            Double weightSum = 0.0;
            for (ThermalSample sample : mSamples.values()) {
                if(sample.mVario > mVario)
                    mVario = sample.mVario;
            }

            for(ThermalSample sample: mSamples.values()) {
                Double weight = 1.0 - (mVario - sample.mVario) / mVario;
                weight *= weight;
                weightSum += weight;
                altitudeSum += weight * sample.mLocation.getAltitude();
                latitudeSum += weight * sample.mLocation.getLatitude();
                longitudeSum += weight * sample.mLocation.getLongitude();
            }

            altitudeSum /= weightSum;
            latitudeSum /= weightSum;
            longitudeSum /= weightSum;

            long time = new Date().getTime();
            mLocation = new Location("ThermalCentroid @ " + time);
            mLocation.setAltitude(altitudeSum);
            mLocation.setLatitude(latitudeSum);
            mLocation.setLongitude(longitudeSum);
        }
    }

    private ThermalCentroid mCentroid = new ThermalCentroid();

    private final double MIN_TURN_RATE = 10; // degrees / second

    // IMessageNotify
    public void process() {
        if(mProcessors.isEmpty()) {
            for(UUID id: mProcessorIDs) {
                mProcessors.put(id, ProcessorFactory.build(id));
                mProcessors.get(id).registerSource(mProvider);
                mProcessors.get(id).start();
            }
        }

        for(UUID id: mProcessorIDs) {
            if(!mProcessors.get(id).isValid())
                return;
        }

        Double vario = mProcessors.get(ProcessorID.VARIO).value();
        Double latitude = mProcessors.get(ProcessorID.LATITUDE).value();
        Double longitude = mProcessors.get(ProcessorID.LONGITUDE).value();
        Double altitude = mProcessors.get(ProcessorID.ALTITUDE).value();
        Double turnRate = mProcessors.get(ProcessorID.TURNRATE).value();

        long mCurrentTime = new Date().getTime();

        if(vario > 0 && turnRate > MIN_TURN_RATE) {
            Location loc = new Location("Thermal Sample");
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            loc.setAltitude(altitude);
            loc.setTime(mCurrentTime);
            mCentroid.add(new ThermalSample(loc, vario));
        }
    }

    @Override
    public Vector invalid() { return null; }

    @Override
    public boolean isValid() { return (mCentroid.isValid()); }

    protected boolean hasChanged() {
        return true;
    }

    private Location mLocation = new Location("CurrentLoc");
    private Double mDistance = Double.MAX_VALUE;
    private double mBearing = Double.MAX_VALUE;

    @Override
    public Vector value() {

        if(!mCentroid.isValid())
            return null;

        for(UUID id: mProcessorIDs) {
            if(!mProcessors.get(id).isValid())
                return null;
        }

        new AsyncTask<String,Void,String>(){

            @Override
            protected String doInBackground(String... params) {
                mLocation.setLatitude(mProcessors.get(ProcessorID.LATITUDE).value());
                mLocation.setLongitude(mProcessors.get(ProcessorID.LONGITUDE).value());
                mDistance = (double)mLocation.distanceTo(mCentroid.mLocation);
                mBearing = ((mLocation.bearingTo(mCentroid.mLocation) + 720.0) - mProcessors.get(ProcessorID.BEARING).value()) % 360.0;

                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();

        Vector result = new Vector();
        result.setDirectionAndMagnitude(mBearing, mDistance);
        return result;
    }

    public double getClimbRate() {
        return mCentroid.mVario;
    }
}
