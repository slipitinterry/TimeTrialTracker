package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class TTRiderStats extends Activity {

    private TTSQLiteHelper db; // Database link for storing lap information
    private ListView listView; // ListView to display lap data
    private TTRiderStatsCursorAdapter ttRiderStatsAdapter; // Adapter between the database and ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttrider_stats);


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ttlap_splits);

            // Get a handle to our database, so we can store/retrieve
            // recent responses.
            db = new TTSQLiteHelper(this);

            // Database query can be a time consuming task ..
            // so its safe to call database query in another thread
            // Handler, will handle this stuff.
            // Start this early, so we can get everything else
            // up and running while this goes on. Limits the perceived delay.
            listView = (ListView) findViewById(R.id.listViewAddRider);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ttRiderStatsAdapter = new TTRiderStatsCursorAdapter(TTRiderStats.this, db.getAllLapDataAndRiderName());
                    listView.setAdapter(ttRiderStatsAdapter);
                }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ttrider_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
