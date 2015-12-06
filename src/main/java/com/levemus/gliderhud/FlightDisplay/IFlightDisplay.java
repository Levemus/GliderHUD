package com.levemus.gliderhud.FlightDisplay;

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

/**
 * Created by mark@levemus on 15-11-29.
 */
public interface IFlightDisplay {
    public void init(Activity activity);
    public void display();
}
