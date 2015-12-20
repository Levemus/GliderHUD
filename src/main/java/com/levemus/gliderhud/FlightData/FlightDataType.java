package com.levemus.gliderhud.FlightData;

import java.util.Arrays;
import java.util.UUID;
import java.util.HashSet;

/**
 * Created by markcarter on 15-12-16.
 */
public class FlightDataType {
    public static final UUID ALTITUDE = UUID.fromString("495a2826-1c5c-4d59-a101-86c2c78b1a6e");
    public static final UUID GROUNDSPEED = UUID.fromString("b7122024-efb5-40e1-b730-3e956551a234");
    public static final UUID BEARING = UUID.fromString("b9605f3d-0a75-4796-b6c7-f410dd569a3a");
    public static final UUID YAW = UUID.fromString("7cb2f9f4-eefb-4f3c-9d09-4e39cf81e063");
    public static final UUID VARIO = UUID.fromString("9a159d98-11e5-4656-8d86-4f36b174ace4");
    public static final UUID LATITUDE = UUID.fromString("76173e76-16a4-4177-85a0-7b2772069800");
    public static final UUID LONGITUDE = UUID.fromString("88c2f159-8d49-4895-8938-0e236b6197cb");

    public static final HashSet<UUID> ALL = new HashSet(Arrays.asList(
            LATITUDE,
            LONGITUDE,
            ALTITUDE,
            GROUNDSPEED,
            BEARING,
            YAW,
            VARIO
    ));

    public static final HashSet<UUID> NONE = new HashSet();
}