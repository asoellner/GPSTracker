package com.soellner.gpstracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_PERMISSION = 3;
    double _latitude;
    double _longitude;
    //keller
    //private String SERVER_URL="http://192.168.1.124:8080/SampleApp/greeting/crunchifyService";

    //henny
    private String SERVER_URL = "http://192.168.1.139:8080/SampleApp/greeting/saveLocation";

    //work
    //private String SERVER_URL = "http://172.20.3.52:8080/SampleApp/greeting/crunchifyService";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button startButton = (Button) findViewById(R.id.startButton);
        assert startButton != null;
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startService(new Intent(getBaseContext(), GPSTrackerService.class));

            }


        });

        Button stopButton = (Button) findViewById(R.id.stopButton);
        assert stopButton != null;
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(new Intent(getBaseContext(), GPSTrackerService.class));

            }


        });




        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        assert settingsButton != null;
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

                startActivity(intent);


            }


        });


        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        GPSTracker gps = new GPSTracker(locationManager, getBaseContext());
        // check if GPS enabled
        if (gps.canGetLocation() && gps.getLatitude() != 0.0) {
            _latitude = gps.getLatitude();
            _longitude = gps.getLongitude();

            // new UploadLocation().execute();


            //tv.setText("Latitude:" + latitude + ", Longitude:" + longitude);

        }


    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        String textResult;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                GMailSender sender = new GMailSender("", "");
                sender.sendMail("This is Subject",
                        "This is Body",
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


    private class UploadLocation extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        private byte[] _byteArray;

        @Override
        protected void onPreExecute() {
            try {
                pDialog = new ProgressDialog(MainActivity.this);


                //pDialog.setMessage("Uploading " + size);

            } catch (Exception e) {
                e.printStackTrace();
            }


            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {


                JSONObject obj = new JSONObject();

                obj.put("latitude", _latitude);
                obj.put("longitude", _longitude);
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

        protected void onPostExecute(String params) {
            super.onPostExecute(params);
            //pDialog.dismiss();
            //TextView resultText = (TextView) findViewById(R.id.resultText);
            //resultText.setTextColor(Color.rgb(0, 240, 0));
            //resultText.setTypeface(null, Typeface.BOLD);
            //resultText.setText("Upload successful");


        }

    }

}
