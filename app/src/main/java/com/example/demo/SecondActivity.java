package com.example.demo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.Clock;
import java.util.Calendar;

import static com.example.demo.App.CHANNEL_ID;

public class SecondActivity<Public> extends AppCompatActivity {
    private static final int MY_SOCKET_TIMEOUT_MS = 60000 * 6;
    public static final int notificationId = 23323;
    public static String TAG = "hello";
    private WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        deleteSharedPreferences("demo");
        Intent intent = getIntent();
        String email = intent.getStringExtra(MainActivity.em);
        String password = intent.getStringExtra(MainActivity.pass);
        SharedPreferences shrd = getSharedPreferences("demo", MODE_PRIVATE);
        SharedPreferences.Editor editor = shrd.edit();
        editor.putString("com.example.demo.email", email);
        editor.putString("com.example.demo.password", password);
        editor.putBoolean("com.example.demo.stop", false);
        editor.apply();
        //starting service here
        stopService(new Intent(getBaseContext(), RequestService.class));
        Intent serviceIntent = new Intent(this, RequestService.class);
        startService(serviceIntent);
        //setting listener
        Button stopChecking = findViewById(R.id.stopBtn);
        stopChecking.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopService(new Intent(getBaseContext(), RequestService.class));
            Toast toast = Toast.makeText(getBaseContext(), "Checking stopped", Toast.LENGTH_LONG);
            toast.show();
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }
    });

    }



}