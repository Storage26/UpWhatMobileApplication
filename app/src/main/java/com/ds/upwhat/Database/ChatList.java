package com.ds.upwhat.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ds.upwhat.Objects.ChatListObject;

import java.util.ArrayList;

public class ChatList extends SQLiteOpenHelper
{
    Context context;

    public ChatList(Context context)
    {
        super(context, "ChatListStorage", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query = "CREATE TABLE Storage (email TEXT PRIMARY KEY, name TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void Add(String email, String name)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM Storage WHERE email='" + email + "';";
        Cursor cursor = database.rawQuery(query, null);

        if (!cursor.moveToFirst())
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("email", email);
            contentValues.put("name", name);
            database.insert("Storage", null, contentValues);
        }

        cursor.close();
    }

    public ChatListObject GetList()
    {
        ChatListObject chatListObject = new ChatListObject();
        ArrayList<String> listEmail = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM Storage";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do
            {
                String email = cursor.getString(0);
                String name = cursor.getString(1);
                listEmail.add(email);
                listName.add(name);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        chatListObject.email = listEmail;
        chatListObject.name = listName;
        return chatListObject;
    }
}
