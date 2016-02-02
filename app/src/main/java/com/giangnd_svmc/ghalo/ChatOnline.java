package com.giangnd_svmc.ghalo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.entity.Account;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIANGND-SVMC on 22/01/2016.
 */
public class ChatOnline extends AppCompatActivity {
    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    private ImageButton ibtnSend;
    private EditText edtChat;
    private Account friend;
    private Account me;
    Socket mSocket = SocketHandler.getSocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_chat_list);
        Intent intent = getIntent();
        me = (Account) intent.getSerializableExtra("ME");
        friend = (Account) intent.getSerializableExtra("FRIEND");
        recyclerView = (RecyclerView) findViewById(R.id.rvfriend_chat_list);
        ibtnSend = (ImageButton) findViewById(R.id.btnChatFriend);
        edtChat = (EditText) findViewById(R.id.edtChat);

        recyclerView.setHasFixedSize(true);
        mAdapter = new MessageAdapter(messages);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
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
                    mAdapter.notifyDataSetChanged();
                    edtChat.setText("");
                    mSocket.emit("client-chat-friend", me.getName() + "-" + me.getGender() + "_" + friend.getName() + "-" + friend.getGender() + ":" + content);
                    recyclerView.smoothScrollToPosition(recyclerView.getChildCount());
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(),"Vui lòng đăng nhập để đọc tin nhắn",Toast.LENGTH_LONG).show();
                }
            }
        });
        mSocket.on("server-tra-ve-tn-" + me.getName() + "-" + me.getGender(), nhanTinNhan);
    }

    private Emitter.Listener nhanTinNhan = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String content = data.getString("tinnhan");
                        String arr[] = content.split(":");
                        Message message = new Message("me", "friend", arr[1]);
                        messages.add(message);
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

}