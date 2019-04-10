package com.example.befueleddriver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.befueleddriver.R;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {


    private ArrayList<String> email = new ArrayList<>();
    private ArrayList<String> username = new ArrayList<>();
    private Context mContext;

    public UserRecyclerAdapter(Context context,ArrayList<String> username, ArrayList<String> email) {
        this.username= username;
        this.email = email;
        this.mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.email.setText(email.get(i));
        viewHolder.username.setText(username.get(i));

    }

    @Override
    public int getItemCount() {
        return username.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, email;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
        }


    }

}
