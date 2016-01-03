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

import com.levemus.gliderhud.FlightData.Providers.Provider;
import com.levemus.gliderhud.FlightData.Providers.Test.TestProvider;
import com.levemus.gliderhud.FlightData.Providers.Bluetooth.BluetoothProvider;
import com.levemus.gliderhud.FlightData.Managers.DataManager;
import com.levemus.gliderhud.FlightDisplay.IFlightDisplay;
import com.levemus.gliderhud.FlightDisplay.MainDisplay;

/**
 * Created by mark@levemus on 15-11-23.
 */

@SuppressLint("NewApi")
public class HUDActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();

	HUD mHUD = new HUD();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		mHUD.init(this);
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		mHUD.start();
		mHUD.resume();
	}

	@Override
	public void onPause()  {
		Log.d(TAG, "onPause");
		mHUD.pause();
		mHUD.stop();

		super.onPause();
	}

	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy");
		mHUD.deInit();
		super.onDestroy();
	}
}
