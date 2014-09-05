package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

        // Get a handle to our database, so we can store/retrieve
        // recent responses.
        db = new TTSQLiteHelper(this);

        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff.
        // Start this early, so we can get everything else
        // up and running while this goes on. Limits the perceived delay.
        listView = (ListView) findViewById(R.id.listViewRiderStats);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ttRiderStatsAdapter = new TTRiderStatsCursorAdapter(TTRiderStats.this, db.getAllRiderData());
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
        if (id == R.id.action_clear) {
            clearRiderData();
            return true;
        }
        if (id == R.id.action_clear_everything) {
            clearAllData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the database to remove all the rider info
     * and update the listview to reflect the new data
     */
    private void clearRiderData(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.acknowledge_delete_riders_title )
                .setMessage(R.string.acknowledge_delete_riders_msg)
                .setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do stuff onclick of YES
                        // clear the database
                        db.deleteAllRiders();
                        // update the listView
                        ttRiderStatsAdapter.changeCursor(db.getAllRiderData());
                    }
                })
                .setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do nothing onclick of CANCEL
                    }
                }).show();

    }

    /**
     * Update the database to remove all the rider info
     * and update the listview to reflect the new data
     */
    private void clearAllData(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.acknowledge_delete_everything_title )
                .setMessage(R.string.acknowledge_delete_everything_msg)
                .setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do stuff onclick of YES
                        // clear the database
                        db.wipeDatabases();
                        // update the listView
                        ttRiderStatsAdapter.changeCursor(db.getAllRiderData());
                    }
                })
                .setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do nothing onclick of CANCEL
                    }
                }).show();

    }

}
