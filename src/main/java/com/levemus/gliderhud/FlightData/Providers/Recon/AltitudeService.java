package com.levemus.gliderhud.FlightData.Providers.Recon;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2016 Levemus Software, Inc.
 */

import com.levemus.gliderhud.FlightData.Configuration.IChannelized;
import com.levemus.gliderhud.FlightData.Configuration.IIdentifiable;
import com.levemus.gliderhud.FlightData.Providers.MessageService;
import com.levemus.gliderhud.FlightData.Providers.ServiceProviderThread;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public class AltitudeService extends MessageService implements IIdentifiable, IChannelized {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public HashSet<UUID> channels() { return mWorkerThread.channels(); }

    @Override
    public UUID id() { return mWorkerThread.id(); }

    private AltitudeServiceThread mWorkerThread = new AltitudeServiceThread(TAG);
    protected ServiceProviderThread workerThread() {
        return mWorkerThread;
    }
}
