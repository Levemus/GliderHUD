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
 * Created by mark@levemus on 15-12-19.
 */
public class NormalDistribution {
    double n2 = 0;
    int n2_cached = 0;
    public double random(double mean, double stddev)
    {
        double x, y, r;
        if (n2_cached == 0)
        {
            do
            {
                x = 2.0*Math.random();
                y = 2.0*Math.random();
                r = x*x + y*y;
            } while (r==0.0 || r>1.0);
            double d = Math.sqrt(-2.0*Math.log(r)/r);
            double n1 = x*d;
            double result = n1*stddev + mean;
            n2 = y*d;
            n2_cached = 1;
            return result;
        }
        else
        {
            n2_cached = 0;
            return n2*stddev + mean;
        }
    }
}
