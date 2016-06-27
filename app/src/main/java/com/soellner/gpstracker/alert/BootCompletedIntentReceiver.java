package com.soellner.gpstracker.alert;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.soellner.gpstracker.GMailSender;

import java.util.Calendar;

/**
 * Created by Alex on 19.06.2016.
 */
public class BootCompletedIntentReceiver extends WakefulBroadcastReceiver {


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        //new SendStartingMailTask().execute();

      /*  AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        //Boolean currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);
        //long timeInMillis = (System.currentTimeMillis() + 1200000) / 1000 * 1000;     //> 20 min
        //long timeInMillis = (System.currentTimeMillis() + 960000) / 1000 * 1000;     //> 16 min
        long timeInMillis = (System.currentTimeMillis() + 300000) / 1000 * 1000;     //> 5 min

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                timeInMillis,
                pendingIntent);

*/

        //Intent pushIntent = new Intent(context, GpsTrackerReceiver.class);
        //context.startService(pushIntent);

        Log.e("BootReceiver", "BootCompletedIntentReceiver.onReceive");
        Intent gpsTrackerIntent = new Intent(context, GPSTracker.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, gpsTrackerIntent, 0);

        final int SDK_INT = Build.VERSION.SDK_INT;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long timeInMillis = (System.currentTimeMillis() + 960000) / 1000 * 1000;     //> 16 min

        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("BootReceiver", ">=Build.VERSION_CODES.M");
            Calendar calendar = Calendar.getInstance();
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        }

    }

    private class SendStartingMailTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                GMailSender sender = new GMailSender("@gmx.net", "!");
                sender.sendMail("Service start from Boot",
                        "Service start from Boot",
                        "@gmx.net",
                        "@-.com");
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
