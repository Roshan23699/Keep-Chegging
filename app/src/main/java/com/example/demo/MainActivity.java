package com.example.demo;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static com.example.demo.App.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    public int BACK = 0;
    public final static String PACKAGE_NAME_TEMP = "com.example.demo.accounts";
    NotificationManagerCompat notificationManagerCompat;

    public void submit(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        if(email.length() == 0 || password.length() == 0) {
            Toast.makeText(this, "Please fill the details", Toast.LENGTH_SHORT).show();
            return;
        }

        //save
        int status = saveAccount(email.getText().toString(), password.getText().toString());
        if(status == -1)return;
        //change activity
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        if(sharedPreferences.getInt(PACKAGE_NAME_TEMP + ".accounts", 0) == 0) {
            if(BACK == 0) {
                Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show();
                BACK++;
                return;
            }
            else {
                BACK = 0;
                finish();
            }
        }

        super.onBackPressed();
    }

    private int saveAccount(String email, String password) {
        //save
        SharedPreferences accounts = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        int no_of_accounts = accounts.getInt(PACKAGE_NAME_TEMP+".accounts", 0);
        if(no_of_accounts == 3) {
            Toast.makeText(this, "Maximum Accounts limit reached", Toast.LENGTH_LONG).show();
            return -1;
        }
        int number = no_of_accounts;
        for(int i = 0; i < no_of_accounts; i++) {
            String e = accounts.getString(PACKAGE_NAME_TEMP+".email"+i, "");
            if(email.equals(e)){
                Toast.makeText(this, "Cannot add same email twice", Toast.LENGTH_SHORT).show();
                return -1;
            }
        }


        SharedPreferences.Editor editor = accounts.edit();
        editor.putInt(PACKAGE_NAME_TEMP+".accounts",(number + 1));
        editor.putString(PACKAGE_NAME_TEMP+".email"+number, email);
        editor.putString(PACKAGE_NAME_TEMP+".password"+number, password);
        editor.apply();
        return 0;
    }

}