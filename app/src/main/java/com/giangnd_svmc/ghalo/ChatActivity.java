package com.giangnd_svmc.ghalo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.adapter.MessageAdapter;
import com.giangnd_svmc.ghalo.dao.MessageDao;
import com.giangnd_svmc.ghalo.entity.Account;
import com.giangnd_svmc.ghalo.entity.Message;
import com.giangnd_svmc.ghalo.global.SessionHandler;
import com.giangnd_svmc.ghalo.global.SocketHandler;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIANGND-SVMC on 22/01/2016.
 */
public class ChatActivity extends AppCompatActivity {
    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    private Button ibtnSend;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText edtChat;
    private Account friend;
    private Account me;
    private Socket mSocket = SocketHandler.getSocket();
    private MessageDao messageDao = new MessageDao(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_chat_list);

        Intent intent = getIntent();
        me = new SessionHandler(this.getBaseContext()).getSharePrefer();
        friend = (Account) intent.getSerializableExtra(getString(R.string.FRIEND));

        mSocket.emit("client-join-room", me.toServer());

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("Chat with " + friend.getName());

        recyclerView = (RecyclerView) findViewById(R.id.rvfriend_chat_list);
        ibtnSend = (Button) findViewById(R.id.btnChatFriend);
        edtChat = (EditText) findViewById(R.id.edtChat);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        messageDao.open();
        messages = messageDao.getHistoryMessage(friend, getString(R.string.limit));
        messageDao.close();
        recyclerView.setHasFixedSize(true);
        mAdapter = new MessageAdapter(messages);
        mAdapter.setSession_user(me);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        ibtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String content = edtChat.getText().toString();
                    if (content.length() == 0) return;
                    Message message = new Message(me.getName(), friend.getName(), content);
                    messages.add(message);
                    if (messages.size() > 10) {
                        messages.remove(0);
                    }
                    mAdapter.setSession_user(me);
                    edtChat.setText("");
                    mSocket.emit(getString(R.string.client_chat_friend), me.toServer() + "_" + friend.toServer() + ":" + content);
                    messageDao.open();
                    messageDao.createData(message);
                    messageDao.close();

                    mAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.smoothScrollToPosition(messages.size());

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getString(R.string.before_you_must_login), Toast.LENGTH_LONG).show();
                }
            }
        });
        mSocket.on(getString(R.string.server_tra_ve_tn_) + me.toServer(), nhanTinNhan);
        //event show them message
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageDao.open();
                messages = messageDao.getHistoryMessage(friend, "20");
                messageDao.close();

                mAdapter.setSession_user(me);
                recyclerView.setAdapter(mAdapter);
                recyclerView.smoothScrollToPosition(messages.size());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private Emitter.Listener nhanTinNhan = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String content = data.getString(getString(R.string.tinnhan));
                        String arr[] = content.split(":");
                        String[] nguoi_gui = arr[0].split("-");
                        Message message = new Message(nguoi_gui[1], me.getName(), arr[1]);
                        messages.add(message);
                        if (messages.size() > 10) {
                            messages.remove(0);
                        }
                        //set data vao recycler
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.smoothScrollToPosition(messages.size());
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}