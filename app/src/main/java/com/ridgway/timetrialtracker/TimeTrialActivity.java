package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class TimeTrialActivity extends Activity {

    private long startTime;
    private long elapsedTime;
    private boolean bStopped = false;

    private String currentElapsedTime;
    private String currentMillis;

    private final int REFRESH_RATE = 50;

    private boolean mclear_rider_on_entry = true;

    private TTSQLiteHelper db; // Database link for storing response information
    private ListView listView; // Main activity ListView to display recent responses
    private TTCursorAdapter ttAdapter; // Adapter between the database and ListView

    private Handler mHandler = new Handler();

    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_trial);
        resetTimer();
        hideStopButton();

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
                ttAdapter = new TTCursorAdapter(TimeTrialActivity.this, db.getAllData());
                listView.setAdapter(ttAdapter);
            }
        });

        // Add a header row to the listview
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.listview_header, listView, false);
        listView.addHeaderView(header, null, false);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.time_trial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clear) {
            db.deleteAllRiderSplits();
            ttAdapter.changeCursor(db.getAllData());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateTimer (float time){

        long secs = (long)(time/1000);
        long mins = (long)((time/1000)/60);
        long hrs = (long)(((time/1000)/60)/60);
        long millis = (long)(time);

        /** Convert the seconds to String
         * and format to ensure it has
         * a leading zero when required
         */
        secs = secs % 60;
        String seconds = String.valueOf(secs);
        if(secs == 0){ seconds = "00"; }
        if(secs <10 && secs > 0){ seconds = "0"+seconds; }

        /* Convert the minutes to String
         * and format the String
         */
        mins = mins % 60;
        String minutes = String.valueOf(mins);
        if(mins == 0){ minutes = "00"; }
        if(mins <10 && mins > 0){ minutes = "0"+minutes; }

        /**
         * Convert the hours to String
         * and format the String
         */
        String hours = String.valueOf(hrs);
        if(hrs == 0){ hours = "00"; }
        if(hrs <10 && hrs > 0){ hours = "0"+hours; }

        /* Although we are not using milliseconds
         * on the timer in this example
         * code included in the event that it's wanted
         */
        millis = millis % 1000;
        String milliseconds = String.valueOf(millis);
        //if(milliseconds.length()==2){ milliseconds = "0"+milliseconds; }
        //if(milliseconds.length()<=1){ milliseconds = "00"; }
        //milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);

        /**
         * Setting the timer text to the elapsed time
         */
        currentElapsedTime = hours + ":" + minutes + ":" + seconds;
        currentMillis = "."+milliseconds;
        ((TextView)findViewById(R.id.timer)).setText(currentElapsedTime);
        ((TextView)findViewById(R.id.millis)).setText(currentMillis);

    }

    public void onStart(View v){
        showStopButton();
        if(bStopped){
            startTime = System.currentTimeMillis() - elapsedTime;
        }
        else{
            startTime = System.currentTimeMillis();
        }
        mHandler.postDelayed(startTimer, 0);
    }

    public void onStop(View v){
        hideStopButton();
        bStopped = true;
        mHandler.removeCallbacks(startTimer);
    }

    public void onReset (View view){
        bStopped = false;
        resetTimer();
    }

    private void showStopButton(){
        (findViewById(R.id.btnStart)).setVisibility(View.GONE);
        (findViewById(R.id.btnRest)).setVisibility(View.GONE);
        (findViewById(R.id.btnStop)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnEnter)).setEnabled(true);
    }

    private void hideStopButton(){
        (findViewById(R.id.btnStart)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnRest)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnStop)).setVisibility(View.GONE);
        (findViewById(R.id.btnEnter)).setEnabled(false);
    }

    private void resetTimer(){
        ((TextView)findViewById(R.id.timer)).setText("00:00:00");
        ((TextView)findViewById(R.id.millis)).setText(".0");
    }

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
            db.addRiderSplit(rider, currentElapsedTime + currentMillis);
            ttAdapter.changeCursor(db.getAllData());
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
        TextView txtRider = (TextView)findViewById(R.id.txtRider);
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
