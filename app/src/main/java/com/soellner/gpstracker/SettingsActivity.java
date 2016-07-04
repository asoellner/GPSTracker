package com.soellner.gpstracker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SettingsActivity extends AppCompatActivity {
    static final int REQUEST_PERMISSION = 3;
    private static final String TAG = "SettingsActivity";

    //keller
    //private String SERVER_URL="http://192.168.1.124:8080/SampleApp/greeting/crunchifyService";

    //henny
    //private String SERVER_URL = "http://192.168.1.139:8080/SampleApp/greeting/checkLogin";

    //work
    //private String SERVER_URL = "http://172.20.3.52:8080/SampleApp/greeting/checkLogin";

    //home_server
    private String SERVER_URL = "http://xxxxx.dyndns.org:8080/services/main/checkLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences settings = getSharedPreferences("com.soellner.gpstracker.prefs", 0);
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
                        }
                    });

                    alertDialog.show();


                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                    alertDialog.setTitle("Test Connection");
                    alertDialog.setMessage("not Successfull");

                    alertDialog.show();


                }
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveButton);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                SharedPreferences settings = getSharedPreferences("com.soellner.gpstracker.prefs", 0);
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

        JSONObject user = new JSONObject();
        try {
            user.put("username", username);
            user.put("password", password);


            StringEntity entity = new StringEntity(user.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);
            String respStr = EntityUtils.toString(resp.getEntity());

            if (respStr.equals("ok"))
                success = true;


        } catch (JSONException e) {
            Log.e(TAG, "ERROR", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "ERROR", e);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "ERROR", e);
        } catch (IOException e) {
            Log.e(TAG, "ERROR", e);
        }


        return success;
    }



}
