package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.demo.adapter.RecyclerViewAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.example.demo.MainActivity.PACKAGE_NAME_TEMP;

public class SettingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Account>Accounts;
    private ArrayAdapter<String>arrayAdapter;
    private String deletedAccount;
    private Account RestoreAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Accounts = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        int no = sharedPreferences.getInt(PACKAGE_NAME_TEMP+".accounts", 0);
        for(int i = 0; i < no; i++) {
            String email = sharedPreferences.getString(PACKAGE_NAME_TEMP+".email"+i, "");
            String password = sharedPreferences.getString(PACKAGE_NAME_TEMP+".password"+i, "");
            String status = "";
            try {
                status = getRunningStatus(email) ? "Checking" : "Not Checking";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Accounts.add(new Account(email, status, password));
        }
        recyclerViewAdapter = new RecyclerViewAdapter(SettingsActivity.this, Accounts);
        recyclerView.setAdapter(recyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



    }
    //
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            if(direction == ItemTouchHelper.LEFT) {
                int position = viewHolder.getAdapterPosition();
                deletedAccount = Accounts.get(position).getEmail();
                RestoreAccount = Accounts.get(position);
                Accounts.remove(viewHolder.getAdapterPosition());
                removeAccount(position);
                recyclerViewAdapter.notifyItemRemoved(position);
                Snackbar.make(recyclerView, deletedAccount, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            RestoreAccount = new Account(deletedAccount, getRunningStatus(deletedAccount) ? "Checking" : "Not Checking", getPassword(deletedAccount));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Accounts.add(position, RestoreAccount);
                        addAccount(position, RestoreAccount);
                        recyclerViewAdapter.notifyItemInserted(position);

                    }
                }).show();

            }
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(SettingsActivity.this, R.color.color_red))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    //helper methods
    public void changeStatus() {
        recyclerViewAdapter.notify();
    }
    private boolean getRunningStatus(String email) throws Exception {
        //save
        SharedPreferences accounts = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        int no_of_accounts = accounts.getInt(PACKAGE_NAME_TEMP+".accounts", 0);
        int number = no_of_accounts;
        for(int i = 0; i < no_of_accounts; i++) {
            if(accounts.getString(PACKAGE_NAME_TEMP + ".email" + i, "").equals(email)) {
                number = i;
                break;
            }
        }
        if(number == no_of_accounts) {
            Exception e = new Exception("email not found");
            throw e;
        }
        return accounts.getBoolean(PACKAGE_NAME_TEMP+".running" + number, false);

    }
    private String getPassword(String email) throws Exception {
        //save
        SharedPreferences accounts = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        int no_of_accounts = accounts.getInt(PACKAGE_NAME_TEMP+".accounts", 0);
        int number = no_of_accounts;
        for(int i = 0; i < no_of_accounts; i++) {
            if(accounts.getString(PACKAGE_NAME_TEMP + ".email" + i, "").equals(email)) {
                number = i;
                break;
            }
        }
        if(number == no_of_accounts) {
            Exception e = new Exception("email not found");
            throw e;
        }
        return accounts.getString(PACKAGE_NAME_TEMP+".password" + number, "");

    }
    private void removeAccount(int position) {
        //save
        SharedPreferences accounts = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        int number = accounts.getInt(PACKAGE_NAME_TEMP+".accounts", 0);
        SharedPreferences.Editor editor = accounts.edit();
        if(position == 0) {

            for(int i = 1; i < number; i++) {
                editor.putBoolean(PACKAGE_NAME_TEMP+".running"+(i - 1), accounts.getBoolean(PACKAGE_NAME_TEMP+".running"+i, false));
                editor.putString(PACKAGE_NAME_TEMP+".email"+(i - 1), accounts.getString(PACKAGE_NAME_TEMP+".email"+i, ""));
                editor.putString(PACKAGE_NAME_TEMP+".password"+(i - 1), accounts.getString(PACKAGE_NAME_TEMP+".password"+i, ""));

            }
            editor.putInt(PACKAGE_NAME_TEMP+".accounts", number - 1);
            editor.apply();
        }
        if(position == 1) {
            for(int i = 2; i < number; i++) {
                editor.putBoolean(PACKAGE_NAME_TEMP+".running"+(i - 1), accounts.getBoolean(PACKAGE_NAME_TEMP+".running"+i, false));
                editor.putString(PACKAGE_NAME_TEMP+".email"+(i - 1), accounts.getString(PACKAGE_NAME_TEMP+".email"+i, ""));
                editor.putString(PACKAGE_NAME_TEMP+".password"+(i - 1), accounts.getString(PACKAGE_NAME_TEMP+".password"+i, ""));

            }
            editor.putInt(PACKAGE_NAME_TEMP+".accounts", number - 1);
            editor.apply();
        }
        if(position == 2) {
            editor.putInt(PACKAGE_NAME_TEMP+".accounts", number - 1);
            editor.apply();
        }


    }
    private void addAccount(int position, Account account) {
        //save
        SharedPreferences accounts = getSharedPreferences(PACKAGE_NAME_TEMP, MODE_PRIVATE);
        int number = accounts.getInt(PACKAGE_NAME_TEMP+".accounts", 0);
        SharedPreferences.Editor editor = accounts.edit();
        if(position == 0) {

            for(int i = 0; i < number; i++) {
                editor.putBoolean(PACKAGE_NAME_TEMP+".running"+(i + 1), accounts.getBoolean(PACKAGE_NAME_TEMP+".running"+(i), false));
                editor.putString(PACKAGE_NAME_TEMP+".email"+(i + 1), accounts.getString(PACKAGE_NAME_TEMP+".email" + (i), ""));
                editor.putString(PACKAGE_NAME_TEMP+".password"+(i + 1), accounts.getString(PACKAGE_NAME_TEMP+".password" + (i), ""));
            }
            editor.putInt(PACKAGE_NAME_TEMP+".accounts", number + 1);


            //
            editor.putBoolean(PACKAGE_NAME_TEMP+".running"+(0), account.getStatus().equals("Checking") ? true : false);
            editor.putString(PACKAGE_NAME_TEMP+".email"+(0), account.getEmail());
            editor.putString(PACKAGE_NAME_TEMP+".password"+(0), account.getPassword());
            editor.apply();
        }
        if(position == 1) {
            for(int i = 1; i < number; i++) {
                editor.putBoolean(PACKAGE_NAME_TEMP+".running"+(i + 1), accounts.getBoolean(PACKAGE_NAME_TEMP+".running"+(i), false));
                editor.putString(PACKAGE_NAME_TEMP+".email"+(i + 1), accounts.getString(PACKAGE_NAME_TEMP+".email"+(i), ""));
                editor.putString(PACKAGE_NAME_TEMP+".password"+(i + 1), accounts.getString(PACKAGE_NAME_TEMP+".password"+(i), ""));

            }
            //
            editor.putBoolean(PACKAGE_NAME_TEMP+".running"+(1), account.getStatus().equals("Checking") ? true : false);
            editor.putString(PACKAGE_NAME_TEMP+".email"+(1), account.getEmail());
            editor.putString(PACKAGE_NAME_TEMP+".password"+(1), account.getPassword());
            editor.putInt(PACKAGE_NAME_TEMP+".accounts", number + 1);
            editor.apply();
        }
        if(position == 2) {
            editor.putBoolean(PACKAGE_NAME_TEMP+".running"+(2), account.getStatus().equals("Checking") ? true : false);
            editor.putString(PACKAGE_NAME_TEMP+".email"+(2), account.getEmail());
            editor.putString(PACKAGE_NAME_TEMP+".password"+(2), account.getPassword());
            editor.putInt(PACKAGE_NAME_TEMP+".accounts", number + 1);
            editor.apply();
        }


    }
}