package com.levemus.gliderhud.FlightDisplay;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.levemus.gliderhud.FlightData.Processors.Processor;
import com.levemus.gliderhud.FlightData.Services.FlightDataService;
import com.levemus.gliderhud.Messages.ChannelMessages.Data.DataMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mark@levemus on 15-12-20.
 */

public abstract class FlightDisplay extends Fragment  {

    private final String TAG = this.getClass().getSimpleName();

    protected HashMap<UUID, Processor> mProcessors = new HashMap<>();
    protected HashMap<UUID, Object> mResults = new HashMap();

    protected long mTimeOfLastUpdate = 0;

    private boolean mActive = false;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mActive)
                return;
            mActive = true;
            Bundle bundle = intent.getBundleExtra("MSG");
            DataMessage message = (DataMessage)bundle.getSerializable("MSG");
            new AsyncTask<DataMessage,Void,Void>(){
                @Override
                protected Void doInBackground(DataMessage... params) {
                    for(Processor processor : mProcessors.values()) {
                        Object result = processor.onMsg(params[0]);
                        if(processor.isValid(result)) {
                            mResults.put(processor.id(), result);
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void res) {
                    long currentTime = new Date().getTime();
                    if(currentTime - mTimeOfLastUpdate > refreshPeriod()) {
                        update();
                        mTimeOfLastUpdate = currentTime;
                    }
                    mActive = false;
                }
            }.execute(message);
        }
    };

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(FlightDataService.intentFilter()));
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    protected int refreshPeriod() { return 500; }

    protected void update() {}
}
