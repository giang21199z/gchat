package com.giangnd_svmc.ghalo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.giangnd_svmc.ghalo.adapter.SMSDetailAdapter;
import com.giangnd_svmc.ghalo.entity.SMS;

import java.util.ArrayList;

/**
 * Created by hoangdd on 2/8/2016.
 */
public class SMSDetailActivity extends AppCompatActivity {

    ListView lv;
    SMSDetailAdapter sms2PeopleAdapter;
    ArrayList<SMS> listSMS2People;
    EditText inputMsg;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);
        lv = (ListView) findViewById(R.id.list_view_messages);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        btnSend = (Button) findViewById(R.id.btnSend);


        Intent intent = getIntent();
        listSMS2People = (ArrayList<SMS>) intent.getSerializableExtra("LIST_SMS_DETAIL");

        sms2PeopleAdapter = new SMSDetailAdapter(this, listSMS2People);
        lv.setAdapter(sms2PeopleAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSMS sendSMS = new SendSMS(SMSDetailActivity.this,listSMS2People.get(0).get_address(), inputMsg.getText()+"");
                sendSMS.execute();
                String threadID = listSMS2People.get(0).get_thread_id();
                String number = listSMS2People.get(0).get_address();
                String msg = inputMsg.getText() + "";
                listSMS2People.add(new SMS("", threadID, number, "", "", "", "", "", "", "2", "", "", msg, "", "", "", ""));
                sms2PeopleAdapter = new SMSDetailAdapter(SMSDetailActivity.this, listSMS2People);
                lv.setAdapter(sms2PeopleAdapter);
                inputMsg.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
