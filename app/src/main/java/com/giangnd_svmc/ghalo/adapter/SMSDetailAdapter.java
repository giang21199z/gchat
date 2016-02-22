package com.giangnd_svmc.ghalo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.entity.SMS;

import java.util.ArrayList;

/**
 * Created by hoangdd on 2/9/2016.
 */
public class SMSDetailAdapter extends ArrayAdapter<SMS> {
    Activity activity;

    public SMSDetailAdapter(Activity activity, ArrayList<SMS> products) {
        super(activity, 0, products);
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {


        SMS sms = (SMS) getItem(position);

        int viewType = Integer.parseInt(sms.getType());
        switch (viewType) {
            case 1: { //LEFT
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_item_message_left, null);
                break;
            }
            case 2: { //RIGHT
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_item_message_right, null);
                break;
            }
        }
//Convert view is now garunteed to not be null, and to be of the correct type, so now just populate your data.
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        txtMsg.setText(sms.getBody());

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0)
            return 1;  //LEFT
        else
            return 2;  //RIGHT
    }
}
