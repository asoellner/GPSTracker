package com.soellner.gpstracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.soellner.gpstracker.alert.GPSTracker;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_PERMISSION = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button startButton = (Button) findViewById(R.id.startButton);
        assert startButton != null;
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("com.soellner.gpstracker.prefs", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("enableService", true);
                TextView statusText = (TextView) findViewById(R.id.serviceStatusText);
                assert statusText != null;
                statusText.setText(R.string.serviceRunning);
                statusText.setTextColor(Color.parseColor("#00FF00"));
                editor.apply();
                startService(new Intent(getBaseContext(), GPSTracker.class));

            }


        });

        Button stopButton = (Button) findViewById(R.id.stopButton);
        assert stopButton != null;
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("com.soellner.gpstracker.prefs", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("enableService", false);
                editor.apply();

                TextView statusText = (TextView) findViewById(R.id.serviceStatusText);
                assert statusText != null;
                statusText.setText(R.string.serviceStopped);
                statusText.setTextColor(Color.parseColor("#FF0000"));


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


        //check permissions
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
        }


        SharedPreferences settings = getSharedPreferences("com.soellner.gpstracker.prefs", 0);
        boolean enableService = settings.getBoolean("enableService", false);
        TextView statusText = (TextView) findViewById(R.id.serviceStatusText);
        if (enableService) {
            assert statusText != null;
            statusText.setText(R.string.serviceRunning);
            statusText.setTextColor(Color.parseColor("#00FF00"));
        } else {
            assert statusText != null;
            statusText.setText(R.string.serviceStopped);
            statusText.setTextColor(Color.parseColor("#FF0000"));
        }


    }


}
