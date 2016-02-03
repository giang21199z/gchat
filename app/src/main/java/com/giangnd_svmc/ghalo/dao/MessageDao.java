package com.giangnd_svmc.ghalo.dao;

/**
 * Created by GIANGND-SVMC on 03/02/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.giangnd_svmc.ghalo.entity.Account;
import com.giangnd_svmc.ghalo.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    /*Tên database*/
    private static final String DATABASE_NAME = "db_gchat";

    /*Version database*/
    private static final int DATABASE_VERSION = 1;

    /*Tên tabel và các column trong database*/
    private static final String TABLE_MESSAGE = "message";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ME = "me";
    public static final String COLUMN_FRIEND = "friend";
    public static final String COLUMN_CONTENT = "content";

    /*Các đối tượng khác*/
    private static Context context;
    static SQLiteDatabase db;
    private OpenHelper openHelper;

    /*Hàm dựng, khởi tạo đối tượng*/
    public MessageDao(Context c) {
        MessageDao.context = c;
    }

    /*Hàm mở kết nối tới database*/
    public MessageDao open() throws SQLException {
        openHelper = new OpenHelper(context);
        db = openHelper.getWritableDatabase();
        return this;
    }

    /*Hàm đóng kết nối với database*/
    public void close() {
        openHelper.close();
    }

    /*Hàm createData dùng để chèn dữ mới dữ liệu vào database*/
    public long createData(Message message) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ME, message.getMe());
        cv.put(COLUMN_FRIEND, message.getFriend());
        cv.put(COLUMN_CONTENT, message.getContent());
        return db.insert(TABLE_MESSAGE, null, cv);
    }

    /*Hàm getData trả về toàn bộ dữ liệu của table ACCOUNT của database dưới 1 chuỗi*/
    public String getData() {
        String[] columns = new String[]{COLUMN_ID, COLUMN_ME, COLUMN_FRIEND, COLUMN_CONTENT};
        Cursor c = db.query(TABLE_MESSAGE, columns, null, null, null, null, null);
        /*if(c==null)
            Log.v("Cursor", "C is NULL");*/
        String result = "";
        //getColumnIndex(COLUMN_ID); là lấy chỉ số, vị trí của cột COLUMN_ID ...
        int iRow = c.getColumnIndex(COLUMN_ID);
        int iM = c.getColumnIndex(COLUMN_ME);
        int iF = c.getColumnIndex(COLUMN_FRIEND);
        int iC = c.getColumnIndex(COLUMN_CONTENT);

        //Vòng lặp lấy dữ liệu của con trỏ
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = result + " " + c.getString(iRow)
                    + " - me:" + c.getString(iM)
                    + " - friend:" + c.getString(iF)
                    + " - content:" + c.getString(iC) + "\n";
        }
        c.close();
        //Log.v("Result", result);
        return result;
    }

    public List<Message> getHistoryMessage(Account friend, String limit) {
        List<Message> list = new ArrayList<>();
        String[] columns = new String[]{COLUMN_ID, COLUMN_ME, COLUMN_FRIEND, COLUMN_CONTENT};
        Cursor c = db.query(TABLE_MESSAGE, columns, COLUMN_ME + "=? OR " + COLUMN_FRIEND + "=?", new String[]{friend.getName(), friend.getName()}, null, null, "_id DESC", limit);
        if (c != null) {
            //getColumnIndex(COLUMN_ID); là lấy chỉ số, vị trí của cột COLUMN_ID ...
            int iM = c.getColumnIndex(COLUMN_ME);
            int iF = c.getColumnIndex(COLUMN_FRIEND);
            int iC = c.getColumnIndex(COLUMN_CONTENT);

            //Vòng lặp lấy dữ liệu của con trỏ
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String sMe = c.getString(iM);
                String sFriend = c.getString(iF);
                String content = c.getString(iC);
                Message message = new Message(sMe, sFriend, content);
                list.add(message);
            }
        }
        c.close();

        return list;
    }

    //---------------- class OpenHelper ------------------
    private static class OpenHelper extends SQLiteOpenHelper {

        /*Hàm dựng khởi tạo 1 OpenHelper*/
        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /*Tạo mới database*/
        @Override
        public void onCreate(SQLiteDatabase arg0) {
            arg0.execSQL("CREATE TABLE " + TABLE_MESSAGE + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ME + " TEXT NOT NULL, "
                    + COLUMN_FRIEND + " TEXT NOT NULL, "
                    + COLUMN_CONTENT + " TEXT NOT NULL);");
        }

        /*Kiểm tra phiên bản database nếu khác sẽ thay đổi*/
        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
            onCreate(arg0);
        }
    }
}