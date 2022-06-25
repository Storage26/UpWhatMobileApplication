package com.ds.upwhat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.ds.upwhat.Database.ChatList;
import com.ds.upwhat.Database.Conversations;
import com.ds.upwhat.ListAdapters.Adapter_FragmentChats_ItemsList;
import com.ds.upwhat.Objects.ChatListObject;

import java.util.ArrayList;

public class ChatsFragment extends Fragment
{
    public ChatsFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_chats, container, false); }

    ImageView NewChatButton;

    LinearLayout ModeEmpty;
    LinearLayout ModeItems;

    ListView listView;

    BroadcastReceiver receiver;

    private static final int MODE_EMPTY = 0;
    private static final int MODE_ITEMS = 1;

    ArrayList<String> emailList;
    ArrayList<String> nameList;
    ArrayList<String> lastMessageList;
    ArrayList<Boolean> presenceStatusList;

    Adapter_FragmentChats_ItemsList adapter;

    @Override
    public void onStart() {
        super.onStart();
        if (getContext() != null)
        {
            SetupVariables();
            BindListeners();
            SetupAdapter();
            RefreshChatList();
            RegisterReceiver();
        }
    }

    // TODO MAKE NEW CONVERSATION DIALOG AND FUNC WITH SERVER SIDE

    private void RegisterReceiver()
    {
        if (getContext() != null)
        {
            // List: OnItemClick
            UnregisterReceiver();

            IntentFilter filter = new IntentFilter();
            filter.addAction("CHATS_FRAGMENT_LIST__ON_ITEM_CLICK");
            receiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int position = intent.getExtras().getInt("Position");

                    String email = emailList.get(position);
                    String name = nameList.get(position);

                    OpenChat(email, name);
                }
            };

            getContext().registerReceiver(receiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        UnregisterReceiver();
        super.onDestroy();
    }

    private void UnregisterReceiver()
    {
        if (receiver != null && getContext() != null)
        {
            getContext().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void SetupAdapter()
    {
        if (getActivity() != null)
        {
            adapter = new Adapter_FragmentChats_ItemsList(getContext(), getActivity(), emailList, nameList, lastMessageList, presenceStatusList);
            listView.setAdapter(adapter);
        }
    }

    private void AddItem(String email, String name, String lastMessage)
    {
        emailList.add(email);
        nameList.add(name);
        lastMessageList.add(lastMessage);
        presenceStatusList.add(false);

        adapter.notifyDataSetChanged();
    }

    private void RefreshChatList()
    {
        // Clear List
        ClearChatList();

        // Add Items
        ChatList chatList = new ChatList(getContext());
        ChatListObject object = chatList.GetList();
        ArrayList<String> emailList = object.email;
        ArrayList<String> nameList = object.name;

        if (!emailList.isEmpty())
        {
            for (int i = 0; i < emailList.size(); i++)
            {
                AddItem(emailList.get(i), nameList.get(i), "Ok. See ya!");
            }

            SetMode(MODE_ITEMS);
        }
        else
        {
            SetMode(MODE_EMPTY);
        }
    }

    private void ClearChatList()
    {
        emailList.clear();
        nameList.clear();
        lastMessageList.clear();
        presenceStatusList.clear();
    }

    private void AddConversationToList(String email, String name)
    {
        if (getContext() != null)
        {
            Conversations conversations = new Conversations(getContext());
            conversations.Add(email, name);

            RefreshChatList();
        }
    }

    private void SetMode(int mode)
    {
        if (mode == MODE_EMPTY)
        {
            ModeItems.setVisibility(View.GONE);
            ModeEmpty.setVisibility(View.VISIBLE);
        }

        if (mode == MODE_ITEMS)
        {
            ModeEmpty.setVisibility(View.GONE);
            ModeItems.setVisibility(View.VISIBLE);
        }
    }

    private void BindListeners()
    {
        NewChatButton.setOnClickListener(view -> NewChat());
        ModeEmpty.setOnClickListener(view -> NewChat());
    }

    private void SetupVariables()
    {
        // ArrayList Variables
        emailList = new ArrayList<>();
        nameList = new ArrayList<>();
        lastMessageList = new ArrayList<>();
        presenceStatusList = new ArrayList<>();

        // Interface Variables
        if (getActivity() != null)
        {
            NewChatButton = getActivity().findViewById(R.id.ChatsFragment_NewChatButton);
            ModeEmpty = getActivity().findViewById(R.id.ChatsFragment_ModeEmpty);
            ModeItems = getActivity().findViewById(R.id.ChatsFragment_ModeItems);
            listView = getActivity().findViewById(R.id.ChatsFragment_ItemsList);
        }
    }

    private void OpenChat(String email, String name)
    {
        if (getContext() != null)
        {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("Email", email);
            intent.putExtra("Name", name);
            startActivity(intent);
        }
    }

    private void NewChat() {}
}