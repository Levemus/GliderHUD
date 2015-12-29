package com.levemus.gliderhud;

/*
 Both the author and publisher makes no representations or warranties
 about the suitability of this software, either expressed or implied, including
 but not limited to the implied warranties of merchantability, fitness
 for a particular purpose or noninfringement. Both the author and publisher
 shall not be liable for any damages suffered as a result of using,
 modifying or distributing the software or its derivatives.

 (c) 2015 Levemus Software, Inc.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Broadcasters.Broadcaster;
import com.levemus.gliderhud.FlightData.Broadcasters.IBroadcaster;
import com.levemus.gliderhud.FlightData.Broadcasters.Recon.HeadLocationDataBroadcaster;
import com.levemus.gliderhud.FlightData.Broadcasters.Test.TestFlightDataBroadcaster;
import com.levemus.gliderhud.FlightData.Broadcasters.Multiplexer;
import com.levemus.gliderhud.FlightDisplay.FlightDisplay;
import com.levemus.gliderhud.FlightDisplay.MainDisplay;

/**
 * Created by mark@levemus on 15-11-23.
 */
@SuppressLint("NewApi")
public class HUDActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();

	private Broadcaster[] mBroadcasterList = {
			new HeadLocationDataBroadcaster(),
			//new BluetoothBroadcaster(),
			//new InternalGPSFlightDataBroadcaster(),
			new TestFlightDataBroadcaster()
	};

	private FlightDisplay[] mDisplayList = {
			new MainDisplay()
	};

	private Multiplexer mMultiplexer = new Multiplexer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		for(Broadcaster broadcaster : mBroadcasterList) {
			broadcaster.init(this);
			mMultiplexer.registerWith(broadcaster);
		}

		for(FlightDisplay display : mDisplayList)
		{
			display.init(this);
			display.registerWith(mMultiplexer);
		}
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		for(IBroadcaster broadcaster : mBroadcasterList) {
			broadcaster.resume(this);
		}
	}

	@Override
	public void onPause()  {
		Log.d(TAG, "onPause");
		super.onPause();
		for(IBroadcaster broadcaster : mBroadcasterList) {
			broadcaster.pause(this);
		}
	}

	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}
}
