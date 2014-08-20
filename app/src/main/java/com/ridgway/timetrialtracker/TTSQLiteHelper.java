package com.ridgway.timetrialtracker;

/**
 * Created by ridgway on 7/27/14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TTSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_SMS = "timetrial";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RIDER = "rider";
    public static final String COLUMN_TIMESPLIT = "time_split";

    private static final String DATABASE_NAME = "tt.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SMS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_RIDER + " text not null, "
            + COLUMN_TIMESPLIT + " text not null);";


    private static final String QUERY_ALL_DESC = "SELECT "
            + COLUMN_ID + ", "
            + COLUMN_RIDER + ", "
            + COLUMN_TIMESPLIT + " FROM " + TABLE_SMS
            + " ORDER BY " + COLUMN_ID + " DESC";


    public TTSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.w(TTSQLiteHelper.class.getName(), "Create the SQLite Database to track rider splits");
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TTSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        onCreate(db);
    }

    // Add a response to the database
    public void addRiderSplit(String rider, String time){
        Log.d("TTSQLiteHelper: addRiderSplit: ", rider + ", " + time);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(COLUMN_RIDER, rider); // write number
        values.put(COLUMN_TIMESPLIT, time); // write number

        // 3. insert the new number. Date/Time will be added
        // automatically by SQLite, due to the default option on the column.
        db.insert(TABLE_SMS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    // We want to be able to wipe out the contents of the database
    public void deleteAllRiderSplits(){
        List<Integer> responses = getAllRiderSplits();
        ListIterator<Integer> listIterator = responses.listIterator();
        while (listIterator.hasNext()) {
            deleteRiderSplit(listIterator.next());
        }

    }

    // Get all the stored responses
    public List<Integer> getAllRiderSplits() {
        List<Integer> responses = new LinkedList<Integer>();

        // get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_ALL_DESC, null);

        // go over each row, grab the id column value and add it to list
        int id;
        if (cursor.moveToFirst()) {
            do {
                id = Integer.parseInt(cursor.getString(0));
                Log.d("TTSQLiteHelper: getAllRiderSplits()", ""+id);

                // Add response id to list
                responses.add(id);
            } while (cursor.moveToNext());
        }

        // return books
        return responses;
    }

    // Get all the data return the appropriate Cursor.
    public Cursor getAllData(){

        Log.d("TTSQLiteHelper", "getAllData SQL: " + QUERY_ALL_DESC);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(QUERY_ALL_DESC, null);
    }

    // Delete a stored response by database id value.
    public void deleteRiderSplit(Integer id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_SMS, //table name
                COLUMN_ID + " = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("TTSQLiteHelper: deleteRiderSplit", ""+id);

    }


    public int getRiderSplitCount(String number) {
        String countQuery = "SELECT  * FROM " + TABLE_SMS
                + " WHERE " + COLUMN_RIDER + "='" + number + "'";

        Log.d("TTSQLiteHelper: getResponseCount", "Query String: " + countQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return count;
    }
}