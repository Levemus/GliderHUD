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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Services.FlightDataService;

/**
 * Created by mark@levemus on 15-11-23.
 */

@SuppressLint("NewApi")
public class HUDActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(com.levemus.gliderhud.R.layout.hud);
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		startService(new Intent(this, FlightDataService.class));
		super.onResume();
	}

	@Override
	public void onPause()  {
		Log.d(TAG, "onPause");
		stopService(new Intent(this, FlightDataService.class));
		super.onPause();
	}

	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}
}
