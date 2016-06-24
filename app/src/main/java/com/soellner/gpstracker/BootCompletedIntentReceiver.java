package com.soellner.gpstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Alex on 19.06.2016.
 */
public class BootCompletedIntentReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

       Intent srvIntent = new Intent(context, GPSTrackerService.class);
        PendingIntent pIntent = PendingIntent.getService(context, 0, srvIntent, 0);

        final int SDK_INT = Build.VERSION.SDK_INT;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //long timeInMillis = (System.currentTimeMillis() + 1200000) / 1000 * 1000;     //> example
        long timeInMillis = (System.currentTimeMillis() + 300000) / 1000 * 1000;     //> example

        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pIntent);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pIntent);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pIntent);
        }
       // new SendStartingMailTask().execute();


        startWakefulService(context, srvIntent);
    }

    private class SendStartingMailTask extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... params) {
            try {
                GMailSender sender = new GMailSender("", "!");
                sender.sendMail("Service Start",
                        "Service Start",
                        "",
                        "");
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
