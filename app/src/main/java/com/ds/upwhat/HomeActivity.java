package com.ds.upwhat;

import android.content.BroadcastReceiver;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ds.upwhat.DialogCreator.DialogManager;

import Tools.Tool;

public class HomeActivity extends AppCompatActivity {

    ChatsFragment chatsFragment;
    StatusFragment statusFragment;
    CallsFragment callsFragment;

    TextView TabItemChats;
    TextView TabItemStatus;
    TextView TabItemCalls;

    ImageView ProfileButton;

    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Tool.Hide(getSupportActionBar());
        SetupVariables();
        SetupFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();

        BindListeners();

        // Default Fragment
        SwitchFragment(1);
    }

    private void BindListeners()
    {
        TabItemChats.setOnClickListener(view -> SwitchFragment(1));
        TabItemStatus.setOnClickListener(view -> SwitchFragment(2));
        TabItemCalls.setOnClickListener(view -> SwitchFragment(3));
        ProfileButton.setOnClickListener(view -> {
            DialogManager dialogManager = new DialogManager();
            dialogManager.MakeProfileDialog(this, this);
        });
    }

    private void SwitchFragment(int id)
    {
        if (id == 1)
        {
            manager.beginTransaction().hide(statusFragment).commit();
            manager.beginTransaction().hide(callsFragment).commit();
            manager.beginTransaction().show(chatsFragment).commit();

            TabItemStatus.setBackground(null);
            TabItemStatus.setTextColor(Color.parseColor("#afffd1"));
            TabItemCalls.setBackground(null);
            TabItemCalls.setTextColor(Color.parseColor("#afffd1"));

            TabItemChats.setBackgroundResource(R.drawable.activity_home_tab_item_background);
            TabItemChats.setTextColor(Color.parseColor("#00e161"));
        }

        else if (id == 2)
        {
            manager.beginTransaction().hide(chatsFragment).commit();
            manager.beginTransaction().hide(callsFragment).commit();
            manager.beginTransaction().show(statusFragment).commit();

            TabItemChats.setBackground(null);
            TabItemChats.setTextColor(Color.parseColor("#afffd1"));
            TabItemCalls.setBackground(null);
            TabItemCalls.setTextColor(Color.parseColor("#afffd1"));

            TabItemStatus.setBackgroundResource(R.drawable.activity_home_tab_item_background);
            TabItemStatus.setTextColor(Color.parseColor("#00e161"));
        }

        else if (id == 3)
        {
            manager.beginTransaction().hide(statusFragment).commit();
            manager.beginTransaction().hide(chatsFragment).commit();
            manager.beginTransaction().show(callsFragment).commit();

            TabItemStatus.setBackground(null);
            TabItemStatus.setTextColor(Color.parseColor("#afffd1"));
            TabItemChats.setBackground(null);
            TabItemChats.setTextColor(Color.parseColor("#afffd1"));

            TabItemCalls.setBackgroundResource(R.drawable.activity_home_tab_item_background);
            TabItemCalls.setTextColor(Color.parseColor("#00e161"));
        }
    }

    private void SetupVariables()
    {
        // Interface Variables
        TabItemChats = findViewById(R.id.HomeActivity_TabItemChats);
        TabItemStatus = findViewById(R.id.HomeActivity_TabItemStatus);
        TabItemCalls = findViewById(R.id.HomeActivity_TabItemCalls);
        ProfileButton = findViewById(R.id.HomeActivity_ProfileButton);

        // Other Variables
        chatsFragment = new ChatsFragment();
        statusFragment = new StatusFragment();
        callsFragment = new CallsFragment();
        manager = getSupportFragmentManager();
    }

    private void SetupFragments()
    {
        manager.beginTransaction()
                .add(R.id.HomeActivity_MainFrameLayout, chatsFragment, "CHATS")
                .add(R.id.HomeActivity_MainFrameLayout, statusFragment, "STATUS")
                .add(R.id.HomeActivity_MainFrameLayout, callsFragment, "CALLS")
                .commit();

        manager.beginTransaction().hide(chatsFragment);
        manager.beginTransaction().hide(statusFragment);
        manager.beginTransaction().hide(callsFragment);
    }
}