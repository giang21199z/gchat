package com.giangnd_svmc.ghalo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.giangnd_svmc.ghalo.entity.Account;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginOrRegisterActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Account session_user = new Account();
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("ghalo", MODE_PRIVATE);
        String id = sp.getString("id", "none");
        String name = sp.getString("name", "none");
        String gender = sp.getString("gender", "none");
        String email = sp.getString("email", "none");
        if (!name.equals("none") && !gender.equals("none")) {
            session_user = new Account(id, name, gender, email);
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("SESSION", session_user);
            startActivity(intent);
        } else {
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            setContentView(R.layout.activity_login_or_register);
            loginButton = (LoginButton) findViewById(R.id.loginBtnFb);
            checkbox = (CheckBox) findViewById(R.id.cbLogin);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();
                    GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String id = object.optString("id");
                            String name = object.optString("name");
                            String gender = object.optString("gender");
                            String email = object.optString("email");
                            session_user.setId(id);
                            session_user.setEmail(email);
                            session_user.setGender(gender);
                            session_user.setName(name);
                            if (checkbox.isChecked()) {
                                SharedPreferences sp = getSharedPreferences("ghalo", MODE_PRIVATE);
                                SharedPreferences.Editor edt = sp.edit();
                                edt.putString("id", id);
                                edt.putString("name", name);
                                edt.putString("gender", gender);
                                edt.putString("email", email);
                                edt.commit();
                            }
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.putExtra("SESSION", session_user);
                            startActivity(intent);
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,gender,link,email");
                    graphRequest.setParameters(parameters);
                    graphRequest.executeAsync();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getBaseContext(), "Login Cancle", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException e) {
                    Toast.makeText(getBaseContext(), "Login False", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}