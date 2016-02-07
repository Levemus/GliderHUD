package com.levemus.gliderhud.FlightData.Listeners;

import android.app.Activity;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Providers.Android.InternalGPSProvider;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by markcarter on 16-01-25.
 */
public class IGCLogger implements IListener {

    // Constants
    private final String TAG = this.getClass().getSimpleName();

    // Listeners
    private List<UUID> mProcessorIDs = Arrays.asList(
            ProcessorID.TIME,
            ProcessorID.LATITUDE,
            ProcessorID.LONGITUDE,
            ProcessorID.PRESSUREALTITUDE,
            ProcessorID.GPSALTITUDE);

    private HashMap<UUID, Processor<Double>> mProcessors = new HashMap<>();

    // Members
    private Handler mHandler = new Handler();
    private final int PERIOD = 1000; // ms
    private String mFileNamePrefix = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/GliderHUD_Log_";
    private String mFileNamePostfix = ".igc";

    @Override
    public void init(Activity activity) {}

    @Override
    public void deInit(Activity activity) {}

    @Override
    public void registerProvider(IChannelDataSource provider){
        for(UUID id : mProcessorIDs) {
            mProcessors.put(id, ProcessorFactory.build(id));
            mProcessors.get(id).registerSource(provider);
            mProcessors.get(id).start();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                writeLog();
                mHandler.postDelayed(this, PERIOD);
            }
        }, PERIOD);
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider){
        for(UUID id : mProcessorIDs) {
            mProcessors.get(id).stop();
            mProcessors.get(id).deRegisterSource(provider);
            mProcessors.remove(id);
        }
    }

    String mFileName;
    private void writeLog() {
        for(UUID id : mProcessorIDs) {
            if (mProcessors.get(id) == null)
                return;
            if (!mProcessors.get(id).isValid())
                return;
        }

        if(mFileName == null) {
            Long time = mProcessors.get(ProcessorID.TIME).value().longValue();
            Date date = new Date(time);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyMMddhhmm");
            String dateText = dateFormatter.format(date);
            mFileName = mFileNamePrefix + dateText + mFileNamePostfix;
            File file = new File(mFileName);
            try {
                BufferedWriter bufferedWritter = new BufferedWriter(new FileWriter(file));
                for(Header header : mIGCHeader) {
                    bufferedWritter.append(header.mKey);
                    if(header.mKey.compareTo("HFDTE") == 0) {
                        dateFormatter = new SimpleDateFormat("ddMMyy");
                        dateText = dateFormatter.format(date);
                        bufferedWritter.append(dateText);
                    }
                    else if(header.mValue.length() != 0)
                        bufferedWritter.append(header.mValue);
                    bufferedWritter.append("\r\n");
                }
                bufferedWritter.flush();
                bufferedWritter.close();
            } catch(Exception e) {}
        }

        try {
            File file = new File(mFileName);
            BufferedWriter bufferedWritter = new BufferedWriter(new FileWriter(file, true));
            bufferedWritter.write("B");

            Long time = mProcessors.get(ProcessorID.TIME).value().longValue();
            Date date = new Date(time);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("hhmmss");
            String dateText = dateFormatter.format(date);
            bufferedWritter.append(dateText);

            double latitude = mProcessors.get(ProcessorID.LATITUDE).value();
            int degrees = (int)Math.abs(latitude);
            int minutes = (int)((Math.abs(latitude) - degrees) * 60 * 1000);
            String direction = (latitude < 0 ? "S": "N");
            String degreeString = String.format("%02d", degrees);
            String minutesString = String.format("%05d", minutes);
            bufferedWritter.append(degreeString
                    + minutesString
                    + direction);

            double longitude = mProcessors.get(ProcessorID.LONGITUDE).value();
            degrees = (int)Math.abs(longitude);
            minutes = (int)((Math.abs(longitude) - degrees) * 60 * 1000);
            direction = (longitude < 0 ? "W": "E");

            degreeString = String.format("%03d", degrees);
            minutesString = String.format("%05d", minutes);
            bufferedWritter.append(degreeString
                    + minutesString
                    + direction);

            bufferedWritter.append("A");

            String altitudeString;
            double pressureAltitude = mProcessors.get(ProcessorID.PRESSUREALTITUDE).value();
            if(pressureAltitude > 0)
                altitudeString = String.format("%05d", (int)pressureAltitude);
            else
                altitudeString = String.format("-%04d", (int)pressureAltitude);

            bufferedWritter.append(altitudeString);

            double gpsAltitude = mProcessors.get(ProcessorID.GPSALTITUDE).value();
            bufferedWritter.append(String.format("%05d", Math.max((int)gpsAltitude, 0)));

            bufferedWritter.append("\r\n");
            bufferedWritter.flush();
            bufferedWritter.close();
        } catch(Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    class Header {
        String mKey;
        String mValue;

        Header(String key, String value) {
            mKey = key;
            mValue = value;
        }
    }

    List<Header> mIGCHeader = Arrays.asList(
            new Header("AXXX:", "GliderHUD Logger v0.1"),
            new Header("HFDTE", "$DATE"),
            new Header("HFFXA035", ""),
            new Header("HFPLTPILOT:", "Mark Carter"),
            new Header("HFGTYGLIDERTYPE:", "Gin Carrera+"),
            new Header("HFGIDGLIDERID:", "not recorded"),
            new Header("HFDTM100GPSDATUM:", "WGS-84"),
            new Header("HFCIDCOMPETITIONID:", "none recorded"),
            new Header("HFCCLCOMPETITIONCLASS:", "none recorded")
    );
}
