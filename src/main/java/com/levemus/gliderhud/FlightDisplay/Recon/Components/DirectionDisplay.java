package com.levemus.gliderhud.FlightDisplay.Recon.Components;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightDisplay.IFlightDisplay;
import com.levemus.gliderhud.Types.Point;
import com.levemus.gliderhud.Utils.Angle;

/**
 * Created by mark@levemus on 15-12-01.
 */
abstract public class DirectionDisplay implements IFlightDisplay {

    private static double PIXELS_PER_45_DEGREES = 190.0;
    protected double mCurrentDirection = 0;
    protected DirectionDisplayOffset mImageOffset = null;
    protected DirectionDisplay() {}

    public void display(boolean refresh) {}

    public static double smoothDirection(double newHeading, double oldHeading) {

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

    protected Point getScreenLocation(double angle)
    {
        int offset = (angle >= 315f && angle <= 360) ? -(int) PIXELS_PER_45_DEGREES * 7 : (int) PIXELS_PER_45_DEGREES;
        int x = (int) (angle / 360.0 * (8.0 * PIXELS_PER_45_DEGREES)) + offset;
        return new Point(x, 0);
    }

    public double getCurrentDirection() {
        return mCurrentDirection;
    }

    public void setCurrentDirection(double direction) {
        mCurrentDirection = direction;
    }

    // Offset functions
    public void setOffsetBaseAngle( double angle)
    {
        if(mImageOffset == null)
            mImageOffset = new DirectionDisplayOffset();
        mImageOffset.setBaseAngle(angle);
    }

    protected class DirectionDisplayOffset {

        private double mBaseAngle = 0;

        public void setBaseAngle(double angle)
        {
            mBaseAngle = angle;
        }
        public double baseAngle() {return mBaseAngle;}

        public double determineOffset(double angle)
        {
            return(Angle.delta(((angle + 93) % 360), mBaseAngle));
        }
    }
}
