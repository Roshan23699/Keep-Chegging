package com.example.demo;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.view.View;

import android.os.Bundle;
import android.widget.EditText;

import static com.example.demo.App.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    static final String em = "email", pass= "password";
    NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onBackPressed() {
        finish();
    }

    public void submit(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra(em,  email.getText().toString());
        intent.putExtra(pass, password.getText().toString());
        startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}