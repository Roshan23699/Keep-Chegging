package com.example.demo;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import java.sql.Time;
import java.util.Calendar;

import static android.os.SystemClock.sleep;
import static android.os.SystemClock.uptimeMillis;
import static com.example.demo.App.CHANNEL_ID;
import static com.example.demo.App.default_channel;
import static com.example.demo.SecondActivity.TAG;

public class RequestService extends Service {

    public boolean STOP_NOW = false;
    private static final int MY_SOCKET_TIMEOUT_MS = 60000 * 6;
    long TIME, time;
    public short available = 3;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            TIME = uptimeMillis();
        Log.d(TAG, "run: time" + TIME);



        //use of volley
            new Thread(new Runnable() {

                @Override
                public void run() {

                    String textTitle = "Chegg";
                    String textContent = "Waiting for new questions";
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), default_channel)
                            .setSmallIcon(R.drawable.notification)
                            .setContentTitle(textTitle)
                            .setContentText(textContent)
                            .build();
                    startForeground(2, notification);
                    while(true) {
                        time = uptimeMillis();
                        if(time - TIME > 7200000)onDestroy();
                        if(STOP_NOW)break;

                        Log.d(TAG, "run: service running");
                        try {
                            RequestQueue requestQueue = Volley.newRequestQueue(RequestService.this);
                            String URL = "http://3.143.169.217:3000/api/";
                            JSONObject jsonBody = new JSONObject();
                            SharedPreferences getShared =   getSharedPreferences("demo", MODE_PRIVATE);
                            String email, password;
                            email = getShared.getString("com.example.demo.email", "");
                            password = getShared.getString("com.example.demo.password", "");

                            jsonBody.put("username", email);
                            jsonBody.put("password", password);
                            Log.d(TAG, "onStartCommand: email is " + email + "password is " + password);
                            final String requestBody = jsonBody.toString();

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("VOLLEY", "the response is " + response);
                                    String textTitle = "", textContent = "";
                                    NotificationManagerCompat notificationManagerCompat;
                                    if (response.equals("available")) {
                                        available = 1;
                                        Log.d(TAG, "onResponse: Working inside notification now" + " " + available);

                                        textTitle = "New Question";
                                        textContent = "Got new chegg question 10 mins left...";
                                        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                                .setSmallIcon(R.drawable.notification)
                                                .setContentTitle(textTitle)
                                                .setContentText(textContent)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                                .build();
                                        notificationManagerCompat.notify(1, notification);

                                    }
                                    else if(response.equals("noquestion")) {
//                                            textTitle = "Chegg";
//                                            textContent = "No question yet";
//                                            notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//                                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                                                    .setSmallIcon(R.drawable.notification)
//                                                    .setContentTitle(textTitle)
//                                                    .setContentText(textContent)
//                                                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                                                    .build();
//                                            notificationManagerCompat.notify(1, notification);
                                            available = 2;
                                    }
                                    else {
//                                        textTitle = "error";
//                                        textContent = response;
//                                        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//                                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                                                .setSmallIcon(R.drawable.notification)
//                                                .setContentTitle(textTitle)
//                                                .setContentText(textContent)
//                                                .setPriority(NotificationCompat.PRIORITY_LOW)
//                                                .build();
//                                        notificationManagerCompat.notify(1, notification);
                                        available = 2;
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("VOLLEY", error.toString());
//                                    stopSelf();
                                }
                            }) {
                                @Override
                                public String getBodyContentType() {
                                    return "application/json; charset=utf-8";
                                }

                                @Override
                                public byte[] getBody() throws AuthFailureError {
                                    try {
                                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                                    } catch (UnsupportedEncodingException uee) {
                                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                                        stopSelf();
                                        return null;
                                    }
                                }

//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }

                            };
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    MY_SOCKET_TIMEOUT_MS,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            requestQueue.add(stringRequest);
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            stopSelf();
                        }
                        try {
                            Log.d(TAG, "run: available" + available);
                            if(available == 3) {
                                int ii = 0;
                                while(true) {
                                    if(ii > 20) break;
                                    sleep(3000);
                                    if(available != 3)break;
                                    ii++;
                                }
                            }
                            if(available == 1) {
                                Log.d(TAG, "run: inside 10 minute sleep");
                                Thread.sleep(10 * 60 * 1000);
                            }
                            else {
                                Log.d(TAG, "run: inside 2 minute sleep");
                                Thread.sleep(2 * 60 * 1000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }


            }).start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroy");
        STOP_NOW = true;
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
