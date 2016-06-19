package com.soellner.gpstracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * Created by Alex on 19.06.2016.
 */
public class GPSTrackerService extends Service {

    //keller
    //private String SERVER_URL="http://192.168.1.124:8080/SampleApp/greeting/crunchifyService";

    //henny
    private String SERVER_URL = "http://192.168.1.139:8080/SampleApp/greeting/saveLocation";

    //work
    //private String SERVER_URL = "http://172.20.3.52:8080/SampleApp/greeting/crunchifyService";

    public IBinder onBind(Intent intent) {
        // Fuer dieses Tutorial irrelevant. Gehoert zu bounded Services.

        return null;
    }


    @Override
    public void onCreate() {
        Log.v("GPSTrackerService", System.currentTimeMillis()
                + ": GPSTrackerService created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            GPSTracker gpsTracker = new GPSTracker(locationManager, getBaseContext());


            if (gpsTracker.canGetLocation()) {


                MyTaskParams params = new MyTaskParams(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                new UploadLocation().execute(params);

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }


        new SendMail().execute();


/*

        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),"BUMMMM",Toast.LENGTH_LONG).show();
            }
        });

*/
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service killed", Toast.LENGTH_LONG).show();
        Log.v("GPSTrackerService", System.currentTimeMillis()
                + ": GPSTrackerService killed.");
    }

    private class SendMail extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {


            try {
                GMailSender sender = new GMailSender("", "!");
                sender.sendMail("Send GPS!",
                        "Successfull GPS send",
                        "",
                        "a");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }

    private class UploadLocation extends AsyncTask<MyTaskParams, Void, Void> {

        private byte[] _byteArray;

        @Override
        protected Void doInBackground(MyTaskParams... params) {


            try {

                double latitude = params[0]._latitude;
                double longitude = params[0]._longitude;
                JSONObject obj = new JSONObject();

                obj.put("latitude", latitude);
                obj.put("longitude", longitude);
                obj.put("userID", 0);


                //URL url = new URL("http://192.168.1.124:8080/SampleApp/greeting/crunchifyService");
                URL url = new URL(SERVER_URL);

                //URL url = new URL("http://172.20.3.52:8080/SampleApp/greeting/crunchifyService");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(obj.toString());
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));


                in.close();


            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }


    }

    private static class MyTaskParams {
        double _latitude;
        double _longitude;

        public MyTaskParams(double _latitude, double _longitude) {
            this._latitude = _latitude;
            this._longitude = _longitude;
        }
    }

}