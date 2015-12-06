package com.levemus.gliderhud.Types;

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
 * Created by flyinorange on 15-11-23.
 */
public class Point
{
    private double _x;
    private double _y;

    public double X()
    {
        return _x;
    }

    public double Y()
    {
        return _y;
    }

    public Point()
    {
        _x = 0;
        _y = 0;
    }

    public Point(double x, double y)
    {
        _x = x;
        _y = y;
    }
}

