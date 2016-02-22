package com.giangnd_svmc.ghalo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.view.Gravity;

/**
 * Created by hoangdd on 2/13/2016.
 */

public class SendSMS extends AsyncTask<Void, Void, Void> {
    ProgressDialog progressDialog;
    SMSDetailActivity activity;
    String address;
    String body;
    SentNewMessage sentNewMessage;

    public SendSMS(SMSDetailActivity activity, String address, String body) {
        this.activity = activity;
        this.address = address;
        this.body = body;
        progressDialog = new ProgressDialog(activity);
    }
    public SendSMS(SentNewMessage sentNewMessage, String address, String body) {
        this.sentNewMessage = sentNewMessage;
        this.address = address;
        this.body = body;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String s = "Senting...";
        progressDialog.setMessage(s);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        sendSMS(activity, address, body);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void sendSMS(Context context, String address, String body) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 16) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(address, null, body, null, null);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(address, null, body, null, null);
            ContentValues values = new ContentValues();
            values.put("address", address);//sender name
            values.put("body", body);
            context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        }
    }
}
