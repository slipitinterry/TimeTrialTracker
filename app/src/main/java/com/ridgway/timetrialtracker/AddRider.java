package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

import com.ridgway.timetrialtracker.R;

public class AddRider extends Activity
                      implements AddRiderNameFragment.AddRiderNameFragmentListener {

    boolean mDebug = true;

    private TTSQLiteHelper db; // Database link for storing response information
    private ListView listView; // Main activity ListView to display recent responses
    private TTRiderCursorAdapter ttRiderAdapter; // Adapter between the database and ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rider);

        // Get a handle to our database, so we can store/retrieve
        // recent responses.
        db = new TTSQLiteHelper(this);

        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff.
        // Start this early, so we can get everything else
        // up and running while this goes on. Limits the perceived delay.
        listView = (ListView) findViewById(R.id.listViewRider);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ttRiderAdapter = new TTRiderCursorAdapter(AddRider.this, db.getAllRiderData());
                listView.setAdapter(ttRiderAdapter);
            }
        });

    }

    public void onAddRider (View view){
        // open dialog to get rider name from user
        DialogFragment dialog = new AddRiderNameFragment();
        dialog.show(getFragmentManager(), "AddRiderNameFragment");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_rider, menu);
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
                        ttRiderAdapter.changeCursor(db.getAllRiderData());
                    }
                })
                .setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do nothing onclick of CANCEL
                    }
                }).show();

    }

    /**
     * Methods used for Duration Picker Dialog and receiving
     * responses and retrieving values from the dialog.
     */
    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddRiderNameFragment();
        dialog.show(getFragmentManager(), "DurationPickerDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the DurationPickerDialog.DurationPickerDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Do nothing here. Just to satisfy onClickListener implementation
    }

    public void onDialogPositiveClick(DialogFragment dialog, EditText riderName) {
        // Add rider to database, auto-generate ID, with other fields empty
        // add to the database
        String rider_name = riderName.getText().toString();
        db.addRider(rider_name);
        ttRiderAdapter.changeCursor(db.getAllRiderData());
        Log.d("TimeTrialTracker", "onDialogPositiveClick: Add Rider Completed.");

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d("TimeTrialTracker", "onDialogNegativeClick: Add Rider Canceled.");

    }



}
