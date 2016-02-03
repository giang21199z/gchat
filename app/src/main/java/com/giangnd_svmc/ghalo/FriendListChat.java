package com.giangnd_svmc.ghalo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.entity.Account;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIANGND-SVMC on 22/01/2016.
 */
public class FriendListChat extends AppCompatActivity {
    private Switch swIsOnline;
    private List<Account> friends = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendOnlineAdapter mAdapter;
    private Socket mSocket;
    private Account me;
    private Account friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_friend_online);
        Intent intent = getIntent();
        me = (Account) intent.getSerializableExtra("ME");
        friend = (Account) intent.getSerializableExtra("FRIEND");
        try {
            //connect to server
            mSocket = IO.socket("http://192.168.201.1:3000");
//            mSocket = IO.socket("http://103.237.99.174:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        //server tra ve ket qua
        mSocket.on("server-tra-ve-so-nguoi-online", clientReqNumberUserOnline);
        mSocket.on("server-tra-ve-tn-" + me.getName() + "-" + me.getGender(), baoNoti);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swIsOnline = (Switch) findViewById(R.id.swtIsOnline);
        recyclerView.setHasFixedSize(true);
        mAdapter = new FriendOnlineAdapter(friends);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
//        prepareMovieData();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                 TODO Handle item click
                Toast.makeText(getApplicationContext(), "Friend:"+friends.get(position).getName(), Toast.LENGTH_SHORT).show();
                if (friends.get(position).getName().equals(me.getName())) {
                    Toast.makeText(getBaseContext(), "Can not talk to yourself!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getBaseContext(), ChatOnline.class);
                    intent.putExtra("ME", me);
                    intent.putExtra("FRIEND", friends.get(position).getName());
                    startActivity(intent);
                    SocketHandler.setSocket(mSocket);
                }
            }
        }));
        swIsOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSocket.emit("client-join-room", me.getName() + "-" + me.getGender());
                } else {
                    mSocket.emit("client-out-room", me.getName() + "-" + me.getGender());
                }
            }
        });
    }

    public void createNoti(String title, String content) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.message_icon)
                        .setContentTitle(title)
                        .setContentText(content);
        Intent resultIntent = new Intent(this, ChatOnline.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChatOnline.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private Emitter.Listener clientReqNumberUserOnline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONArray listUser = data.getJSONArray("danhsach");
                        friends = new ArrayList<>();
                        for (int i = 0; i < listUser.length(); i++) {
                            friends.add(new Account("", listUser.get(i).toString(), "", ""));
                            Toast.makeText(getBaseContext(), listUser.get(i).toString(), Toast.LENGTH_SHORT).show();
                        }
                        mAdapter = new FriendOnlineAdapter(friends);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener baoNoti = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String content = data.getString("tinnhan");
                        createNoti("Bạn có tin nhắn mới", content);
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}