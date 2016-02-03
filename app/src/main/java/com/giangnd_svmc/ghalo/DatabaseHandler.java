package com.giangnd_svmc.ghalo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.giangnd_svmc.ghalo.entity.Message;

import java.util.ArrayList;

/**
 * Created by GIANGND-SVMC on 23/01/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public final static String DB_NAME = "ghalo";
    public final static int DB_VERSION = 1;

    public final static String DB_TABLE = "message";
    public final static String ID = "id";
    public final static String ME = "me";
    public final static String FRIEND = "friend";
    public final static String CONTENT = "content";
    Context context;

    SQLiteDatabase mSQLiteDB;

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        mSQLiteDB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "CREATE TABLE IF NOT EXISTS " + DB_TABLE + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ME + " TEXT, "
                + FRIEND + " TEXT, " + CONTENT + " TEXT);";
        Log.d("SQL = ", sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        String sql = "DROP TABLE IF EXISTS " + DB_TABLE;
        db.execSQL(sql);
        onCreate(db);
    }

    public void addMessage(Message message) {
        ContentValues contenValues = new ContentValues();
        contenValues.put(DatabaseHandler.ME, message.getMe());
        contenValues.put(DatabaseHandler.FRIEND, message.getFriend());
        contenValues.put(DatabaseHandler.CONTENT, message.getContent());
        mSQLiteDB = this.getWritableDatabase();
        mSQLiteDB.insert(DatabaseHandler.DB_TABLE, DatabaseHandler.ME,
                contenValues);
        Toast.makeText(context, "Add succcess", Toast.LENGTH_SHORT).show();
    }


    public ArrayList<Message> getMessages() {
        ArrayList<Message> arrMessage = new ArrayList<Message>();
        Cursor cursor = null;
        mSQLiteDB = this.getWritableDatabase();
        cursor = mSQLiteDB.query(true, this.DB_TABLE, null, null, null, null,
                null, "me", null, null);

        while (cursor.moveToNext()) {
            String me = cursor.getString(1);
            String friend = cursor.getString(2);
            String content = cursor.getString(3);

            arrMessage.add(new Message(me, friend, content));
        }
        return arrMessage;
    }
}
