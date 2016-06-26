package com.soellner.gpstracker.alert;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


import com.soellner.gpstracker.GMailSender;

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
    //private String SERVER_URL = "http://192.168.1.139:8080/SampleApp/greeting/saveLocation";

    //work
    private String SERVER_URL = "http://172.20.3.52:8080/SampleApp/greeting/saveLocation";

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

        new SendMail().execute();
       /* try {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            GPSTracker gpsTracker = new GPSTracker(locationManager, getBaseContext());


            if (gpsTracker.canGetLocation() && gpsTracker.getLatitude() != 0.0) {

                MyTaskParams params = new MyTaskParams(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                new UploadLocation().execute(params);

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
*/

        //new SendMail().execute();


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
                sender.sendMail("Trying to send GPS!",
                        "Trying to send GPS",
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

    private class UploadLocation extends AsyncTask<MyTaskParams, Void, Void> {


        @Override
        protected Void doInBackground(MyTaskParams... params) {


            try {

                double latitude = params[0]._latitude;
                double longitude = params[0]._longitude;
                JSONObject obj = new JSONObject();

                obj.put("latitude", latitude);
                obj.put("longitude", longitude);
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                String username = settings.getString("Username", "");
                String password = settings.getString("Password", "");
                obj.put("username", username);
                obj.put("password", password);

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