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
 * Created by mark@levemus on 15-11-23.
 */

public class Vector {
    private double _X = 0;
    private double _Y = 0;

    private double _magnitude = 0;
    private double _direction = 0;

    public double Direction() { return _direction; }
    public double Magnitude() { return _magnitude; }
    public double X() { return _X; }
    public double Y() { return _Y; }

    public void setDirectionAndMagnitude(double direction, double magnitude)
    {
        _Y = magnitude * Math.cos(Math.toRadians(direction));
        _X = magnitude * Math.sin(Math.toRadians(direction));
        _direction = direction;
        _magnitude = magnitude;
    }

    public Vector Add(Vector b)
    {
        Update(_X + b.X(), _Y + b.Y());
        return this;
    }

    public Vector() {}
    public Vector(double X, double Y)
    {
        Update(X, Y);
    }

    public Vector(Vector src)
    {
        _X = src.X();
        _Y = src.Y();
        _magnitude = src.Magnitude();
        _direction = src.Direction();
    }

    private void Update(double X, double Y)
    {
        _X = X;
        _Y = Y;
        _magnitude = Math.sqrt(_X * _X + _Y * _Y);
        double arctanValue = java.lang.Math.atan2(_X, _Y);
        _direction = (arctanValue >= 0 ? arctanValue : (2 * Math.PI + arctanValue)) * 360 / (2 * Math.PI);
    }
}
