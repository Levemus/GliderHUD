package com.levemus.gliderhud.FlightData.Broadcasters;
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

import com.levemus.gliderhud.FlightData.Listeners.IFlightDataListener;
import com.levemus.gliderhud.FlightData.IFlightData;
import java.util.EnumSet;

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
public interface IFlightDataBroadcaster {
    public EnumSet<IFlightData.FlightDataType> supportedTypes();
    public EnumSet<IFlightData.FlightDataType> addListener(IFlightDataListener listener, long notificationInterval, EnumSet<IFlightData.FlightDataType> subscription);
    public void init(Activity activity);
    public void pause();
    public void resume();

}
