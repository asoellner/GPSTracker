package com.soellner.gpstracker.alert;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.soellner.gpstracker.GMailSender;

/**
 * Created by Alex on 26.06.2016.
 */
public class GpsTrackerReceiver extends Service {
    private static final String TAG = "GpsReceiver";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("GpsTrackerReceiver", "onStartCommand");
        //new SendStartingMailTask().execute();




        return START_NOT_STICKY;
    }

    private class SendStartingMailTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                GMailSender sender = new GMailSender("@.net", "!");
                sender.sendMail("Service Running",
                        "Service running",
                        "",
                        "@-.");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }

}