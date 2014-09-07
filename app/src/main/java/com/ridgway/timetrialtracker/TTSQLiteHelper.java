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
import android.widget.TextView;

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
    public static final String COLUMN_RIDER_LAP_COUNT = "lap_count";

    public static final String TABLE_LAP = "timetriallap";
    public static final String COLUMN_LAP_ID = "_id";
    public static final String COLUMN_LAP_RIDER = "rider_id";
    public static final String COLUMN_LAP_TIMESPLIT = "time_split";

    private static final String VIEW_LAP_STATS = "view_laps_stats";

    private static final String DATABASE_NAME = "tt.db";
    private static final int DATABASE_VERSION = 5;

    // Database creation sql statement
    private static final String DATABASE_CREATE_RIDER = "create table "
            + TABLE_RIDER + " ("
            + COLUMN_RIDER_ID + " integer primary key autoincrement, "
            + COLUMN_RIDER_NAME + " text not null, "
            + COLUMN_RIDER_LAST_SEEN + " date, "
            + COLUMN_RIDER_ETA + " date, "
            + COLUMN_RIDER_AVG_LAP + " numeric, "
            + COLUMN_RIDER_STD_DEV + " numeric,"
            + COLUMN_RIDER_LAP_COUNT + " integer);";

    // Database creation sql statement
    private static final String DATABASE_CREATE_LAPS = "create table "
            + TABLE_LAP + "("
            + COLUMN_LAP_ID + " integer primary key autoincrement, "
            + COLUMN_LAP_RIDER + " integer not null, "
            + COLUMN_LAP_TIMESPLIT + " date not null);";

    private static final String VIEW_CREATE_ALL_LAPS_WITH_RIDERS = "CREATE VIEW " + VIEW_LAP_STATS + " AS "
            + " SELECT "
            + TABLE_LAP+"."+COLUMN_LAP_RIDER + ", "
            + TABLE_RIDER+"."+COLUMN_RIDER_NAME + ", "
            + TABLE_LAP+"."+COLUMN_LAP_TIMESPLIT + ", "
            + TABLE_RIDER+"."+COLUMN_RIDER_ID
            + " FROM " + TABLE_LAP + ", " + TABLE_RIDER
            + " WHERE " + TABLE_LAP+"."+COLUMN_LAP_RIDER + "=" + TABLE_RIDER+"."+COLUMN_RIDER_ID
            + " ORDER BY " + TABLE_LAP+"."+COLUMN_LAP_RIDER + ", " + TABLE_LAP+"."+COLUMN_LAP_TIMESPLIT + ";";


    private static final String QUERY_ALL_RIDERS = "SELECT * FROM " + TABLE_RIDER
            + " ORDER BY " + COLUMN_RIDER_ETA + ", " + COLUMN_RIDER_LAST_SEEN + ", " + COLUMN_RIDER_ID + ";";

    private static final String QUERY_ALL_RIDERS_DATA = "SELECT * FROM " + TABLE_RIDER
            + " ORDER BY " + COLUMN_RIDER_ID + ";";

    private static final String QUERY_ALL_LAPS = "SELECT * FROM " + TABLE_LAP
            + " ORDER BY " + COLUMN_LAP_RIDER + ", " + COLUMN_LAP_ID + ";";


    public TTSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.w(TTSQLiteHelper.class.getName(), "Create the SQLite Database to track riders & splits");
        database.execSQL(DATABASE_CREATE_RIDER);
        database.execSQL(DATABASE_CREATE_LAPS);
        database.execSQL(VIEW_CREATE_ALL_LAPS_WITH_RIDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TTSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP VIEW IF EXISTS " + VIEW_LAP_STATS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDER + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAP + ";");
        onCreate(db);
    }


    // Cleanup everything and start fresh
    public void wipeDatabases(){
        Log.d("TTSQLiteHelper: wipeDatabases ", "Cleanup");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP VIEW IF EXISTS " + VIEW_LAP_STATS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDER + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAP + ";");
        onCreate(db);
    }


    // Add a rider split time to the database
    public void addRiderSplit(String rider, float time){
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

        values.put(COLUMN_RIDER_NAME, rider); // write rider name
        values.put(COLUMN_RIDER_LAST_SEEN, 0); // write rider last seen time to zero
        values.put(COLUMN_RIDER_ETA, 0); // write rider eta to zero
        values.put(COLUMN_RIDER_AVG_LAP, 0); // write rider average lap time to zero
        values.put(COLUMN_RIDER_STD_DEV, 0); // write rider lap std-dev to zero
        values.put(COLUMN_RIDER_LAP_COUNT, 0); // write rider lap std-dev to zero

        // 3. insert the new number. Date/Time will be added
        // automatically by SQLite, due to the default option on the column.
        db.insert(TABLE_RIDER, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    // get the last seen value for the rider
    public float getRiderLastSeen(String riderNum){
        Log.d("TTSQLiteHelper: getRiderLastSeen: ", riderNum );
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        String QUERY_RIDER = "SELECT * FROM " + TABLE_RIDER
                + " WHERE " + COLUMN_RIDER_ID + "=" + riderNum + ";"; // use rider id as key to query

        Cursor cursor = db.rawQuery(QUERY_RIDER, null);

        // find the first row, grab the Last Seen column value
        float last_seen = 0.0f;
        if (cursor.moveToFirst()) {
            last_seen = Integer.parseInt(cursor.getString(2));
            Log.d("TTSQLiteHelper: getRiderLastSeen()", "" + last_seen);

            db.close();
        }

        return last_seen;
    }

    // update the last seen value for the rider
    public void updateRiderLastSeen(String riderNum, float riderTime){
        Log.d("TTSQLiteHelper: updateRiderLastSeen: ", riderNum + ", " + riderTime);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        String where = COLUMN_RIDER_ID + "=" + riderNum; // use rider id as key to update call

        values.put(COLUMN_RIDER_LAST_SEEN, riderTime); //write rider split as last seen time

        db.update(TABLE_RIDER, values, where, null);
        db.close();
    }

    // update the ETA value for a rider
    public void updateRiderETA(String riderNum, float riderETA){
        Log.d("TTSQLiteHelper: updateRiderETA: ", riderNum + ", " + riderETA);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        String where = COLUMN_RIDER_ID + "=" + riderNum; // use rider id as key to update call

        values.put(COLUMN_RIDER_ETA, riderETA); //write rider eta

        db.update(TABLE_RIDER, values, where, null);
        db.close();
    }

    // update the rider name value for a rider
    public void updateRiderName(String riderNum, String riderName){
        Log.d("TTSQLiteHelper: updateRiderName: ", riderNum + ", " + riderName);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        String where = COLUMN_RIDER_ID + "=" + riderNum; // use rider id as key to update call

        values.put(COLUMN_RIDER_NAME, riderName); //write rider eta

        db.update(TABLE_RIDER, values, where, null);
        db.close();
    }

    // update the Avg Lap value for a rider
    public void updateRiderAvgLap(String riderNum, float riderAvg){
        Log.d("TTSQLiteHelper: updateRiderETA: ", riderNum + ", " + riderAvg);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        String where = COLUMN_RIDER_ID + "=" + riderNum; // use rider id as key to update call

        values.put(COLUMN_RIDER_AVG_LAP, riderAvg); //write rider avg

        db.update(TABLE_RIDER, values, where, null);
        db.close();
    }


    // update the std-dev value for a rider
    public void updateRiderStdDev(String riderNum, float riderStdDev){
        Log.d("TTSQLiteHelper: UpdateRiderStdDev: ", riderNum + ", " + riderStdDev);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        String where = COLUMN_RIDER_ID + "=" + riderNum; // use rider id as key to update call

        values.put(COLUMN_RIDER_STD_DEV, riderStdDev); //write rider StdDev

        db.update(TABLE_RIDER, values, where, null);
        db.close();
    }

    // update the std-dev value for a rider
    public void updateRiderLapCount(String riderNum, int riderLapCount){
        Log.d("TTSQLiteHelper: UpdateRiderStdDev: ", riderNum + ", " + riderLapCount);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        String where = COLUMN_RIDER_ID + "=" + riderNum; // use rider id as key to update call

        values.put(COLUMN_RIDER_LAP_COUNT, riderLapCount); //write rider StdDev

        db.update(TABLE_RIDER, values, where, null);
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
        List<Integer> riders = new LinkedList<Integer>();

        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_ALL_RIDERS, null);

        // go over each row, grab the id column value and add it to list
        int id;
        if (cursor.moveToFirst()) {
            do {
                id = Integer.parseInt(cursor.getString(0));
                Log.d("TTSQLiteHelper: getAllRiders()", ""+id);

                // Add response id to list
                riders.add(id);
            } while (cursor.moveToNext());
        }

        // return books
        return riders;
    }

    // Get all the stored responses
    public List<Integer> getAllLaps() {
        List<Integer> laps = new LinkedList<Integer>();

        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_ALL_LAPS, null);

        // go over each row, grab the id column value and add it to list
        int id;
        if (cursor.moveToFirst()) {
            do {
                id = Integer.parseInt(cursor.getString(0));
                Log.d("TTSQLiteHelper: getAllLaps()", ""+id);

                // Add response id to list
                laps.add(id);
            } while (cursor.moveToNext());
        }

        db.close();

        // return books
        return laps;
    }

    // Get all the stored laps for a specified rider
    public List<Float> getAllRiderLaps(String riderNum) {

        List<Float> laps = new LinkedList<Float>();

        // Setup Query
        String QUERY_ALL_RIDER_LAPS = "SELECT * FROM " + TABLE_LAP
                + " WHERE " + COLUMN_LAP_RIDER + "=" + riderNum
                + " ORDER BY " + COLUMN_LAP_TIMESPLIT + ";";


        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_ALL_RIDER_LAPS, null);

        // go over each row, grab the id column value and add it to list
        Float lap_split;
        if (cursor.moveToFirst()) {
            do {
                lap_split = Float.parseFloat(cursor.getString(2));
                Log.d("TTSQLiteHelper: getAllRiderLaps()", ""+lap_split);

                // Add response id to list
                laps.add(lap_split);
            } while (cursor.moveToNext());
        }

        db.close();

        // return laps list
        return laps;
    }


    // Get all the data return the appropriate Cursor.
    public Cursor getAllLapDataAndRiderName(){

        String QUERY_ALL_LAPS_WITH_RIDERS = "SELECT * FROM " + VIEW_LAP_STATS +";";

        Log.d("TTSQLiteHelper", "getAllLapDataAndRiderName SQL: " + QUERY_ALL_LAPS_WITH_RIDERS);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(QUERY_ALL_LAPS_WITH_RIDERS, null);
    }

    // Get all the data return the appropriate Cursor.
    public Cursor getAllRiderData(){

        Log.d("TTSQLiteHelper", "getAllData SQL: " + QUERY_ALL_RIDERS_DATA);

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(QUERY_ALL_RIDERS_DATA, null);
    }

    // Get all the data return the appropriate Cursor.
    public Cursor getAllMainScreenRiderData(){

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
                + " WHERE " + COLUMN_LAP_RIDER + "='" + number + "';";

        Log.d("TTSQLiteHelper: getRiderSplitCount", "Query String: " + countQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        Log.d("TTSQLiteHelper: getRiderSplitCount", "Count: " + count);

        // return count
        return count;
    }

    public String getAllLapsAsCSVString() {
        String csvHeader = "\"RiderNumber\", \"RiderName\", \"LapSplit\"";
        String csvValues = "";
        String csvString = "";

        Cursor lapCursor = getAllLapDataAndRiderName();

        if (lapCursor != null) {
            csvString = csvHeader;
            while (lapCursor.moveToNext()) {
                csvValues = lapCursor.getString(lapCursor.getColumnIndex(lapCursor.getColumnName(0))) + ",";

                csvValues += lapCursor.getString(lapCursor.getColumnIndex(lapCursor.getColumnName(1))) + ",";

                String strSplit = lapCursor.getString(lapCursor.getColumnIndex(lapCursor.getColumnName(2)));
                if (strSplit == null || strSplit.isEmpty()) {
                    strSplit = "0";
                }
                TimeString tsRiderSplit = Utils.floatToTimeString(Float.parseFloat(strSplit));
                csvValues += tsRiderSplit.getCurrentElapsedTime();

                csvString += "\n" + csvValues;
            }
        }

        return csvString;
    }

    public String getAllRidersAsCSVString() {
        String csvHeader = "\"RiderNumber\", \"RiderName\", \"AvgLap\", \"Std-Dev\"";
        String csvValues = "";
        String csvString = "";

        Cursor riderCursor = getAllRiderData();

        if (riderCursor != null) {
            csvString = csvHeader;
            while (riderCursor.moveToNext()) {
                csvValues = riderCursor.getString(riderCursor.getColumnIndex(riderCursor.getColumnName(0))) + ",";

                csvValues += riderCursor.getString(riderCursor.getColumnIndex(riderCursor.getColumnName(1))) + ",";

                String strSplit = riderCursor.getString(riderCursor.getColumnIndex(riderCursor.getColumnName(4)));
                if (strSplit == null || strSplit.isEmpty()) {
                    strSplit = "0";
                }
                TimeString tsRiderSplit = Utils.floatToTimeString(Float.parseFloat(strSplit));
                csvValues += tsRiderSplit.getCurrentElapsedTime();

                csvValues += riderCursor.getString(riderCursor.getColumnIndex(riderCursor.getColumnName(5)));

                csvString += "\n" + csvValues;
            }
        }

        return csvString;
    }
}