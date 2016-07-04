package com.soellner.gpstracker.alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Alex on 19.06.2016.
 */
public class BootCompletedIntentReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "BootCompletedIntentReceiver.onReceive");
        Intent gpsTrackerIntent = new Intent(context, GPSTracker.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, gpsTrackerIntent, 0);

        final int SDK_INT = Build.VERSION.SDK_INT;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long timeInMillis = (System.currentTimeMillis() + 1800000) / 1000 * 1000;     //> 30 min

        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.e("BootReceiver", "Build.VERSION_CODES.KITKAT");
            am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            Log.e("BootReceiver", "Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M");
            am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("BootReceiver", ">=Build.VERSION_CODES.M");
            Calendar calendar = Calendar.getInstance();
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        }

    }


}
