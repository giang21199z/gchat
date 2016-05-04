package com.giangnd_svmc.ghalo.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.giangnd_svmc.ghalo.LoginOrRegisterActivity;
import com.giangnd_svmc.ghalo.MainActivity;
import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.entity.SMS;
import com.giangnd_svmc.ghalo.global.SocketHandler;
import com.giangnd_svmc.ghalo.entity.Account;
import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;

/**
 * Created by GIANGND-SVMC on 27/01/2016.
 */
public class PersonalFragment extends Fragment {
    private Account session_user;
    private Button btnLogout, btnGuide, btnAboutUs;
    private ImageView imvFragmentThree;
    private Socket mSocket;

    public PersonalFragment() {
        // Required empty public constructor
    }

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getActivity(), "Share success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Share cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), "Share error", Toast.LENGTH_SHORT).show();
            }
        });
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
        btnGuide = (Button) view.findViewById(R.id.btnGuide);
        btnAboutUs = (Button) view.findViewById(R.id.btnAboutUs);
        imvFragmentThree = (ImageView) view.findViewById(R.id.imvFragmentThree);
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
                                SharedPreferences sp = getActivity().getSharedPreferences("ghalo", getActivity().MODE_PRIVATE);
                                String id = sp.getString("id", "none");
                                if (id.equals("69696969")) {
                                    SharedPreferences.Editor edt = sp.edit();
                                    edt.remove("id");
                                    edt.remove("name");
                                    edt.remove("gender");
                                    edt.remove("email");
                                    edt.commit();
                                    //end delete
                                    //End logout room chat
                                    Intent intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
                                    ArrayList<SMS> listThreadIdSMS = (ArrayList<SMS>) getActivity().getIntent().getSerializableExtra("LIST_SMS");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("LIST_SMS", listThreadIdSMS);
                                    startActivity(intent);
                                } else {
                                    SharedPreferences.Editor edt = sp.edit();
                                    edt.remove("id");
                                    edt.remove("name");
                                    edt.remove("gender");
                                    edt.remove("email");
                                    edt.commit();
                                    //end delete
                                    //Logout room chat
                                    mSocket = SocketHandler.getSocket();
                                    mSocket.emit("client-out-room", session_user.toServer());
                                    //End logout room chat
                                    MainActivity activity = (MainActivity)getActivity();

                                    Intent intent = new Intent(activity, LoginOrRegisterActivity.class);
                                    ArrayList<SMS> listThreadIdSMS = (ArrayList<SMS>) getActivity().getIntent().getSerializableExtra("LIST_SMS");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("LIST_SMS", listThreadIdSMS);
                                    startActivity(intent);
                                    mSocket.disconnect();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        btnGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imvFragmentThree.setImageDrawable(getResources().getDrawable(R.drawable.guideline));
                imvFragmentThree.requestFocus();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Gchat ra mắt")
                            .setContentDescription("Gchat! Kết nối yêu thương")
                            .setImageUrl(Uri.parse("http://giangnd.xyz/img/logo.png"))
                            .setContentUrl(Uri.parse("http://giangnd.xyz"))
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });
        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imvFragmentThree.setImageDrawable(getResources().getDrawable(R.drawable.about_us));
                imvFragmentThree.requestFocus();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}