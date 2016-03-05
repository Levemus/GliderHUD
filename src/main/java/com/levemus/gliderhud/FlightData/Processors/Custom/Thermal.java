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

import com.levemus.gliderhud.FlightData.Configuration.ChannelEntity;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.IProcessor;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Providers.Recon.HeadLocationProvider;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Types.ThermalCore;
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
public class Thermal extends Processor<ThermalCore> implements IProcessor<ThermalCore>, ChannelEntity {

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

    private HeadLocationProvider mOrientation;

    @Override
    public HashSet<UUID> channels() {
        return new HashSet<UUID>();
    }

    @Override
    public UUID id() { return ProcessorID.THERMAL; }

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
            currentLoc.setLatitude(results.get(ProcessorID.LATITUDE));
            currentLoc.setLongitude(results.get(ProcessorID.LONGITUDE));

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

    HashMap<UUID, Double> results = new HashMap<>();

    @Override
    public ThermalCore onMsg(DataMessage msg) {

        if(mProcessors.isEmpty()) {
            for(UUID id: mProcessorIDs) {
                mProcessors.put(id, ProcessorFactory.build(id));
            }
        }

        for(UUID id: mProcessorIDs) {
            Double result = mProcessors.get(id).onMsg(msg);
            if(mProcessors.get(id).isValid(result)) {
                results.put(id, result);
            }
        }

        long currentTime = new Date().getTime();
        if(currentTime - mTimeOfLastProcess > refreshPeriod()) {
            process();
            mTimeOfLastProcess = currentTime;
        }

        return value();
    }

    // IMessageNotify
    @Override
    public void process() {

        if(!results.containsKey(ProcessorID.VARIO) ||
                !results.containsKey(ProcessorID.LATITUDE) ||
                !results.containsKey(ProcessorID.LONGITUDE) ||
                !results.containsKey(ProcessorID.ALTITUDE) ||
                !results.containsKey(ProcessorID.TURNRATE))
            return;

        Double vario = results.get(ProcessorID.VARIO);
        Double latitude = results.get(ProcessorID.LATITUDE);
        Double longitude = results.get(ProcessorID.LONGITUDE);
        Double altitude = results.get(ProcessorID.ALTITUDE);
        Double turnRate = results.get(ProcessorID.TURNRATE);

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
    public ThermalCore invalid() { return null; }

    @Override
    public boolean isValid(ThermalCore value) { return (value != null); }

    private Location mLocation = new Location("CurrentLoc");
    private Double mDistance = Double.MAX_VALUE;
    private Double mBearing = Double.MAX_VALUE;

    @Override
    public ThermalCore value() {

        if (!mCentroid.isValid())
            return null;

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                mLocation.setLatitude(results.get(ProcessorID.LATITUDE));
                mLocation.setLongitude(results.get(ProcessorID.LONGITUDE));
                mDistance = (double) mLocation.distanceTo(mCentroid.mLocation);
                mBearing = ((mCentroid.mLocation.bearingTo(mLocation) + 720.0)) % 360.0;
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();

        ThermalCore result = new ThermalCore();
        result.mLocation = new Vector();
        result.mLocation.setDirectionAndMagnitude(mBearing, mDistance);
        result.mStrength = mCentroid.mVario;
        return result;
    }
}
