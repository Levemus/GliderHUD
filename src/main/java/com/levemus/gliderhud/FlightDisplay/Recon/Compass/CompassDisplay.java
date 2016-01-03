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

import java.util.UUID;

import android.app.Activity;
import android.widget.ImageView;

import com.levemus.gliderhud.FlightData.Configuration.Configuration;
import com.levemus.gliderhud.FlightData.Managers.IChannelDataProvider;
import com.levemus.gliderhud.FlightData.Messages.MessageChannels;
import com.levemus.gliderhud.FlightData.Providers.Recon.HeadLocationProvider;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;


/**
 * Created by mark@levemus on 15-12-01.
 */
public class CompassDisplay extends FlightDisplay {

    private final String TAG = this.getClass().getSimpleName();

    private HeadLocationProvider mOrientation;

    private DirectionDisplayImage mHeadingDisplay = null;
    private WindDisplay mWindDisplay = null;
    private BearingDisplay mBearingDisplay = null;

    @Override
    public void init(Activity activity)
    {
        super.init(activity);

        mHeadingDisplay = new DirectionDisplayImage((ImageView)
                activity.findViewById(com.levemus.gliderhud.R.id.compass_bar));

        mOrientation = new HeadLocationProvider();
        mOrientation.start(activity);

        mWindDisplay = new WindDisplay();
        mWindDisplay.init(activity);

        mBearingDisplay = new BearingDisplay();
        mBearingDisplay.init(activity);
    }

    @Override
    public void deInit(Activity activity) {
        mBearingDisplay.deInit(activity);
        mWindDisplay.deInit(activity);

        mOrientation.stop(activity);
        mOrientation = null;

        mHeadingDisplay = null;

        super.deInit(activity);
    }

    @Override
    public void registerProvider(IChannelDataProvider provider)
    {
        mWindDisplay.registerProvider(provider);
        mBearingDisplay.registerProvider(provider);
    }

    @Override
    public void deRegisterProvider(IChannelDataProvider provider) {

        mWindDisplay.deRegisterProvider(provider);
        mBearingDisplay.deRegisterProvider(provider);
    }

    @Override
    public void display()
    {
        try {
            Configuration config = new Configuration(
                    UUID.randomUUID(),
                    mOrientation.channels());
            Double yaw = mOrientation.pullFrom(config).get(MessageChannels.YAW);
            Double current = mHeadingDisplay.getCurrentDirection();
            mHeadingDisplay.setCurrentDirection(DirectionDisplay.smoothDirection(yaw, current));
            mBearingDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
            mWindDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
            mHeadingDisplay.display();
            mBearingDisplay.display();
            mWindDisplay.display();
        }catch(Exception e){}
    }

    @Override
    public void hide() {}

    protected int refreshPeriod() { return 30; } // ms
}
