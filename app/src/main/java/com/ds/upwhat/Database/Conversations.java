package com.ds.upwhat.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Conversations extends SQLiteOpenHelper
{
    Context context;

    public Conversations(Context context)
    {
        super(context, "ConversationsStorage", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void CreateConversation(String email)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        EachUserId eachUserId = new EachUserId(context);
        String id = eachUserId.GetId(email);
        String query = "CREATE TABLE IF NOT EXISTS " + id + " (type TEXT PRIMARY KEY)";
        database.execSQL(query);
    }

    public void Add(String email, String name)
    {
        // Add to Conversations
        CreateConversation(email);

        // Add to ChatList
        ChatList chatList = new ChatList(context);
        chatList.Add(email, name);
    }
}
