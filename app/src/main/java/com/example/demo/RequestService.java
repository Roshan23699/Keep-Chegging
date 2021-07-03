package com.example.demo;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
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

import static android.os.SystemClock.uptimeMillis;
import static com.example.demo.App.CHANNEL_ID;
import static com.example.demo.App.default_channel;
import static com.example.demo.MainActivity.PACKAGE_NAME_TEMP;
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
                        if(!(getRunningStatus(0) || getRunningStatus(1) || getRunningStatus(2)))onDestroy();

                        time = uptimeMillis();
                        if(time - TIME > 7200000) {
                            textTitle = "Stopped Checking Questions";
                            textContent = "Please restart the checking of questions in order to get notified";
                            notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                            Notification notification1 = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.notification)
                                    .setContentTitle(textTitle)
                                    .setContentText(textContent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                                    .build();
                            notificationManagerCompat.notify(4, notification1);
                            onDestroy();
                        }
                        if(STOP_NOW)break;
                        Log.d(TAG, "run: service running");
                        for(int i = 0; i < 3; i++) {
                                if(!getRunningStatus(i))continue;
                            try {
                                RequestQueue requestQueue = Volley.newRequestQueue(RequestService.this);
                                String URL = "http://3.143.169.217:3000/api/";
                                JSONObject jsonBody = new JSONObject();
                                SharedPreferences getShared = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
                                String email, password;
                                email = getShared.getString(PACKAGE_NAME_TEMP+".email"+i, "");
                                password = getShared.getString(PACKAGE_NAME_TEMP+".password"+i, "");
                                jsonBody.put("username", email);
                                jsonBody.put("password", password);
                                Log.d(TAG, "onStartCommand: email is " + email + "password is " + password);
                                final String requestBody = jsonBody.toString();
                                int finalI = i;
                                StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i("VOLLEY", "the response is " + response);
                                        String textTitle = "", textContent = "";
                                        NotificationManagerCompat notificationManagerCompat;
                                        if (response.equals("available")) {
                                            Log.d(TAG, "onResponse: Working inside notification now" + " " + available);
                                            textTitle = email;
                                            textContent = "Got new chegg question 10 mins left...";
                                            notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.notification)
                                                    .setContentTitle(textTitle)
                                                    .setContentText(textContent)
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                                                    .build();
                                            notificationManagerCompat.notify(finalI, notification);

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
                                };
                                stringRequest1.setRetryPolicy(new DefaultRetryPolicy(
                                        MY_SOCKET_TIMEOUT_MS,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                requestQueue.add(stringRequest1);
                            } catch (JSONException e) {
                                e.printStackTrace();
//                            stopSelf();
                            }

                            //1st request ends here
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }



                        //sleep the thread
                        try {
                            Thread.sleep(1000 * 60 * 10);
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
    private boolean getRunningStatus(int i){
        //save
        SharedPreferences accounts = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        return accounts.getBoolean(PACKAGE_NAME_TEMP+".running" + i, false);

    }
}
