package com.giangnd_svmc.ghalo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ReadSMS readSMS = new ReadSMS(StartActivity.this);
        readSMS.execute();
    }
}
