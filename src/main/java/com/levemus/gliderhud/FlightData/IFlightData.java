package com.levemus.gliderhud.FlightData;

/**
 * Created by mark@levemus on 15-12-05.
 */
import java.util.EnumSet;

public interface IFlightData {

    public double getData(FlightDataType type) throws java.lang.UnsupportedOperationException;
    public enum FlightDataType {
        ALTITUDE, GROUNDSPEED, BEARING, YAW, VARIO, GLIDE, WINDSPEED, WINDDIRECTION;
        public static final EnumSet<FlightDataType> ALL_OPTS = EnumSet.allOf(FlightDataType.class);
    }
}
