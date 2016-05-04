package com.giangnd_svmc.ghalo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.R;
import com.giangnd_svmc.ghalo.ReadSMS;
import com.giangnd_svmc.ghalo.SearchPhoneSendSMS;
import com.giangnd_svmc.ghalo.adapter.SMSListAdapter;
import com.giangnd_svmc.ghalo.entity.SMS;

import java.util.ArrayList;

/**
 * Created by GIANGND-SVMC on 27/01/2016.
 */
public class SmsFragment extends Fragment {

    ListView lv;
    SMSListAdapter adapter;
    FloatingActionButton FAB;


    public SmsFragment() {
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
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        lv = (ListView) getActivity().findViewById(R.id.lvProducts);
        FAB = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(),"Toasdsdfasdf",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SearchPhoneSendSMS.class);
                startActivity(intent);
            }
        });
        putSMSToAdapter();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SMS itemValue = (SMS) lv.getItemAtPosition(position);   // lay du lieu tai vi tri click
                ReadSMS readSMS = new ReadSMS(getActivity(), 1, itemValue.get_thread_id());
                readSMS.execute();
            }
        });
    }

    public void putSMSToAdapter() {
        ArrayList<SMS> listThreadIdSMS = (ArrayList<SMS>) getActivity().getIntent().getSerializableExtra("LIST_SMS");
        adapter = new SMSListAdapter(getActivity(), listThreadIdSMS);
        lv.setAdapter(adapter);

    }
}