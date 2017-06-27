package com.example.achilleas.memoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/*this class handles the database in which the Memo object data is stored edited and deleted
  this class was taken from http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
   and then modified where appropriate
  */
public class DatabaseHandler extends SQLiteOpenHelper {
    // Database version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "memosManager";

    // Memos table name
    private static final String TABLE_MEMOS = "memo_table";

    // Memos Table Columns names
    private static final String KEY_MEMO = "memo";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_MODE = "mode";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMO_TABLE = "CREATE TABLE " + TABLE_MEMOS + "("
                +  KEY_MEMO + " TEXT PRIMARY KEY,"
                + KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_MODE + " TEXT" + ")";
        db.execSQL(CREATE_MEMO_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMOS);

        // Create tables again
        onCreate(db);
    }
    // Adding new memo
    public void addMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEMO, memo.getMemo()); // Memo text
        values.put(KEY_DATE, memo.getDate()); // Memo Date
        values.put(KEY_TIME, memo.getTime()); //Memo time
        values.put(KEY_MODE, memo.getMode()); //Memo mode

        // Inserting Row
        db.insert(TABLE_MEMOS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single memo
    public Memo getMemo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MEMOS, new String[] {
                        KEY_MEMO, KEY_DATE, KEY_TIME, KEY_MODE }, KEY_MEMO + "=?",
                        new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        Memo memo = new Memo(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return memo;
    }

    // Getting All memos
    public ArrayList<Memo> getAllMemos() {
        ArrayList<Memo> memoList = new ArrayList<Memo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MEMOS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Memo memo = new Memo();
                memo.setMemo(cursor.getString(0));
                memo.setDate(cursor.getString(1));
                memo.setTime(cursor.getString(2));
                memo.setMode(cursor.getString(3));
                // Adding memo to list
                memoList.add(memo);
            } while (cursor.moveToNext());
        }

        return memoList;
    }

    // Getting memos Count
    public int getMemosCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MEMOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
    // Updating single memo
    public int updateMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEMO, memo.getMemo());
        values.put(KEY_DATE, memo.getDate());
        values.put(KEY_TIME, memo.getTime());
        values.put(KEY_MODE, memo.getMode());

        // updating row
        return db.update(TABLE_MEMOS, values, KEY_MEMO + " = ?",
                new String[] { String.valueOf(memo.getMemo()) });
    }

    // Deleting single memo
    public void deleteMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMOS, KEY_MEMO + " = ?",
                new String[] { String.valueOf(memo.getMemo()) });
        db.close();
    }
    public void deleteAllMemos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMOS, null, null);
        db.close();
    }
}
