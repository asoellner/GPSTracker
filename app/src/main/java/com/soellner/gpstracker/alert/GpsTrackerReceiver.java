package com.soellner.gpstracker.alert;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Alex on 26.06.2016.
 */
public class GpsTrackerReceiver extends Service {
    private static final String TAG = "GpsTrackerReceiver";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }


}