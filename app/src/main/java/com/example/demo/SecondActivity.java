package com.example.demo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import static com.example.demo.MainActivity.PACKAGE_NAME_TEMP;

public class SecondActivity<Public> extends AppCompatActivity {
    private static final int MY_SOCKET_TIMEOUT_MS = 60000 * 6;
    public static final int notificationId = 23323;
    public static String TAG = "hello";
    private WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        int account_no = sharedPreferences.getInt(PACKAGE_NAME_TEMP + ".accounts", 0);
        if(account_no == 0) {
            startActivity(new Intent(this, MainActivity.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //setting listener
        Button stopChecking = findViewById(R.id.stopBtn);
        stopChecking.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences1 = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            for(int i = 0; i < 3; i++) {
                editor.putBoolean(PACKAGE_NAME_TEMP + ".running" + i, false);
            }
            editor.apply();
            stopService(new Intent(getBaseContext(), RequestService.class));
            Toast toast = Toast.makeText(getBaseContext(), "Checking stopped: you can exit", Toast.LENGTH_LONG);
            toast.show();
        }
        });
        Button startChecking = findViewById(R.id.startChecking);
        startChecking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences1 = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences1.edit();
                boolean require = false;
                for(int i = 0; i < 3; i++) {
                    boolean s = sharedPreferences1.getBoolean(PACKAGE_NAME_TEMP + ".running" + i, true);
                    if(!s)require = true;
                }
                if(require) {
                    for (int i = 0; i < 3; i++) {
                        editor.putBoolean(PACKAGE_NAME_TEMP + ".running" + i, true);
                    }
                    editor.apply();
                    startService(new Intent(getBaseContext(), RequestService.class));
                }
                Toast toast = Toast.makeText(getBaseContext(), "Checking Started for all accounts", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        Button gotoSettings = findViewById(R.id.settings);
        gotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondActivity.this, ConfigActivity.class));
            }
        });

    }



}