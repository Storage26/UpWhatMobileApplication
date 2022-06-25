package com.ds.upwhat.ListAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ds.upwhat.R;

import java.util.ArrayList;

public class Adapter_FragmentChat_MessageList extends ArrayAdapter<String>
{
    Activity activity;

    public Adapter_FragmentChat_MessageList(Activity activity, String[] emailList) {
        super(activity, R.layout.list_item_activity_chat_message_list, emailList);

        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_activity_chat_message_list, parent, false);

        return row;
    }
}