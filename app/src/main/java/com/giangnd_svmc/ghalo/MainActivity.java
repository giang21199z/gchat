package com.giangnd_svmc.ghalo;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //    private Socket mSocket;
    EditText edtUsername;
    Button btnJoinRoom, btnNew, btnTab;
    ListView lvUserOnline;
    ArrayList<String> listUserOnline = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
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
        //server tra ve ket noi join room
        mSocket.on("server-join-room", clientJoinRoom);
        */
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        btnJoinRoom = (Button) findViewById(R.id.btnJoin);
        btnNew = (Button) findViewById(R.id.btnNew);
        btnTab = (Button) findViewById(R.id.btnNew2);
        lvUserOnline = (ListView) findViewById(R.id.lvListFriendOnline);
        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, listUserOnline);
        lvUserOnline.setAdapter(adapter);
        btnJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edtUsername.getText().toString();
                //client ket noi server
//                mSocket.emit("client-join-room", username);
                Toast.makeText(getBaseContext(), username, Toast.LENGTH_SHORT).show();
            }
        });
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FriendListChat.class);
                intent.putExtra("ME", username);
                startActivity(intent);
            }
        });
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), TabActivity.class);
                startActivity(intent);
            }
        });
    }

    public void doCreateDb() {

        Toast.makeText(getBaseContext(), "Create Success", Toast.LENGTH_SHORT).show();
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
                        listUserOnline = new ArrayList<String>();
                        for (int i = 0; i < listUser.length(); i++)
                            listUserOnline.add(listUser.get(i).toString());
                        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, listUserOnline);
                        lvUserOnline.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener clientJoinRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    try {
                        noidung = data.getString("noidung");
                        if (noidung == "true") {
                            Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "False", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}
