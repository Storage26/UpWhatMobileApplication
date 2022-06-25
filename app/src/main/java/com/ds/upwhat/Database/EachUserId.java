package com.ds.upwhat.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class EachUserId extends SQLiteOpenHelper
{
    Context context;

    public EachUserId(Context context)
    {
        super(context, "EachUserIdStorage", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query = "CREATE TABLE Storage (email EMAIL PRIMARY KEY, id INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public String GetId(String email)
    {
        String id = "_";
        SQLiteDatabase database = this.getWritableDatabase();

        String query = "SELECT id FROM Storage WHERE email='" + email + "';";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            // Exist
            id += String.valueOf(cursor.getInt(0));
        }
        else
        {
            // Create New
            int new_id = 0;

            while (true)
            {
                String query1 = "SELECT * FROM Storage WHERE id=" + new_id + ";";
                Cursor cursor1 = database.rawQuery(query1, null);

                if (!cursor1.moveToFirst())
                {
                    break;
                }

                cursor1.close();
                new_id++;
            }

            id += String.valueOf(new_id);
        }

        cursor.close();
        return id;
    }
}
