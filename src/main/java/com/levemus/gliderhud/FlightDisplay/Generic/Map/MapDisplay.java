package com.levemus.gliderhud.FlightDisplay.Generic.Map;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Processors.Custom.ThermalCore;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;

import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorID;
import com.levemus.gliderhud.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mark@levemus on 16-01-25.
 */

public class MapDisplay extends FlightDisplay {

    // Constants
    private final String TAG = this.getClass().getSimpleName();

    // Listeners
    private ThermalCore mClimbLocation;
    private Processor<Double> mVario;

    // Displays
    private ImageView mMapDisplay;

    // Initialization/registration
    @Override
    public void init(Activity activity)
    {
        mMapDisplay = (ImageView) activity.findViewById(R.id.mapDisplay);
        super.init(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider) {
        mClimbLocation = new ThermalCore();
        mClimbLocation.registerSource(provider);
        mClimbLocation.start();

        mVario = ProcessorFactory.build(ProcessorID.VARIO);
        mVario.registerSource(provider);
        mVario.start();
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {
        mClimbLocation.stop();
        mClimbLocation.deRegisterSource(provider);
        mClimbLocation = null;

        mVario.stop();
        mVario.deRegisterSource(provider);
        mVario = null;
    }

    private final int METERS_PER_PIXEL = 2;
    private final float THERMAL_MARKER_RADIUS = 5; // pixels
    private final int POSITION_MARKER_WIDTH = 15; // pixels
    private final int POSITION_MARKER_HEIGHT = 15; // pixels

    private List<Double> mClimbRateRatios = new ArrayList<>(Arrays.asList(1.0, 2.0, 4.0));
    private List<Integer> mClimbRateColors = new ArrayList<>(Arrays.asList(Color.GREEN, Color.YELLOW, Color.RED));

    private int getCoreColor() {
        if(mVario.isValid() && mClimbLocation.isValid()) {
            double climbRatio = mClimbLocation.getClimbRate() / mVario.value();
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
    // Operation
    @Override
    public void display(Activity activity) {
        try {
            if(mClimbLocation.isValid()) {
                Bitmap bitmap = Bitmap.createBitmap(mMapDisplay.getWidth(), mMapDisplay.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(getCoreColor());
                float locationX = ((float)mClimbLocation.value().X() / METERS_PER_PIXEL) + mMapDisplay.getWidth() / 2;
                float locationY = (float)mClimbLocation.value().Y() / METERS_PER_PIXEL + mMapDisplay.getHeight() / 2;
                canvas.drawCircle(locationX, locationY, THERMAL_MARKER_RADIUS, paint);
                paint.setColor(Color.WHITE);
                canvas.drawLine(mMapDisplay.getWidth() / 2 - POSITION_MARKER_WIDTH / 2,
                        mMapDisplay.getHeight() / 2,
                        mMapDisplay.getWidth() / 2 + POSITION_MARKER_WIDTH / 2,
                        mMapDisplay.getHeight() / 2, paint);

                canvas.drawLine(mMapDisplay.getWidth() / 2,
                        mMapDisplay.getHeight() / 2 - POSITION_MARKER_HEIGHT / 2,
                        mMapDisplay.getWidth() / 2,
                        mMapDisplay.getHeight() / 2 + POSITION_MARKER_HEIGHT / 2, paint);

                mMapDisplay.setImageBitmap(bitmap);
            }
        }catch (Exception e){
        }
    }
}
