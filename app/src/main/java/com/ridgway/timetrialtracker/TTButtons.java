package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ridgway on 8/25/14.
 */
public class TTButtons  extends Activity {

    private boolean mclear_rider_on_entry = true;

    private TTSQLiteHelper db; // Database link for storing response information
    private ListView listView; // Main activity ListView to display recent responses
    private TTCursorAdapter ttAdapter; // Adapter between the database and ListView

    private String currentElapsedTime;
    private String currentMillis;

    /**
     * Handle Clear Button Click
     * @param v
     */
    public void onBtnClear(View v){
        clearRiderNumber();
    }

    /**
     * Handle Enter Button Click
     * @param v
     */
    public void onBtnEnter(View v){
        TextView txtRider = (TextView)findViewById(R.id.txtRider);
        String rider = txtRider.getText().toString();

        if(!rider.isEmpty()) {
            // now write to database
            //db.addRiderSplit(rider, currentElapsedTime + currentMillis);
            ttAdapter.changeCursor(db.getAllRiderData());
        }

        // now clear the rider number, ready for the next entry
        if(mclear_rider_on_entry){
            clearRiderNumber();
        }
    }

    /**
     * Reset the Rider number value to blank, ready for re-entering,
     * or after entering a new record.
     */
    private void clearRiderNumber(){
        TextView txtRider = (TextView)findViewById(R.id.txtRider);
        txtRider.setText("");
    }

    /**
     * Add a new number to the rider number string
     * @param strNum
     */
    private void addCharToRiderNumber(String strNum){
        TextView txtRider;
        txtRider = (TextView)findViewById(R.id.txtRider);
        String currentTxt = txtRider.getText().toString();
        String newTxt = currentTxt + strNum;
        txtRider.setText(newTxt);
    }

    public void onBtn0(View v){
        addCharToRiderNumber("0");
    }


    public void onBtn1(View v){
        addCharToRiderNumber("1");
    }

    public void onBtn2(View v){
        addCharToRiderNumber("2");
    }

    public void onBtn3(View v){
        addCharToRiderNumber("3");
    }

    public void onBtn4(View v){
        addCharToRiderNumber("4");
    }

    public void onBtn5(View v){
        addCharToRiderNumber("5");
    }

    public void onBtn6(View v){
        addCharToRiderNumber("6");
    }

    public void onBtn7(View v){
        addCharToRiderNumber("7");
    }

    public void onBtn8(View v){
        addCharToRiderNumber("8");
    }

    public void onBtn9(View v){
        addCharToRiderNumber("9");
    }
}
