package com.giangnd_svmc.ghalo.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.entity.Account;

import java.util.List;

/**
 * Created by GIANGND-SVMC on 22/01/2016.
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {

    private List<Account> friendList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListName;
        public ImageView imageView;
        public TextView tvListCheckSms;
        Drawable drawable;

        public MyViewHolder(View view) {
            super(view);
            tvListName = (TextView) view.findViewById(R.id.tvListName);
            imageView = (ImageView) view.findViewById(R.id.avatar);
            tvListCheckSms = (TextView) view.findViewById(R.id.tvListCheckSms);
            drawable = view.getResources().getDrawable(R.drawable.avatar_f);
        }
    }


    public AccountAdapter(List<Account> friendList) {
        this.friendList = friendList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_online_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Account friend = friendList.get(position);
        holder.tvListName.setText(friend.getName());
        if (friend.getGender().equals("female")) {
            holder.imageView.setImageDrawable(holder.drawable);
        }
        if (friend.getCheckNewSms()) {
            holder.tvListCheckSms.setText("Have new messages!!!");
        } else {
            holder.tvListCheckSms.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}