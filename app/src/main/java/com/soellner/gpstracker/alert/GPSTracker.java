package com.soellner.gpstracker.alert;

/**
 * Created by Alex on 19.06.2016.
 */

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.soellner.gpstracker.GMailSender;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class GPSTracker extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "GPSTracker";

    //keller
    //private String SERVER_URL="http://192.168.1.124:8080/SampleApp/greeting/crunchifyService";

    //henny
    //private String SERVER_URL = "http://192.168.1.139:8080/SampleApp/greeting/saveLocation";

    //work
    // private String SERVER_URL = "http://172.20.3.52:8080/SampleApp/greeting/saveLocation";

    //work
    private String SERVER_URL = "http://xxx.dyndns.org:8080/SampleApp/greeting/saveLocation";


    private boolean currentlyProcessingLocation = false;
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        //if (!currentlyProcessingLocation) {
         //   currentlyProcessingLocation = true;
            startTracking();
        //}

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            Log.e(TAG, "unable to connect to google play services.");
            return;
        }


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }

    }

    protected void sendLocationDataToWebsite(Location location) {

        SharedPreferences settings = getSharedPreferences("com.soellner.gpstracker.prefs", 0);
        boolean enableService = settings.getBoolean("enableService", false);

        //check if service is disabled
        if (!enableService) {
            return;
        }

        Log.d(TAG, "sendLocationDataToWebsite");


        SharedPreferences sharedPreferences = this.getSharedPreferences("com.soellner.gpstracker.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        float totalDistanceInMeters = sharedPreferences.getFloat("totalDistanceInMeters", 0f);

        boolean uploadGPS = false;
        boolean firstTimeGettingPosition = sharedPreferences.getBoolean("firstTimeGettingPosition", true);

        if (firstTimeGettingPosition) {
            editor.putBoolean("firstTimeGettingPosition", false);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(sharedPreferences.getFloat("previousLatitude", 0f));
            previousLocation.setLongitude(sharedPreferences.getFloat("previousLongitude", 0f));

            float distance = location.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            editor.putFloat("totalDistanceInMeters", totalDistanceInMeters);
            if (totalDistanceInMeters > 20.0f) {
                uploadGPS = true;
            }
        }


        editor.putFloat("previousLatitude", (float) location.getLatitude());
        editor.putFloat("previousLongitude", (float) location.getLongitude());
        editor.apply();

        //only send GPS if location has changed
        if (uploadGPS) {
            uploadLocation(location);
            GpsInfos gpsInfos = new GpsInfos(location.getLatitude() + "", location.getLongitude() + "");
            new SendStartingMailTask().execute(gpsInfos);

        } else {
            Log.d(TAG, "location not changed!");
        }



    }

    private void uploadLocation(Location location) {
        GpsInfos gpsInfos = new GpsInfos(location.getLatitude() + "", location.getLongitude() + "");
        new UploadLocationTask().execute(gpsInfos);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        if (location != null) {
            Log.d(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location.getAccuracy() < 500.0f) {
                stopLocationUpdates();
                sendLocationDataToWebsite(location);
            }
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection has been suspend");
    }

    private class SendStartingMailTask extends AsyncTask<GpsInfos, Void, Void> {


        @Override
        protected Void doInBackground(GpsInfos... params) {
            try {
                String latitude = params[0]._latitude;
                String longitude = params[0]._longitude;

                GMailSender sender = new GMailSender("@gmx.net", "!");
                sender.sendMail("GPS Info send",
                        "Latidude: " + latitude + "  Longitude: " + longitude,
                        "@gmx.net",
                        "@-.");
            } catch (Exception e) {
                Log.d("SendMail", e.getMessage(), e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }

    private static class GpsInfos {
        String _latitude;
        String _longitude;

        public GpsInfos(String _latitude, String _longitude) {
            this._latitude = _latitude;
            this._longitude = _longitude;
        }


    }


    private class UploadLocationTask extends AsyncTask<GpsInfos, Void, Void> {


        @Override
        protected Void doInBackground(GpsInfos... params) {


            try {
                Log.d(TAG, "uploadStart");

                String latitude = params[0]._latitude;
                String longitude = params[0]._longitude;
                JSONObject obj = new JSONObject();

                obj.put("latitude", latitude);
                obj.put("longitude", longitude);
                SharedPreferences settings = getSharedPreferences("com.soellner.gpstracker.prefs", 0);
                String username = settings.getString("Username", "");
                String password = settings.getString("Password", "");
                obj.put("username", username);
                obj.put("password", password);
                Log.d(TAG, "upload User" + username + " pass: " + password + "  Latidude: " + latitude + "  Longitude: " + longitude);


                URL url = new URL(SERVER_URL);

                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(obj.toString());
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));


                in.close();
                Log.d(TAG, "upload success");


            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
            }


            return null;
        }


    }


}