package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ridgway.timetrialtracker.R;

public class TTAddRider extends Activity
                      implements TTAddRiderNameFragment.AddRiderNameFragmentListener {

    boolean mDebug = true;

    private TTSQLiteHelper db; // Database link for storing response information
    private ListView listView; // Main activity ListView to display recent responses
    private TTRiderCursorAdapter ttRiderAdapter; // Adapter between the database and ListView

    private EditText editTextRider;
    private boolean addingNew = false;
    private boolean editingRider = false;

    private int editingIndex = -1;

    static final private int ADD_NEW_RIDER = Menu.FIRST;
    static final private int REMOVE_RIDER = Menu.FIRST + 1;
    static final private int EDIT_RIDER = Menu.FIRST + 2;
    static final private int DELETE_ALL_RIDERS = Menu.FIRST + 3;

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
        listView = (ListView) findViewById(R.id.listViewAddRider);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ttRiderAdapter = new TTRiderCursorAdapter(TTAddRider.this, db.getAllRiderData());
                listView.setAdapter(ttRiderAdapter);
            }
        });


        editTextRider = (EditText)findViewById(R.id.editTextRider);
        editTextRider.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        String riderName = editTextRider.getText().toString();
                        if(addingNew){
                            db.addRider(riderName);
                        }
                        else {
                            Cursor cursor = (Cursor) ttRiderAdapter.getItem(editingIndex);
                            String riderNum = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                            db.updateRiderName(riderNum, riderName);
                        }

                        editTextRider.setText("");
                        ttRiderAdapter.changeCursor(db.getAllRiderData());
                        cancelAdd();
                        return true;
                    }
                return false;
            }
        });

        registerForContextMenu(listView);

    }

    public void onAddRider (View view) {
        onAddRider();
    }

    public void onAddRider(){

        // open dialog to get rider name from user
        TTAddRiderNameFragment dialog = new TTAddRiderNameFragment();
        dialog.show(getFragmentManager(), "TTAddRiderNameFragment");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_rider, menu);
        // Create and add new menu items.
        MenuItem itemAdd = menu.add(0, ADD_NEW_RIDER, Menu.NONE,
                R.string.btn_add_rider);
        MenuItem itemEdit = menu.add(0, EDIT_RIDER, Menu.NONE,
                R.string.action_edit_rider);
        MenuItem itemRem = menu.add(0, REMOVE_RIDER, Menu.NONE,
                R.string.remove);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Log.d("TTAddRider","inside onPrepareOptionsMenu()");

        int idx = listView.getSelectedItemPosition();

        String removeTitle = getString(addingNew ?
                                R.string.cancel : R.string.remove);

        MenuItem removeItem = menu.findItem(REMOVE_RIDER);
        removeItem.setTitle(removeTitle);
        removeItem.setVisible(addingNew || idx > -1);

        MenuItem editItem = menu.findItem(EDIT_RIDER);
        editItem.setVisible(!addingNew && idx > -1);

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

        int index = listView.getSelectedItemPosition();
        if( index > -1 ) {
            Cursor cursor = (Cursor) ttRiderAdapter.getItem(index);
            String riderNum = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
            Integer riderId = Integer.parseInt(riderNum);

            switch (item.getItemId()) {
                case (REMOVE_RIDER): {
                    if (addingNew) {
                        cancelAdd();
                    } else {
                        removeRider(riderId);
                    }
                    return true;
                }
                case (ADD_NEW_RIDER): {
                    addNewItem();
                    return true;
                }
                case (EDIT_RIDER): {
                    editRider(index, riderId);
                    return true;
                }
                case (DELETE_ALL_RIDERS): {
                    clearRiderData();
                    return true;
                }
            }
        }
        else{
            //Toast.makeText(getBaseContext(), getString(R.string.cancelremove), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        Log.d("TTAddRider","inside onCreateContextMenu()");

        menu.setHeaderTitle(R.string.set_menu_item);
        //menu.add(0, ADD_NEW_RIDER, Menu.NONE, R.string.btn_add_rider);
        menu.add(0, REMOVE_RIDER, Menu.NONE, R.string.remove);
        menu.add(0, EDIT_RIDER, Menu.NONE, R.string.action_edit_rider);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        Log.d("TTAddRider","inside onContextItemSelected()");

        switch (item.getItemId()) {
            case (ADD_NEW_RIDER): {
                onAddRider();
                return true;
            }
            case (REMOVE_RIDER): {
                AdapterView.AdapterContextMenuInfo menuInfo;
                menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int index = menuInfo.position;

                Cursor cursor = (Cursor)ttRiderAdapter.getItem(index);
                String riderId = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));

                removeRider(Integer.parseInt(riderId));
                return true;
            }
            case (EDIT_RIDER): {
                AdapterView.AdapterContextMenuInfo menuInfo;
                menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int index = menuInfo.position;

                Cursor cursor = (Cursor)ttRiderAdapter.getItem(index);
                String riderId = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));

                editRider(index, Integer.parseInt(riderId));
                return true;
            }
        }
        return false;
    }

    private void cancelAdd() {
        addingNew = false;
        editingRider = false;
        editingIndex = -1;
        editTextRider.setVisibility(View.GONE);
    }

    private void editRider(int idx, int rider_num) {
        Log.d("TTAddRider","inside editRider()");
        addingNew = false;
        editingRider = true;
        editingIndex = idx;

        Cursor cursor = (Cursor)ttRiderAdapter.getItem(idx);
        String riderName = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

        editTextRider.setText(riderName);
        editTextRider.setVisibility(View.VISIBLE);
        editTextRider.requestFocus();
    }

    private void addNewItem() {
        Log.d("TTAddRider","inside addNewItem()");

        addingNew = true;
        editTextRider.setText(R.string.add_rider_title);
        editTextRider.setVisibility(View.VISIBLE);
        editTextRider.requestFocus();
    }

    private void removeRider(final int rider_num) {
        Log.d("TTAddRider","inside removeItem(), remove: " + rider_num);

        new AlertDialog.Builder(this)
                .setTitle(R.string.acknowledgeremove_title )
                .setMessage(R.string.acknowledgeremove_msg)
                .setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do stuff onclick of YES
                        db.deleteRider(rider_num);
                        ttRiderAdapter.changeCursor(db.getAllRiderData());
                    }
                })
                .setNegativeButton(R.string.dlg_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do stuff onclick of CANCEL
                        Toast.makeText(getBaseContext(), getString(R.string.cancelremove), Toast.LENGTH_SHORT).show();
                    }
                }).show();
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

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the DurationPickerDialog.DurationPickerDialogListener interface

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, EditText riderName) {
        // Add rider to database, auto-generate ID, with other fields empty
        // add to the database
        String rider_name = riderName.getText().toString();
        Log.d("TimeTrialTracker", "onDialogPositiveClick: Adding Rider: " + rider_name + ".");
        db.addRider(rider_name);
        ttRiderAdapter.changeCursor(db.getAllRiderData());
        Log.d("TimeTrialTracker", "onDialogPositiveClick: Add Rider Completed.");

    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog, EditText riderName) {
        // Add rider to database, auto-generate ID, with other fields empty
        // add to the database
        String rider_name = riderName.getText().toString();
        Log.d("TimeTrialTracker", "onDialogNeutralClick: Adding Rider: " + rider_name + ".");
        db.addRider(rider_name);
        ttRiderAdapter.changeCursor(db.getAllRiderData());
        Log.d("TimeTrialTracker", "onDialogNeutralClick: Add Rider Completed.");

        // open a new dialog to get an additional rider name from user
        TTAddRiderNameFragment dlgRider = new TTAddRiderNameFragment();
        dlgRider.show(getFragmentManager(), "AddRiderNameFragment");

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d("TimeTrialTracker", "onDialogNegativeClick: Add Rider Canceled.");

    }



}
