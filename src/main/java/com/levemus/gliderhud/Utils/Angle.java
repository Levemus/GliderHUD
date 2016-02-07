package com.levemus.gliderhud.Utils;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */

/**
 * Created by mark@levemus on 15-12-18.
 */

public class Angle {

    private static double DEGREES_PER_CIRCLE = 360;
    private static double DEGREES_PER_DEMI_CIRCLE = DEGREES_PER_CIRCLE / 2;
    public static double delta(double angleA, double angleB)
    {
        double difference = difference(angleA, angleB);
        int sign = (angleA - angleB >= 0 && angleA - angleB <= DEGREES_PER_DEMI_CIRCLE) || (angleA - angleB <=-DEGREES_PER_DEMI_CIRCLE && angleA- angleB>= -DEGREES_PER_CIRCLE) ? -1 : 1;
        difference *= sign;

        return difference;
    }

    public static double difference(double angleA, double angleB)
    {
        double difference = Math.abs(angleA - angleB) % DEGREES_PER_CIRCLE;
        difference = Math.min(DEGREES_PER_CIRCLE - difference, difference);
        return difference;
    }

    public static double AddDelta(double angle, double delta) {
        angle += delta;
        if(angle < 0) {
            angle = DEGREES_PER_CIRCLE + angle;
        }

        angle %= DEGREES_PER_CIRCLE;
        return angle;
    }
}
