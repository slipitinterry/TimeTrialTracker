package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ShareActionProvider;


public class TTLapSplitsActivity extends Activity {

    private TTSQLiteHelper db; // Database link for storing lap information
    private ListView listView; // ListView to display lap data
    private TTLapCursorAdapter ttLapAdapter; // Adapter between the database and ListView

    private ShareActionProvider mShareActionProvider;

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
        listView = (ListView) findViewById(R.id.listViewLapSplits);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ttLapAdapter = new TTLapCursorAdapter(TTLapSplitsActivity.this, db.getAllLapDataAndRiderName());
                listView.setAdapter(ttLapAdapter);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ttlap_splits, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        ShareData();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clear) {
            clearLapData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Grab the data from the database and format it as a CSV string
     * then pass that to the intent for the Simple Share provider.
     */
    private void ShareData(){

        String lapCSVData = db.getAllLapsAsCSVString();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, lapCSVData);
        sendIntent.setType("text/plain");
        Intent.createChooser(sendIntent, getResources().getText(R.string.send_to));

        setShareIntent(sendIntent);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }



    /**
     * Update the database to remove all the rider info
     * and update the listview to reflect the new data
     */
    private void clearLapData(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.acknowledge_delete_laps_title )
                .setMessage(R.string.acknowledge_delete_laps_msg)
                .setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do stuff onclick of YES
                        // clear the database
                        db.deleteAllLaps();
                        // update the listView
                        ttLapAdapter.changeCursor(db.getAllLapDataAndRiderName());
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
                        ttLapAdapter.changeCursor(db.getAllRiderData());
                    }
                })
                .setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do nothing onclick of CANCEL
                    }
                }).show();

    }


}
