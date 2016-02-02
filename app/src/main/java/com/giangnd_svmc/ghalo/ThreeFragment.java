package com.giangnd_svmc.ghalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.giangnd_svmc.ghalo.entity.Account;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by GIANGND-SVMC on 27/01/2016.
 */
public class ThreeFragment extends Fragment {
    private Account session_user;
    private Button btnLogout;
    private Socket mSocket;

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_three, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        session_user = (Account) getActivity().getIntent().getSerializableExtra("SESSION");
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_logout)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Logout facebook
                                FacebookSdk.sdkInitialize(getContext());
                                LoginManager.getInstance().logOut();
                                //end logout fb
                                //delete sharepreferences
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ghalo", 0);
                                SharedPreferences.Editor edt = sharedPreferences.edit();
                                edt.remove("id");
                                edt.remove("name");
                                edt.remove("gender");
                                edt.remove("email");
                                edt.commit();
                                //end delete
                                //Logout room chat
                                mSocket = SocketHandler.getSocket();
                                mSocket.emit("client-out-room", session_user.getName() + "-" + session_user.getGender());
                                //End logout room chat
                                Intent intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}