package com.giangnd_svmc.ghalo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.Gravity;

import com.giangnd_svmc.ghalo.entity.SMS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hoangdd on 2/2/2016.
 */
public class ReadSMS extends AsyncTask<Void, Void, Void> {

    StartActivity activity;
    Activity smsFragment;
    int type = 0;
    String thread_id;
    ProgressDialog progressDialog;
    ArrayList<SMS> listSMS;

    public ArrayList<String> listThreadIdSMS;       // cap nhat danh sach sms moi nhat cua moi so lien lac


    public ArrayList<String> getlistThreadIdSMS() {
        return listThreadIdSMS;
    }

    public ReadSMS(StartActivity activity) {
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
    }

    public ReadSMS(Activity smsFragment, int type, String thread_id) {
        this.smsFragment = smsFragment;
        this.type = type;
        this.thread_id = thread_id;
        progressDialog = new ProgressDialog(smsFragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String s = "Welcome to Gchat!";
        if (type == 1) {
            s = "Loading";
        }
        progressDialog.setMessage(s);
        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (type == 0) {
            listSMS = getListSMS(activity);
        } else if (type == 1) {
            listSMS = getSMSConversation(smsFragment, thread_id);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (type == 0) {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.putExtra("LIST_SMS", listSMS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        } else if (type == 1) {
            Intent intent = new Intent(smsFragment, SMSDetailActivity.class);
            ArrayList<SMS> listSMS = getSMSConversation(smsFragment, thread_id);
            intent.putExtra("LIST_SMS_DETAIL", listSMS);
            smsFragment.startActivity(intent);
        }
        //intent vao Main Activity
    }

    public ArrayList getListSMS(Context context) {
        ArrayList<SMS> listSMS = new ArrayList<>();

//        Cursor cur = getContentResolver().query(Conversations.CONTENT_URI, null, null, null, null);
        try {
            Cursor cur = context.getContentResolver().query(Telephony.Threads.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
            int count = 1;
            SMS sms = new SMS();
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToNext();
                sms.setType(cur.getString(cur.getColumnIndex("type")));
                sms.set_address(cur.getString(cur.getColumnIndex("address")));
                sms.setPerson(getContactName(context.getApplicationContext(), sms.get_address()));
                sms.setRead(cur.getString(cur.getColumnIndex("read")));
                sms.setBody(cur.getString(cur.getColumnIndex("body")));
                sms.set_thread_id(cur.getString(cur.getColumnIndex("thread_id")));
                long timeDate = Long.valueOf(cur.getString(cur.getColumnIndex("date")));
                String date = new SimpleDateFormat("HH:mm yyyy/MM/dd").format(new Date(timeDate));
                sms.setDate(date);
                listSMS.add(sms);
                sms = new SMS();
            }
            cur.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listSMS;
    }

    public ArrayList getSMSConversation(Context context, String thread_id) {
        ArrayList<SMS> listSMS = new ArrayList<>();
        SMS sms;
        Uri uri = Uri.parse("content://sms");
        Cursor cur = context.getContentResolver().query(uri, null, "thread_id = " + thread_id, null, Telephony.Sms.DEFAULT_SORT_ORDER);

        if (!cur.moveToFirst()) {
            return null;
        }

        for (int i = 0; i < cur.getCount(); i++) {
            sms = new SMS();
            sms.set_thread_id(cur.getString(1));
            sms.set_address(cur.getString(2));
            sms.setPerson(getContactName(context.getApplicationContext(), sms.get_address()));
            sms.setRead(cur.getString(7));
            sms.setType(cur.getString(9));
            sms.setBody(cur.getString(12));
            long timeDate = Long.valueOf(cur.getString(4));
            String date = new SimpleDateFormat("HH:mm yyyy/MM/dd").format(new Date(timeDate));
            sms.setDate(date);

            listSMS.add(sms);

            cur.moveToNext();
        }
        return listSMS;
    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
//        return "Android SDK: " + sdkVersion + " (" + release + ")";
        return sdkVersion + "";
    }

}
