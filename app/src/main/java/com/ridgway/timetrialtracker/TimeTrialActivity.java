package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class TimeTrialActivity extends Activity {
    /** The view to show the ad. */
    private AdView adView;
    /* Your ad unit id. Replace with your actual ad unit id. */
    private static final String AD_UNIT_ID = "ca-app-pub-7604167799487973/6453606842";


    private long startTime;
    private long elapsedTime;
    private boolean bStopped = false;

    private String currentElapsedTime;
    private String currentMillis;

    private final int REFRESH_RATE = 50;


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
                ttAdapter = new TTCursorAdapter(TimeTrialActivity.this, db.getAllRiderData());
                listView.setAdapter(ttAdapter);
            }
        });

/*
        // Create an ad.
        AdView adView = (AdView) this.findViewById(R.id.adView);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-7604167799487973/6453606842");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
*/

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
        if (id == R.id.action_add_rider) {
            openAddRiderActivity();
            return true;
        }
        if (id == R.id.action_rider_stats) {
            return true;
        }
        if (id == R.id.action_lap_stats) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Respond to the responses list menu item
     */
    public void openAddRiderActivity(){
        // Open the Add Rider panel
        Intent intent = new Intent(this, AddRider.class);
        startActivity(intent);
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

    public void onStartRider (View view){

    }

    private void showStopButton(){
        (findViewById(R.id.btnStart)).setVisibility(View.GONE);
        (findViewById(R.id.btnRest)).setVisibility(View.GONE);
        (findViewById(R.id.btnStop)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnStartRider)).setEnabled(true);
    }

    private void hideStopButton(){
        (findViewById(R.id.btnStart)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnRest)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnStop)).setVisibility(View.GONE);
        (findViewById(R.id.btnStartRider)).setEnabled(false);
    }

    private void resetTimer(){
        ((TextView)findViewById(R.id.timer)).setText("00:00:00");
        ((TextView)findViewById(R.id.millis)).setText(".0");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called before the activity is destroyed. */
    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
