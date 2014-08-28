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

    public static final String TABLE_RIDER = "timetrialrider";
    public static final String COLUMN_RIDER_ID = "_id";
    public static final String COLUMN_RIDER_NAME = "rider_name";
    public static final String COLUMN_RIDER_LAST_SEEN = "last_seen";
    public static final String COLUMN_RIDER_ETA = "eta";
    public static final String COLUMN_RIDER_AVG_LAP = "avg_lap";
    public static final String COLUMN_RIDER_STD_DEV = "std_dev";




    public static final String TABLE_LAP = "timetriallap";
    public static final String COLUMN_LAP_ID = "_id";
    public static final String COLUMN_LAP_RIDER = "rider_id";
    public static final String COLUMN_LAP_TIMESPLIT = "time_split";

    private static final String DATABASE_NAME = "tt.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE_RIDER = "create table "
            + TABLE_RIDER + " ("
            + COLUMN_RIDER_ID + " integer primary key autoincrement, "
            + COLUMN_RIDER_NAME + " text not null, "
            + COLUMN_RIDER_LAST_SEEN + " date, "
            + COLUMN_RIDER_ETA + " date, "
            + COLUMN_RIDER_AVG_LAP + " numeric, "
            + COLUMN_RIDER_STD_DEV + " numeric);";

    // Database creation sql statement
    private static final String DATABASE_CREATE_LAPS = "create table "
            + TABLE_LAP + "("
            + COLUMN_LAP_ID + " integer primary key autoincrement, "
            + COLUMN_LAP_RIDER + " integer not null, "
            + COLUMN_LAP_TIMESPLIT + " text not null);";


    private static final String QUERY_ALL_RIDERS = "SELECT * FROM " + TABLE_RIDER
            + " ORDER BY " + COLUMN_RIDER_ETA + ", " + COLUMN_RIDER_LAST_SEEN + ", " + COLUMN_RIDER_ID;

    private static final String QUERY_ALL_LAPS = "SELECT * FROM " + TABLE_LAP
            + " ORDER BY " + COLUMN_LAP_RIDER + ", " + COLUMN_LAP_ID;


    public TTSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.w(TTSQLiteHelper.class.getName(), "Create the SQLite Database to track riders & splits");
        database.execSQL(DATABASE_CREATE_RIDER);
        database.execSQL(DATABASE_CREATE_LAPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TTSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDER);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAP);
        onCreate(db);
    }

    // Add a rider split time to the database
    public void addRiderSplit(String rider, String time){
        Log.d("TTSQLiteHelper: addRiderSplit: ", rider + ", " + time);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(COLUMN_LAP_RIDER, rider); // write number
        values.put(COLUMN_LAP_TIMESPLIT, time); // write number

        // 3. insert the new number. Date/Time will be added
        // automatically by SQLite, due to the default option on the column.
        db.insert(TABLE_LAP, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    // Add a rider to the database
    public void addRider(String rider){
        Log.d("TTSQLiteHelper: addRider: ", rider);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(COLUMN_LAP_RIDER, rider); // write rider name

        // 3. insert the new number. Date/Time will be added
        // automatically by SQLite, due to the default option on the column.
        db.insert(TABLE_RIDER, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    // We want to be able to wipe out the contents of the database
    public void deleteAllRiders(){
        List<Integer> responses = getAllRiders();
        ListIterator<Integer> listIterator = responses.listIterator();
        while (listIterator.hasNext()) {
            deleteRider(listIterator.next());
        }

    }

    // We want to be able to wipe out the contents of the database
    public void deleteAllLaps(){
        List<Integer> responses = getAllLaps();
        ListIterator<Integer> listIterator = responses.listIterator();
        while (listIterator.hasNext()) {
            deleteLap(listIterator.next());
        }

    }

    // Get all the stored responses
    public List<Integer> getAllRiders() {
        List<Integer> responses = new LinkedList<Integer>();

        // get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_ALL_RIDERS, null);

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

    // Get all the stored responses
    public List<Integer> getAllLaps() {
        List<Integer> responses = new LinkedList<Integer>();

        // get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_ALL_LAPS, null);

        // go over each row, grab the id column value and add it to list
        int id;
        if (cursor.moveToFirst()) {
            do {
                id = Integer.parseInt(cursor.getString(0));
                Log.d("TTSQLiteHelper: getAllLaps()", ""+id);

                // Add response id to list
                responses.add(id);
            } while (cursor.moveToNext());
        }

        // return books
        return responses;
    }

    // Get all the data return the appropriate Cursor.
    public Cursor getAllLapData(){

        Log.d("TTSQLiteHelper", "getAllData SQL: " + QUERY_ALL_LAPS);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(QUERY_ALL_LAPS, null);
    }

    // Get all the data return the appropriate Cursor.
    public Cursor getAllRiderData(){

        Log.d("TTSQLiteHelper", "getAllData SQL: " + QUERY_ALL_RIDERS);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(QUERY_ALL_RIDERS, null);
    }

    // Delete a stored response by database id value.
    public void deleteLap(Integer id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_LAP, //table name
                COLUMN_LAP_ID + " = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("TTSQLiteHelper: deleteRiderSplit", ""+id);

    }

    // Delete a stored response by database id value.
    public void deleteRider(Integer id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_RIDER, //table name
                COLUMN_RIDER_ID + " = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("TTSQLiteHelper: deleteRiderSplit", ""+id);

    }


    public int getRiderSplitCount(String number) {
        String countQuery = "SELECT  * FROM " + TABLE_LAP
                + " WHERE " + COLUMN_RIDER_ID + "='" + number + "'";

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