package com.ridgway.timetrialtracker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ridgway on 9/1/14.
 */
public class TTStdDevAsyncTask extends AsyncTask<String, Integer, Long> {

    private TTSQLiteHelper db; // Database link for storing lap information
    private final Context mContext;

    public TTStdDevAsyncTask(Context context){
        super();
        mContext = context;
    }

    protected Long doInBackground(String... rider_id) {
        Long status = 0L;

        db = new TTSQLiteHelper(mContext);
        String riderNum = rider_id[0];
        int count = db.getRiderSplitCount(riderNum);
        if(count > 1){
            // so, we have more than one element in the laps
            // table for this rider, so let's start by gathering all
            // of the laps.
            Log.d("TTStdDevAsyncTask: doInBackground", "We have multiple Laps");


            // Once we have all the laps, create an array of lap times
            // by traversing the list and subtracting the first time from the
            // second, the second from the third, etc.
            Log.d("TTStdDevAsyncTask: doInBackground", "Got Lap splits");
            List<Float> rider_lap_times = db.getAllRiderLaps(riderNum);
            List<Float> rider_splits = new LinkedList<Float>();
            ListIterator<Float> iter = rider_lap_times.listIterator();
            Float start = iter.next();
            while(iter.hasNext()){
                Float next = iter.next();
                rider_splits.add(next - start);
                start = next;
            }


            // Now we can calculate the average lap time by adding all the values
            // and dividing by the number of laps minus 1.
            // And write this into the database table.
            Log.d("TTStdDevAsyncTask: doInBackground", "Average Lap Time: ");

            ListIterator<Float> iterAvg = rider_splits.listIterator();
            int lapCount = 1;
            float fAvg = iterAvg.next();
            while(iterAvg.hasNext()){
                fAvg = fAvg + iterAvg.next();
                lapCount++;
            }

            fAvg = fAvg / (float)lapCount;

            db.updateRiderAvgLap(riderNum, fAvg);


            // Using the avg lap time, we can calculate the next ETA
            // Last Seen + Avg
            // And write that in the database table, too.
            Log.d("TTStdDevAsyncTask: doInBackground", "ETA: ");
            float fLast = db.getRiderLastSeen(riderNum);
            float fETA = fLast + fAvg;

            db.updateRiderETA(riderNum, fETA);


            // Now we can calculate the std-deviation.
            // ABS value of lap - avg, for each lap
            // Then avg those values.
            // Finally, write that value into the database
            Log.d("TTStdDevAsyncTask: doInBackground", "Std-Dev: ");
            float fStdDev = 0.0f;

            List<Float> rider_std_dev = new LinkedList<Float>();

            ListIterator<Float> iterAvgStdDev = rider_splits.listIterator();
            int stdDevCount = 1;
            float diff = fAvg - iterAvgStdDev.next();
            rider_std_dev.add(diff);
            while(iterAvgStdDev.hasNext()){
                diff = fAvg - iterAvgStdDev.next();
                rider_std_dev.add(diff);
                stdDevCount++;
            }

            ListIterator<Float> iterDiffs = rider_std_dev.listIterator();
            fStdDev = iterDiffs.next();
            while(iterDiffs.hasNext()){
                float next = iterDiffs.next();
                fStdDev = fStdDev + next;
            }

            fStdDev = fStdDev / (float)stdDevCount;

            db.updateRiderStdDev(riderNum, fStdDev);


            // OK, we're done!
            Log.d("TTStdDevAsyncTask: doInBackground", "DONE!");

        }

        return status;
    }

    protected void onPostExecute(Long result) {
        Log.d("TTStdDevAsyncTask: onPostExecute", "DONE!");
    }

}