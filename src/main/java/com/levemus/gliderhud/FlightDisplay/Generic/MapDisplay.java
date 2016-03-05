package com.levemus.gliderhud.FlightDisplay.Generic;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.levemus.gliderhud.FlightData.Pipeline.MessageListener;
import com.levemus.gliderhud.FlightData.Processors.Custom.Thermal;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Providers.Recon.HeadLocationProvider;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.Messages.ChannelMessages.ChannelMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.R;
import com.levemus.gliderhud.Types.ThermalCore;
import com.levemus.gliderhud.Types.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mark@levemus on 16-01-25.
 */

public class MapDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();
    private final int METERS_PER_PIXEL = 2;
    private final float THERMAL_MARKER_RADIUS = 5; // pixels
    private final int POSITION_MARKER_WIDTH = 15; // pixels
    private final int POSITION_MARKER_HEIGHT = 15; // pixels

    private final List<Double> mClimbRateRatios = new ArrayList<>(Arrays.asList(1.0, 2.0, 4.0));
    private final List<Integer> mClimbRateColors = new ArrayList<>(Arrays.asList(Color.GREEN, Color.YELLOW, Color.RED));

    private Bitmap mBitmap;
    private Canvas mCanvas;

    // Displays
    private ImageView mMapDisplayImage;

    public MapDisplay() {
        mProcessors.put(ProcessorID.VARIO, ProcessorFactory.build(ProcessorID.VARIO));
        mProcessors.put(ProcessorID.BEARING, ProcessorFactory.build(ProcessorID.BEARING));
        mProcessors.put(ProcessorID.THERMAL, ProcessorFactory.build(ProcessorID.THERMAL));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.map_display, container, false);
    }

    private int getCoreColor() {
        if(mResults.containsKey(ProcessorID.VARIO) && mResults.containsKey(ProcessorID.THERMAL)) {
            double climbRatio = ((ThermalCore)mResults.get(ProcessorID.THERMAL)).mStrength / (Double)mResults.get(ProcessorID.VARIO);
            double previousRatio = mClimbRateRatios.get(0);
            for(Double ratio : mClimbRateRatios) {
                if(ratio > climbRatio || (mClimbRateRatios.indexOf(ratio) == mClimbRateRatios.size() - 1)) {
                    int lowColor = mClimbRateColors.get(mClimbRateRatios.indexOf(previousRatio));
                    int highColor = mClimbRateColors.get(mClimbRateRatios.indexOf(ratio));
                    double factor = climbRatio - previousRatio / (ratio - previousRatio);
                    int interpolatedR = (int)((Color.red(highColor) - Color.red(lowColor)) * factor) + Color.red(lowColor);
                    int interpolatedG = (int)((Color.green(highColor) - Color.green(lowColor)) * factor) + Color.green(lowColor);
                    int interpolatedB = (int)((Color.blue(highColor) - Color.blue(lowColor)) * factor) + Color.blue(lowColor);

                    return Color.argb(0xFF, interpolatedR, interpolatedG, interpolatedB);
                }
            }
        }
        return(Color.WHITE);
    }

    private boolean mActive = false;
    @Override
    protected void update() {
        if(mMapDisplayImage == null) {
            mMapDisplayImage = (ImageView) getActivity().findViewById(R.id.mapDisplay);
            int width = mMapDisplayImage.getWidth();
            int height = mMapDisplayImage.getHeight();
            if(width > 0 && height > 0) {
                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
            }
        }

        if(mActive)
            return;
        mActive = true;
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(mCanvas == null)
                    return null;

                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                Paint paint = new Paint();
                if(mResults.containsKey(ProcessorID.THERMAL) && mResults.containsKey(ProcessorID.BEARING)) {
                    paint.setColor(getCoreColor());
                    Vector thermal = ((ThermalCore) mResults.get(ProcessorID.THERMAL)).mLocation;
                    thermal.setDirectionAndMagnitude((thermal.Direction() + (Double)mResults.get(ProcessorID.BEARING)) % 360.0, thermal.Magnitude());
                    float locationX = ((float) thermal.X() / METERS_PER_PIXEL) + mBitmap.getWidth() / 2;
                    float locationY = (float) thermal.Y() / METERS_PER_PIXEL + mBitmap.getHeight() / 2;
                    mCanvas.drawCircle(locationX, locationY, THERMAL_MARKER_RADIUS, paint);
                }
                paint.setColor(Color.WHITE);
                mCanvas.drawLine(mBitmap.getWidth() / 2 - POSITION_MARKER_WIDTH / 2,
                        mBitmap.getHeight() / 2,
                        mBitmap.getWidth() / 2 + POSITION_MARKER_WIDTH / 2,
                        mBitmap.getHeight() / 2, paint);

                mCanvas.drawLine(mBitmap.getWidth() / 2,
                        mBitmap.getHeight() / 2 - POSITION_MARKER_HEIGHT / 2,
                        mBitmap.getWidth() / 2,
                        mBitmap.getHeight() / 2 + POSITION_MARKER_HEIGHT / 2, paint);
                return null;
            }

            @Override
            protected void onPostExecute(Void res) {
                //mMapDisplayImage.setImageBitmap(mBitmap);
                mActive = false;
            }
        }.execute();
    }
}
