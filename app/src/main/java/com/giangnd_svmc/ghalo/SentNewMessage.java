package com.giangnd_svmc.ghalo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.entity.SMS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hoangdd on 2/8/2016.
 */


public class SentNewMessage extends AppCompatActivity {

    private ArrayList<Map<String, String>> mPeopleList;

    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;
    Button btnSendMsg;
    EditText msgContent;

    private String name;
    private String number;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        mPeopleList = new ArrayList<Map<String, String>>();
        PopulatePeopleList();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.list_row_contact, new String[]{"Name", "Phone", "Type"}, new int[]{R.id.ccontName, R.id.ccontNo, R.id.ccontType});
        mTxtPhoneNo.setAdapter(mAdapter);
        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3) {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);
                name = map.get("Name");
                number = map.get("Phone");
                mTxtPhoneNo.setText("" + name + " <" + number + ">");
            }
        });


        btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Create new Message",Toast.LENGTH_SHORT);
            }
        });
    }

    public String existPhone(String number) {
        Uri smsUri = Uri.parse("content://sms/");
        Cursor cur = getContentResolver().query(smsUri, null, null, null, null);
        if (cur.moveToFirst()) { /* false = cursor is empty */
            for (int i = 0; i < cur.getCount(); i++) {
                String _thread_id = cur.getString(1);
                String _address = cur.getString(2);
                if (number.compareToIgnoreCase(_address) == 0) {
                    return _thread_id;
                }
                cur.moveToNext();
            }
            cur.close();
            return null;
        } else {
            return null;
        }
    }

    public void PopulatePeopleList() {

        mPeopleList.clear();

        Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (people.moveToNext()) {
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String hasPhone = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)) {

                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null, null);
                while (phones.moveToNext()) {

                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));

                    Map<String, String> NamePhoneType = new HashMap<String, String>();

                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);

                    if (numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else if (numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if (numberType.equals("2"))
                        NamePhoneType.put("Type", "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");

                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();

        startManagingCursor(people);
    }


}
