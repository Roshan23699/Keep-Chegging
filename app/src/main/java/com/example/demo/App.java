package com.example.demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import static com.example.demo.MainActivity.PACKAGE_NAME_TEMP;

public class App extends Application {
    public static final String CHANNEL_ID = "chegg_notifications", default_channel = "service_channel";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        createDefaultChannel();
    }

    private void createDefaultChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel  = new NotificationChannel(
                    default_channel,
                    "Tracking",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
//        notificationChannel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });
//        notificationChannel.setLightColor(Color.RED);
            notificationChannel.setDescription("Waiting for the questions..");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel  = new NotificationChannel(
                    CHANNEL_ID,
                    "Chegg Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                    );
    //        notificationChannel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });
    //        notificationChannel.setLightColor(Color.RED);
            notificationChannel.setDescription("The chegg question notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

}
