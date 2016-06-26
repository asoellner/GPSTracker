package com.soellner.gpstracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
    //private String SERVER_URL = "http://192.168.1.139:8080/SampleApp/greeting/checkLogin";

    //work
    //private String SERVER_URL = "http://172.20.3.52:8080/SampleApp/greeting/checkLogin";

    //home_server
    private String SERVER_URL = "http://xxxxx.dyndns.org:8080/SampleApp/greeting/checkLogin";

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

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                    alertDialog.setTitle("Test Connection");
                    alertDialog.setMessage("Successfull");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Editable ukucanoIme = input.getText();
                            //finish();
                        }
                    });

// Setting Negative "Cancel" Button
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                            //dialog.cancel();
                        }
                    });

                    alertDialog.show();

                    //Toast.makeText(getApplicationContext(),
                    //       "successfull", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                    alertDialog.setTitle("Test Connection");
                    alertDialog.setMessage("not Successfull");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Editable ukucanoIme = input.getText();
                            //finish();
                        }
                    });

// Setting Negative "Cancel" Button
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();

                    //Toast.makeText(getApplicationContext(),
                    //        "not successfull", Toast.LENGTH_LONG).show();
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

        boolean success = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(SERVER_URL);
        post.setHeader("content-type", "application/json");

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        assert usernameEditText != null;
        String username = usernameEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        assert passwordEditText != null;
        String password = passwordEditText.getText().toString();
        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();
        try {
            dato.put("username", username);
            dato.put("password", password);


            StringEntity entity = new StringEntity(dato.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);
            String respStr = EntityUtils.toString(resp.getEntity());

            if (respStr.equals("ok"))
                success = true;


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return success;
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
