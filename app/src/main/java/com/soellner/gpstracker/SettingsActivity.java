package com.soellner.gpstracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class SettingsActivity extends AppCompatActivity {
    static final int REQUEST_PERMISSION = 3;

    //keller
    //private String SERVER_URL="http://192.168.1.124:8080/SampleApp/greeting/crunchifyService";

    //henny
    private String SERVER_URL = "http://192.168.1.139:8080/SampleApp/greeting/print";

    //work
    //private String SERVER_URL = "http://172.20.3.52:8080/SampleApp/greeting/crunchifyService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String username = settings.getString("Username", "");
        String pass = settings.getString("Password", "");

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        assert usernameEditText != null;
        usernameEditText.setText(username);

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        assert passwordEditText != null;
        passwordEditText.setText(pass);


        Button backButton = (Button) findViewById(R.id.backButton);
        assert backButton != null;
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button testButton = (Button) findViewById(R.id.testButton);
        assert testButton != null;
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (testConnection()) {
                    Toast.makeText(getApplicationContext(),
                            "Test successfull", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Test NOT successfull", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveButton);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                assert usernameEditText != null;
                assert passwordEditText != null;
                editor.putString("Username", usernameEditText.getText().toString());
                editor.putString("Password", passwordEditText.getText().toString());
                editor.apply();
                onBackPressed();
            }
        });

    }

    private boolean testConnection() {
/*
        TestConnectionTask testConnectionTask = new TestConnectionTask();
        testConnectionTask.execute();
        return testConnectionTask.isSuccess();
*/

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_PERMISSION);
        }


        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(SERVER_URL);
            urlConnection = (HttpURLConnection)
                    urlToRequest.openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.setReadTimeout(5000);

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // handle unauthorized (if service requires user login)
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
            }

            // create JSON object from content
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());
            JSONObject jsonObject = new JSONObject(getResponseText(in));
            System.err.println("sss");

        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
        } catch (JSONException e) {
            // response body is no valid JSON string
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return false;
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    private class TestConnectionTask extends AsyncTask<Void, Void, Void> {

        boolean _success = false;

        public boolean isSuccess() {
            return _success;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {


                URL url = new URL(SERVER_URL);


                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setConnectTimeout(20000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = null;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    stringBuilder.append(line);
                }

                in.close();
                _success = true;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }

}
