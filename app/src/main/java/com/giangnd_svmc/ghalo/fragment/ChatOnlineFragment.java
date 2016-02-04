package com.giangnd_svmc.ghalo.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.ChatActivity;
import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.event.RecyclerItemClickListener;
import com.giangnd_svmc.ghalo.global.SocketHandler;
import com.giangnd_svmc.ghalo.adapter.AccountAdapter;
import com.giangnd_svmc.ghalo.dao.MessageDao;
import com.giangnd_svmc.ghalo.entity.Account;
import com.giangnd_svmc.ghalo.entity.Message;
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
 * Created by GIANGND-SVMC on 27/01/2016.
 */
public class ChatOnlineFragment extends Fragment {
    private Account session_user;
    private MessageDao messageDao = new MessageDao(getActivity());

    public ChatOnlineFragment() {
        // Required empty public constructor
    }


    private RecyclerView recyclerView;
    private AccountAdapter mAdapter;
    private List<Account> friends = new ArrayList<>();
    private Switch swIsOnline;
    private Socket mSocket;
    private Account friend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session_user = (Account) getActivity().getIntent().getSerializableExtra("SESSION");
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
        mSocket.on("server-tra-ve-tn-" + session_user.getName() + "-" + session_user.getGender(), baoNoti);
        SocketHandler.setSocket(mSocket);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        swIsOnline = (Switch) getActivity().findViewById(R.id.swtIsOnline);
        mAdapter = new AccountAdapter(friends);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        swIsOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSocket.emit("client-join-room", session_user.getName() + "-" + session_user.getGender());
                } else {
                    mSocket.emit("client-out-room", session_user.getName() + "-" + session_user.getGender());
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                 TODO Handle item click
                if (!swIsOnline.isChecked()) {
                    Toast.makeText(getActivity(), "Your must online before choose a friend.\nPlease online now!", Toast.LENGTH_SHORT).show();
                } else if (friends.get(position).getName().equals(session_user.getName())) {
                    Toast.makeText(getActivity(), "Can not talk to yourself!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("ME", session_user);
                    intent.putExtra("FRIEND", friends.get(position));
                    friends.get(position).setCheckNewSms(false);
                    startActivity(intent);
                }
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private Emitter.Listener clientReqNumberUserOnline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONArray listUser = data.getJSONArray("danhsach");
                        friends = new ArrayList<>();
                        for (int i = 0; i < listUser.length(); i++) {
                            String user = listUser.get(i).toString();
                            String arr[] = user.split("-");
                            friends.add(new Account("", arr[0], arr[1], ""));
                        }
                        mAdapter = new AccountAdapter(friends);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String content = data.getString("tinnhan");
                        String arrs[] = content.split(":");
                        String person[] = arrs[0].split("-");
                        int vt = 0;
                        int j = 0;
                        for (int i = 0; i < friends.size(); i++) {
                            if (!friends.get(i).getCheckNewSms()) {
                                vt = i;
                            }
                            if (friends.get(i).getName().equals(person[0])) {
                                friends.get(i).setCheckNewSms(true);
                                j = i;
                                break;
                            }
                        }
                        mAdapter = new AccountAdapter(friends);
                        recyclerView.setAdapter(mAdapter);
                        Toast.makeText(getActivity(), "You have got new message!\n" + person[0] + ":" + arrs[1], Toast.LENGTH_LONG).show();
//                        createNoti("You have got new message!", person[0] + ":" + arrs[1]);
                        Message message = new Message(session_user.getName(), person[0], arrs[1]);
                        messageDao = new MessageDao(getActivity());
                        messageDao.open();
                        messageDao.createData(message);
                        messageDao.close();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    public void createNoti(String title, String content) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.message_icon)
                        .setContentTitle(title)
                        .setContentText(content);
        Intent resultIntent = new Intent(getActivity(), ChatActivity.class);
        session_user = (Account) getActivity().getIntent().getSerializableExtra("SESSION");
        resultIntent.putExtra("ME", session_user);
        resultIntent.putExtra("FRIEND", friend);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new AccountAdapter(friends);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.emit("client-out-room", session_user.getName() + "-" + session_user.getGender());
    }
}