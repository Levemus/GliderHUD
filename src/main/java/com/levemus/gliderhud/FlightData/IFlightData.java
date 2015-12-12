package com.levemus.gliderhud.FlightData;

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
 * Created by mark@levemus on 15-12-05.
 */
import java.util.EnumSet;

public interface IFlightData {

    public double getData(FlightDataType type) throws java.lang.UnsupportedOperationException;
    public EnumSet<FlightDataType> supportedTypes();
    public enum FlightDataType {
        ALTITUDE, GROUNDSPEED, BEARING, YAW, VARIO, GLIDE, WINDSPEED, WINDDIRECTION;
        public static final EnumSet<FlightDataType> ALL_OPTS = EnumSet.allOf(FlightDataType.class);
    }
}
