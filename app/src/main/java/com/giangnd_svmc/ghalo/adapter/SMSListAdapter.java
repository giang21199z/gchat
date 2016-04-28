package com.giangnd_svmc.ghalo.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.entity.SMS;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by hoangdd on 2/2/2016.
 */
public class SMSListAdapter extends ArrayAdapter {
    Activity activity;

    public SMSListAdapter(Activity activity, ArrayList products) {
        super(activity, 0, products);
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_row_sms, null);
        TextView img = (TextView) convertView.findViewById(R.id.list_image);
        TextView title = (TextView) convertView.findViewById(R.id.title1);
        TextView artist = (TextView) convertView.findViewById(R.id.artist);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);

        SMS sms = (SMS) getItem(position);
        if (sms.getPerson() != null) {
            title.setText(sms.getPerson());
            if (sms.getPerson().substring(0, 1).equals("+")) {
                img.setText(sms.getPerson().substring(1, 2));
            } else {
                img.setText(sms.getPerson().substring(0, 1));
            }
        } else {
            title.setText(sms.get_address());
            img.setText("G");
        }
        String temp = show40char(sms.getBody());
        artist.setText(temp);
        try {
            duration.setText(getTimeSMS(sms.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }


    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTimeSMS(String time) throws ParseException {
        String[] timeSMS = time.split(" ");
        long timeDate = new Date().getTime();
        String hourSMS, dateSMS;
        hourSMS = timeSMS[0];
        dateSMS = timeSMS[1];

        //Get time current
        long timeCurrent = (int) (System.currentTimeMillis());
        String dateCurrent = new SimpleDateFormat("yyyy/MM/dd").format(new Date(timeCurrent));


        if (compareDate(dateSMS, dateCurrent)) {
            return hourSMS;
        } else {
            return dateSMS;
        }
    }

    public boolean compareDate(String str1, String str2) {
        String[] listStr1 = str1.split("/");
        String[] listStr2 = str2.split("/");


        // compare years
        if (listStr1[0].compareTo(listStr2[0]) > 0) {
            return true;
        } else if (listStr1[0].compareTo(listStr2[0]) < 0) {
            return false;
        }

        // compare month
        if (listStr1[1].compareTo(listStr2[1]) > 0) {
            return true;
        } else if (listStr1[1].compareTo(listStr2[1]) < 0) {
            return false;
        }

        // compare day
        if (listStr1[2].compareTo(listStr2[2]) > 0) {
            return true;
        } else if (listStr1[2].compareTo(listStr2[2]) < 0) {
            return false;
        }
        return false;
    }

    public String show40char(String sms) {
        String temp = "";
        try {
            String[] separated = sms.split(" ");
            if (separated.length > 1) {
                for (int i = 0; i < separated.length; i++) {
                    if (temp.length() < 40) {
                        temp = temp + separated[i] + " ";
                    }
                }
            } else {
                if (sms.length() > 40) {
                    temp = sms.substring(0, 40);
                    temp += "...";
                } else {
                    temp = sms.substring(0, sms.length());
                }

            }
        } catch (Exception ex) {

        }

        return temp;
    }
}
