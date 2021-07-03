package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.Account;
import static com.example.demo.MainActivity.PACKAGE_NAME_TEMP;
import com.example.demo.R;
import com.example.demo.RequestService;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Account> accounts;
    public RecyclerViewAdapter(Context context, List<Account>accounts) {
        this.context = context;
        this.accounts = accounts;

    }
    //where to get the single card as the viewholder object
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }
    //what will happen after we get the viewholder object
    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.email.setText(account.getEmail());
        holder.status.setText(account.getStatus());
        try {
            holder.check.setChecked(getRunningStatus(account.getEmail()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //How many times?
    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView email;
        public TextView status;
        public Switch check;
        public ViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            email = itemView.findViewById(R.id.acc_name);
            status = itemView.findViewById(R.id.status);
            check = itemView.findViewById(R.id.chip4);
            try {
                status.setText(getRunningStatus(email.getText().toString()) ? "Checking" : "Not Checking");
            } catch (Exception e) {
                e.printStackTrace();
            }
            check.setChecked(status.getText().toString().equals("Checking") ? false : true);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //starting service here
                    boolean status = check.isChecked();
                    if(status) {
                        try {
                            editStatus(email.getText().toString(), true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        accounts.set(getAdapterPosition(), new Account(email.getText().toString(), "Checking", accounts.get(getAdapterPosition()).getPassword()));
                        notifyItemChanged(getAdapterPosition());
                        Intent serviceIntent = new Intent(context, RequestService.class);
                        context.stopService(serviceIntent);
                        context.startService(serviceIntent);
                    }
                    else {
                        try {
                            editStatus(email.getText().toString(), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        accounts.set(getAdapterPosition(), new Account(email.getText().toString(), "Not Checking", accounts.get(getAdapterPosition()).getPassword()));
                        notifyItemChanged(getAdapterPosition());
                        Intent serviceIntent = new Intent(context, RequestService.class);
                        context.stopService(serviceIntent);
                        context.startService(serviceIntent);
                    }
                }
            };
            check.setOnClickListener(onClickListener);
        }

        @Override
        public void onClick(View v) {
        }
    }
    private int editStatus(String email, boolean status) throws Exception {
        //save
        SharedPreferences accounts = context.getSharedPreferences(PACKAGE_NAME_TEMP, context.MODE_PRIVATE);
        int no_of_accounts = accounts.getInt(PACKAGE_NAME_TEMP+".accounts", 0);
        int number = no_of_accounts;
        for(int i = 0; i < no_of_accounts; i++) {
            if(accounts.getString(PACKAGE_NAME_TEMP + ".email" + i, "").equals(email)) {
                number = i;
                break;
            }
        }
        if(number == no_of_accounts)throw new Exception("email does not exist");
        SharedPreferences.Editor editor = accounts.edit();
        editor.putBoolean(PACKAGE_NAME_TEMP+".running" + number,status);
        editor.apply();


        return 0;
    }
    private boolean getRunningStatus(String email) throws Exception {
        //save
        SharedPreferences accounts = context.getSharedPreferences(PACKAGE_NAME_TEMP, context.MODE_PRIVATE);
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
}
