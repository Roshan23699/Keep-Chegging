package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ConfigActivity extends AppCompatActivity {
    ListView activityConfig;
    ArrayList<String>settingsList;
    ArrayAdapter<String>arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        activityConfig = findViewById(R.id.SettingsList);
        settingsList = new ArrayList<>();
        settingsList.add("Accounts");
        settingsList.add("Add New Account");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, settingsList);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                }
                else if(position == 1) {
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            }
        };
        activityConfig.setOnItemClickListener(onItemClickListener);
        activityConfig.setAdapter(arrayAdapter);

    }
}