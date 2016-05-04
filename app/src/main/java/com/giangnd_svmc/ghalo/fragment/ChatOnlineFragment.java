package com.giangnd_svmc.ghalo.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.giangnd_svmc.ghalo.ChatActivity;
import com.giangnd_svmc.ghalo.DetailFacebook;
import com.giangnd_svmc.ghalo.LoginOrRegisterActivity;
import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.entity.SMS;
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
    private Activity activity;
    private MessageDao messageDao = new MessageDao(activity);
    private int vt = 0;

    private ArrayList<String> FRIEND_LIST = new ArrayList<String>();

    public ChatOnlineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
    }

    private RecyclerView recyclerView;
    private AccountAdapter mAdapter;
    private List<Account> friends = new ArrayList<>();
    private Switch swIsOnline;
    private Socket mSocket;
    private Account friend;
    private AutoCompleteTextView autoCompleteTextView;
    private Button btnSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = activity.getSharedPreferences("ghalo", activity.MODE_PRIVATE);
        String id = sp.getString("id", "none");
        if (id.equals("69696969")) {
            return;
        }
        session_user = (Account) activity.getIntent().getSerializableExtra("SESSION");
        //connect to server
//            mSocket = IO.socket("http://192.168.43.22:3000");
        try {
//            mSocket = IO.socket("http://192.168.11.1:3000");
            mSocket = IO.socket(getResources().getString(R.string.cloud));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//            mSocket = IO.socket("http://103.237.99.174:3000");
        mSocket.connect();
        //server tra ve ket qua
        try {
            mSocket.on("server-tra-ve-so-nguoi-online", clientReqNumberUserOnline);
            mSocket.on("server-tra-ve-tn-" + session_user.toServer(), baoNoti);
        } catch (Exception ex) {
            Log.d("MYEXCEPTION", ex.getMessage());
        }
        SocketHandler.setSocket(mSocket);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences sp = activity.getSharedPreferences("ghalo", activity.MODE_PRIVATE);
        String id = sp.getString("id", "none");
        if (id.equals("69696969")) {
            return inflater.inflate(R.layout.activity_layout_guest, container, false);
        }
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp = activity.getSharedPreferences("ghalo", activity.MODE_PRIVATE);
        String id = sp.getString("id", "none");
        if (id.equals("69696969")) {
            return;
        }
        recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
        swIsOnline = (Switch) activity.findViewById(R.id.swtIsOnline);
        mAdapter = new AccountAdapter(friends);
        autoCompleteTextView = (AutoCompleteTextView) activity.findViewById(R.id.actvKeyword);
        btnSearch = (Button) activity.findViewById(R.id.btnSearch);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, FRIEND_LIST);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
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
                    mSocket.emit("client-join-room", session_user.toServer());
                } else {
                    mSocket.emit("client-out-room", session_user.toServer());
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                 TODO Handle item click
                if (!swIsOnline.isChecked()) {
                    Toast.makeText(activity, "Your must online before choose a friend.\nPlease online now!", Toast.LENGTH_SHORT).show();
                } else if (friends.get(position).getName().equals(session_user.getName())) {
                    Toast.makeText(activity, "Can not talk to yourself!", Toast.LENGTH_SHORT).show();
                } else {
                    vt = position;
                    createDialog();
                }
            }
        }));
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = autoCompleteTextView.getText().toString();
                ArrayList<Account> arrAccounts = new ArrayList<Account>();
                for (Account account : friends) {
                    if (account.getName().contains(keyword)) {
                        arrAccounts.add(account);
                    }
                }
                mAdapter = new AccountAdapter(arrAccounts);
                mAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private Emitter.Listener clientReqNumberUserOnline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            FRIEND_LIST = new ArrayList<String>();
                            JSONArray listUser = data.getJSONArray("danhsach");
                            friends = new ArrayList<>();
                            for (int i = 0; i < listUser.length(); i++) {
                                String user = listUser.get(i).toString();
                                String arr[] = user.split("-");
                                friends.add(new Account(arr[0], arr[1], arr[2], ""));
                                FRIEND_LIST.add(arr[1]);
                            }
                            updateListFriendOnline();

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
                activity.runOnUiThread(new Runnable() {
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
                                if (friends.get(i).getName().equals(person[1])) {
                                    friends.get(i).setCheckNewSms(true);
                                    j = i;
                                    break;
                                }
                            }
                            mAdapter = new AccountAdapter(friends);
                            recyclerView.setAdapter(mAdapter);
                            Toast.makeText(activity, "You have got new message!\n" + person[1] + ":" + arrs[1], Toast.LENGTH_LONG).show();
//                        createNoti("You have got new message!", person[0] + ":" + arrs[1]);
                            Message message = new Message(person[1], session_user.getName(), arrs[1]);
                            messageDao = new MessageDao(activity);
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


//    public void createNoti(String title, String content) {
//        NotificationCompat.Builder mBuilder =
//                (NotificationCompat.Builder) new NotificationCompat.Builder(activity)
//                        .setSmallIcon(R.drawable.message_icon)
//                        .setContentTitle(title)
//                        .setContentText(content);
//        Intent resultIntent = new Intent(activity, ChatActivity.class);
//        session_user = (Account) activity.getIntent().getSerializableExtra("SESSION");
//        resultIntent.putExtra("ME", session_user);
//        resultIntent.putExtra("FRIEND", friend);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
//        stackBuilder.addParentStack(ChatActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(1, mBuilder.build());
//    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = activity.getSharedPreferences("ghalo", activity.MODE_PRIVATE);
        String id = sp.getString("id", "none");
        if (id.equals("69696969") || id.equals("none")) {
            return;
        }
        mAdapter = new AccountAdapter(friends);
        recyclerView.setAdapter(mAdapter);
        mSocket.emit("client-join-room", session_user.toServer());
        FRIEND_LIST.add(session_user.getName());
        updateListFriendOnline();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = activity.getSharedPreferences("ghalo", activity.MODE_PRIVATE);
        String id = sp.getString("id", "none");
        if (id.equals("69696969") || id.equals("none")) {
            return;
        }
        Toast.makeText(activity, "Onpause", Toast.LENGTH_SHORT).show();
        mSocket.emit("client-out-room", session_user.toServer());
        FRIEND_LIST.remove(session_user.getName());
        updateListFriendOnline();
    }

    @Override
    public void onPause() {
        super.onPause();
        //out room
        SharedPreferences sp = activity.getSharedPreferences("ghalo", activity.MODE_PRIVATE);
        String id = sp.getString("id", "none");
        if (id.equals("69696969") || id.equals("none")) {
            return;
        }
        mSocket.emit("client-out-room", session_user.toServer());
        FRIEND_LIST.remove(session_user.getName());
        updateListFriendOnline();
    }

    void updateListFriendOnline() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, FRIEND_LIST);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
    }

    public void createDialog() {
        new android.support.v7.app.AlertDialog.Builder(activity)
                .setIcon(R.drawable.chooses)
                .setTitle("Choose once action")
                .setCancelable(true)
                .setMessage("Chat with friend/ show detail?")
                .setPositiveButton("Chatting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, ChatActivity.class);
                        intent.putExtra("ME", session_user);
                        intent.putExtra("FRIEND", friends.get(vt));
                        friends.get(vt).setCheckNewSms(false);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Show detail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(activity, DetailFacebook.class);
                        intent.putExtra("FRIEND", friends.get(vt));
                        startActivity(intent);
                    }
                })
                .show();

    }
}