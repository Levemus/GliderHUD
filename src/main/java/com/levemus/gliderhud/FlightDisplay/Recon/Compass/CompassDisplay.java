package com.levemus.gliderhud.FlightDisplay.Recon.Compass;

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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Pipeline.MessageListener;
import com.levemus.gliderhud.FlightData.Processors.Factory.ProcessorFactory;
import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.Messages.ChannelMessages.ChannelMessage;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.FlightData.Providers.Recon.HeadLocationProvider;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.R;
import com.levemus.gliderhud.Utils.Angle;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


/**
 * Created by mark@levemus on 15-12-01.
 */
public class CompassDisplay extends FlightDisplay implements HeadLocationListener {

    private final String TAG = this.getClass().getSimpleName();
    private HeadingDisplay mHeadingDisplay = new HeadingDisplay();
    private BearingDisplay mBearingDisplay = new BearingDisplay();

    private List<CompassSubDisplay> mSubDisplays = Arrays.asList(
            new WaypointDisplay(),
            new LaunchDisplay(),
            new WindDisplay()
    );

    private HUDHeadingManager mHUDHeadingManager = null;

    public CompassDisplay() {
        if(mHUDHeadingManager == null)
            mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);
        mHUDHeadingManager.register(this);
    }

    private double mYaw = -5.0;

    private double smoothDirection(double newHeading, double oldHeading) {

        double heading = oldHeading;

        if (heading > 270.0f && newHeading < 90.0f) {
            heading = heading - 360.0f;// avoid aliasing in average when crossing North (angle = 0.0)
        } else if (heading < 90.0f && newHeading > 270.0f) {
            newHeading = newHeading - 360.0f; // avoid aliasing in average when crossing North (angle = 0.0)
        }

        heading = (float) ((4.0 * heading + newHeading) / 5.0); // smooth heading
        if (heading < 0.0f) heading += 360.0f;
        if (heading > 360.0f) heading -= 360.0f;

        return heading;
    }

    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if (Float.isNaN(yaw)) {
            return;
        }

        long currentTime = new Date().getTime();
        if (currentTime - mTimeOfLastUpdate > refreshPeriod()) {
            mYaw = smoothDirection(yaw, mYaw);
            display();
            mTimeOfLastUpdate = currentTime;
        }
    }

    private final int OVERLAP_ALPHA = 0x40;
    private final int NORMAL_ALPHA = 0xFF;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.compass_display, container, false);
    }

    protected void display() {

        try {
            if(mProcessors.isEmpty()) {
                HashSet<UUID> processorIds = new HashSet<>();
                for(CompassSubDisplay subDisplay : mSubDisplays) {
                    processorIds.addAll(subDisplay.processorIDs());
                }
                processorIds.addAll(mBearingDisplay.processorIDs());

                for(UUID id : processorIds) {
                    mProcessors.put(id, ProcessorFactory.build(id));
                }
            }

            mHeadingDisplay.setHeading(mYaw);
            mHeadingDisplay.display(this, mResults);
            for(CompassSubDisplay subDisplay : mSubDisplays) {
                subDisplay.setHeading(mYaw);
                try {
                    if (subDisplay.canDisplay(mResults)) {
                        subDisplay.setAlpha(NORMAL_ALPHA);

                        int currentLower = subDisplay.getPosition() - (int) (0.5 * subDisplay.getWidth());
                        int currentUpper = subDisplay.getPosition() + (int) (0.5 * subDisplay.getWidth());

                        for (CompassSubDisplay previousDisplay : mSubDisplays) {
                            if (previousDisplay == subDisplay)
                                break;
                            if (previousDisplay.canDisplay(mResults)) {
                                int previousLower = previousDisplay.getPosition() - (int) (0.5 * previousDisplay.getWidth());
                                int previousUpper = previousDisplay.getPosition() + (int) (0.5 * previousDisplay.getWidth());

                                if ((previousLower < currentLower && previousUpper > currentLower) ||
                                        (previousUpper > currentUpper && previousLower < currentUpper)) {
                                    subDisplay.setAlpha(OVERLAP_ALPHA);
                                }
                            }
                        }

                        subDisplay.display(this, mResults);
                    }
                }catch(Exception e){
                    Log.d(TAG, "Exception: " + e);
                }
            }
            mBearingDisplay.setHeading(mYaw);
            mBearingDisplay.display(this, mResults);
        } catch (Exception e) {}
    }

    protected int refreshPeriod() { return 30; }
}
