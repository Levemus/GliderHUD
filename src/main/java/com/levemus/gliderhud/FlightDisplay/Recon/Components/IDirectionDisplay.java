package com.levemus.gliderhud.FlightDisplay.Recon.Components;

/**
 * Created by markcarter on 16-01-22.
 */
public interface IDirectionDisplay {
    void setParentDirection(double angle);
    int getWidth();
    int getPosition();
    void setAlpha(int alpha);
}
