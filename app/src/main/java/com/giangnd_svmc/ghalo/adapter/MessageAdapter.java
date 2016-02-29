package com.giangnd_svmc.ghalo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.entity.Account;
import com.giangnd_svmc.ghalo.entity.Message;

import java.util.List;

/**
 * Created by GIANGND-SVMC on 22/01/2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    // objetc chua data item cua recycle dau ban oi messageList
    private List<Message> messageList;
    public Account session_user;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvChatLeft, tvChatRight, tvFriendChat;
        public ImageView imgChatLeft, imgChatRight;
        public RelativeLayout rl1, rl2;

        public MyViewHolder(View view) {
            super(view);
            tvChatLeft = (TextView) view.findViewById(R.id.tv_chat_left);
            tvChatRight = (TextView) view.findViewById(R.id.tv_chat_right);
            tvFriendChat = (TextView) view.findViewById(R.id.tvFriendChat);
            imgChatLeft = (ImageView) view.findViewById(R.id.imvChatLeft);
            imgChatRight = (ImageView) view.findViewById(R.id.imvChatRight);
            rl1 = (RelativeLayout) view.findViewById(R.id.rl1);
            rl2 = (RelativeLayout) view.findViewById(R.id.rl2);
        }

    }

    public void setSession_user(Account session_user) {
        this.session_user = session_user;
    }

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_friend_chat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getMe().equals(session_user.getName())) {
            holder.imgChatLeft.setVisibility(View.VISIBLE);
            holder.rl1.setVisibility(View.VISIBLE);
            holder.tvChatLeft.setVisibility(View.VISIBLE);
            holder.tvFriendChat.setVisibility(View.VISIBLE);
            holder.imgChatRight.setVisibility(View.INVISIBLE);
            holder.rl2.setVisibility(View.INVISIBLE);
            holder.tvChatRight.setVisibility(View.INVISIBLE);
            holder.tvChatLeft.setText(message.getContent());
            holder.tvFriendChat.setText(session_user.getName());
        } else {
            holder.imgChatLeft.setVisibility(View.INVISIBLE);
            holder.rl1.setVisibility(View.INVISIBLE);
            holder.tvChatLeft.setVisibility(View.INVISIBLE);
            holder.tvFriendChat.setVisibility(View.INVISIBLE);
            holder.tvChatRight.setText(message.getContent());
            holder.imgChatRight.setVisibility(View.VISIBLE);
            holder.rl2.setVisibility(View.VISIBLE);
            holder.tvChatRight.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}