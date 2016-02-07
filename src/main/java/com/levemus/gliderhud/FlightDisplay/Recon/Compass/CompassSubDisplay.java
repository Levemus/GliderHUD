package com.levemus.gliderhud.FlightDisplay.Recon.Compass;

import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.Recon.Components.IDirectionDisplay;

/**
 * Created by markcarter on 16-01-22.
 */
public class CompassSubDisplay extends FlightDisplay implements IDirectionDisplay {

    @Override
    public void setParentDirection(double angle) {}

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public int getPosition() {return 0;}

    @Override
    public int getWidth() {return 0;}

}
