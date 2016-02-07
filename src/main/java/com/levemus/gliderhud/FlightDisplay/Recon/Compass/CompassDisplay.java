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
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Managers.IChannelDataSource;
import com.levemus.gliderhud.FlightData.Managers.IClient;
import com.levemus.gliderhud.FlightDisplay.IFlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.IDirectionDisplay;
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.FlightData.Providers.Recon.HeadLocationProvider;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Messages.IMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by mark@levemus on 15-12-01.
 */
public class CompassDisplay extends FlightDisplay implements IClient {

    private final String TAG = this.getClass().getSimpleName();

    private HeadLocationProvider mOrientation;

    private DirectionDisplayImage mHeadingDisplay = null;

    private List<CompassSubDisplay> mDirectionDisplays = Arrays.asList(
            new BearingDisplay(),
            new WaypointDisplay(),
            new LaunchDisplay(),
            new WindDisplay()
            );

    @Override
    public void init(final Activity activity)
    {
        super.init(activity);

        ((Activity)mContext).runOnUiThread(new Runnable() {
            public void run() {
                mHeadingDisplay = new DirectionDisplayImage((ImageView)
                        activity.findViewById(com.levemus.gliderhud.R.id.compass_bar));
            }});

        mOrientation = new HeadLocationProvider();
        mOrientation.registerClient(this);
        mOrientation.start(activity);

        for(FlightDisplay display : mDirectionDisplays) {
            display.init(activity);
        }
    }

    @Override
    public void deInit(Activity activity) {

        for(FlightDisplay display : mDirectionDisplays) {
            display.deInit(activity);
        }

        mOrientation.stop(activity);
        mOrientation = null;

        mHeadingDisplay = null;

        super.deInit(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider)
    {
        for(FlightDisplay display : mDirectionDisplays) {
            display.registerProvider(provider);
        }
    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {

        for(FlightDisplay display : mDirectionDisplays) {
            display.deRegisterProvider(provider);
        }
    }

    private Double mYaw;

    @Override
    public void onMsg(IMessage msg) {
        try {
            mYaw = ((DataMessage)msg).get(Channels.YAW);
        }catch(Exception e){}
    }

    private final int OVERLAP_ALPHA = 0x40;
    private final int NORMAL_ALPHA = 0xFF;

    @Override
    public void display(Activity activity)
    {
        try {
            Double current = mHeadingDisplay.getCurrentDirection();
            mHeadingDisplay.setCurrentDirection(DirectionDisplay.smoothDirection(mYaw, current));

            for(IDirectionDisplay display : mDirectionDisplays) {
                display.setParentDirection(mHeadingDisplay.getCurrentDirection());
            }

            mHeadingDisplay.display(activity);
            for(CompassSubDisplay currentDisplay : mDirectionDisplays) {
                if(currentDisplay.canDisplay()) {
                    currentDisplay.setAlpha(NORMAL_ALPHA);
                    int currentLower = currentDisplay.getPosition() - (int)(0.5 * currentDisplay.getWidth());
                    int currentUpper = currentDisplay.getPosition() + (int)(0.5 * currentDisplay.getWidth());
                    for(CompassSubDisplay previousDisplay : mDirectionDisplays) {
                        if(previousDisplay == currentDisplay)
                            break;
                        if(previousDisplay.canDisplay()) {
                            int previousLower = previousDisplay.getPosition() - (int)(0.5 * previousDisplay.getWidth());
                            int previousUpper = previousDisplay.getPosition() + (int)(0.5 * previousDisplay.getWidth());

                            if((previousLower < currentLower && previousUpper > currentLower) ||
                                    (previousUpper > currentUpper && previousLower < currentUpper))
                            {
                                currentDisplay.setAlpha(OVERLAP_ALPHA);
                            }
                        }
                    }
                    currentDisplay.display(activity);
                }
            }

        }catch(Exception e){}
    }

    @Override
    public void hide() {}

    protected int refreshPeriod() { return 30; } // ms
}
