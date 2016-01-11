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
import com.levemus.gliderhud.Messages.ChannelMessages.Channels;
import com.levemus.gliderhud.FlightData.Providers.Recon.HeadLocationProvider;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.DirectionDisplayImage;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;
import com.levemus.gliderhud.Messages.IMessage;


/**
 * Created by mark@levemus on 15-12-01.
 */
public class CompassDisplay extends FlightDisplay implements IClient {

    private final String TAG = this.getClass().getSimpleName();

    private HeadLocationProvider mOrientation;

    private DirectionDisplayImage mHeadingDisplay = null;
    private WindDisplay mWindDisplay = null;
    private WaypointDisplay mWaypointDisplay = null;
    private BearingDisplay mBearingDisplay = null;

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

        mWindDisplay = new WindDisplay();
        mWindDisplay.init(activity);

        mWaypointDisplay = new WaypointDisplay();
        mWaypointDisplay.init(activity);

        mBearingDisplay = new BearingDisplay();
        mBearingDisplay.init(activity);

    }

    @Override
    public void deInit(Activity activity) {

        mBearingDisplay.deInit(activity);
        mWindDisplay.deInit(activity);
        mWaypointDisplay.deInit(activity);

        mOrientation.stop(activity);
        mOrientation = null;

        mHeadingDisplay = null;

        super.deInit(activity);
    }

    @Override
    public void registerProvider(IChannelDataSource provider)
    {
        mWaypointDisplay.registerProvider(provider);
        mWindDisplay.registerProvider(provider);
        mBearingDisplay.registerProvider(provider);

    }

    @Override
    public void deRegisterProvider(IChannelDataSource provider) {

        mWaypointDisplay.deRegisterProvider(provider);
        mWindDisplay.deRegisterProvider(provider);
        mBearingDisplay.deRegisterProvider(provider);

    }

    private Double mYaw;

    @Override
    public void onMsg(IMessage msg) {
        try {
            mYaw = ((DataMessage)msg).get(Channels.YAW);
        }catch(Exception e){}
    }

    @Override
    public void display(Activity activity)
    {
        try {
            Double current = mHeadingDisplay.getCurrentDirection();
            mHeadingDisplay.setCurrentDirection(DirectionDisplay.smoothDirection(mYaw, current));
            mBearingDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
            mWindDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());
            mWaypointDisplay.setBaseAngle(mHeadingDisplay.getCurrentDirection());

            mHeadingDisplay.display(activity);
            mWindDisplay.display(activity);
            mWaypointDisplay.display(activity);
            mBearingDisplay.display(activity);

        }catch(Exception e){}
    }

    @Override
    public void hide() {}

    protected int refreshPeriod() { return 30; } // ms
}