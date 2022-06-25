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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ds.upwhat.R;

import java.util.ArrayList;

public class Adapter_FragmentChats_ItemsList extends ArrayAdapter<String>
{
    ArrayList<String> emailList;
    ArrayList<String> nameList;
    ArrayList<String> lastMessageList;
    ArrayList<Boolean> presenceStatusList;

    Activity activity;
    Context context;

    public Adapter_FragmentChats_ItemsList(Context context, Activity activity, ArrayList<String> emailList, ArrayList<String> nameList, ArrayList<String> lastMessageList, ArrayList<Boolean> presenceStatusList) {
        super(activity, R.layout.list_item_fragment_chats_items_list, emailList);

        this.context = context;
        this.activity = activity;
        this.emailList = emailList;
        this.nameList = nameList;
        this.lastMessageList = lastMessageList;
        this.presenceStatusList = presenceStatusList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_fragment_chats_items_list, parent, false);

        // Interface Variables
        TextView nameText = row.findViewById(R.id.FragmentChats_ItemsListRow_Name);
        TextView lastMessageText = row.findViewById(R.id.FragmentChats_ItemsListRow_LastMessage);
        View presenceDot = row.findViewById(R.id.FragmentChats_ItemsListRow_PresenceDot);
        LinearLayout container = row.findViewById(R.id.FragmentChats_ItemsListRow_Container);

        // Get Values
        String name = nameList.get(position);
        String lastMessage = lastMessageList.get(position);
        boolean presence = presenceStatusList.get(position);

        // Set Values
        nameText.setText(name);
        if (!lastMessage.trim().isEmpty())
        {
            lastMessageText.setText(lastMessage);
            lastMessageText.setVisibility(View.VISIBLE);
        }
        if (presence)
        {
            presenceDot.setVisibility(View.VISIBLE);
        }

        // Listeners
        container.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction("CHATS_FRAGMENT_LIST__ON_ITEM_CLICK");
            intent.putExtra("Position", position);
            context.sendBroadcast(intent);
        });

        return row;
    }
}