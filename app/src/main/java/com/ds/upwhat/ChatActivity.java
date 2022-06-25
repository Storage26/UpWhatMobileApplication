package com.ds.upwhat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.ds.upwhat.ListAdapters.Adapter_FragmentChat_MessageList;

import java.util.ArrayList;

import Tools.Tool;

public class ChatActivity extends AppCompatActivity {

    private String EMAIL;
    private String NAME;

    ListView listView;

    Adapter_FragmentChat_MessageList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Tool.Hide(getSupportActionBar());
        GetExtras();
        SetupVariables();
    }

    private void SetupVariables()
    {
        listView = findViewById(R.id.ChatActivity_MessageList);
    }

    private void SetupAdapter()
    {
        // todo local adapter
        adapter = new Adapter_FragmentChat_MessageList(this, new String[] {"haha", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"});
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SetupAdapter();
    }

    private void GetExtras()
    {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            if (bundle.containsKey("Email") || bundle.containsKey("Name"))
            {
                EMAIL = bundle.getString("Email");
                NAME = bundle.getString("Name");

                if (EMAIL.isEmpty() || NAME.isEmpty()) finish();
            }
            else finish();
        }
        else finish();
    }
}