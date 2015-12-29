package com.levemus.gliderhud.FlightData.Listeners.Factory.Builder;

import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IAdjuster;
import com.levemus.gliderhud.FlightData.Listeners.Factory.Builder.Operations.IConverter;
import com.levemus.gliderhud.FlightDisplay.IClient;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by markcarter on 15-12-28.
 */
public class ListenerFrameConfig {
    public UUID mId = UUID.randomUUID();
    public HashSet<UUID> mChannels = new HashSet<>();
    public long mNotificationInterval = 500;
    public IConverter mConverter = null;
    public List<IAdjuster> mAdjusters = null;
    public IClient mClient = null;
}
