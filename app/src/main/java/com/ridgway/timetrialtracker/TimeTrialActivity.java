package com.ridgway.timetrialtracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    private int selectedRiderPosition;

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

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Log.d("TimeTrialActivity: OnItemClickListener: position:", ""+position);

            // user clicked a list item, make it "selected"
            ttAdapter.setSelectedPosition(position);
            selectedRiderPosition = position;
            enableStartRiderButton();
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
                ttAdapter = new TTCursorAdapter(TimeTrialActivity.this, db.getAllMainScreenRiderData());
                listView.setAdapter(ttAdapter);
            }
        });

        listView.setOnItemClickListener(mMessageClickedHandler);
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
            openRiderStatsActivity();
            return true;
        }
        if (id == R.id.action_lap_stats) {
            openLapStatsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Respond to the Add Rider menu item
     */
    public void openAddRiderActivity(){
        // Open the Add Rider panel
        Intent intent = new Intent(this, TTAddRider.class);
        startActivity(intent);
    }

    /**
     * Respond to the Lap Stats  menu item
     */
    public void openLapStatsActivity(){
        // Open the Add Rider panel
        Intent intent = new Intent(this, TTLapSplitsActivity.class);
        startActivity(intent);
    }

    /**
     * Respond to the Rider Stats  menu item
     */
    public void openRiderStatsActivity(){
        // Open the Add Rider panel
        Intent intent = new Intent(this, TTRiderStats.class);
        startActivity(intent);
    }

    private void updateTimer (float time){

        TimeString timeString = Utils.floatToTimeString(time);
        ((TextView)findViewById(R.id.timer)).setText(timeString.getCurrentElapsedTime());
        ((TextView)findViewById(R.id.millis)).setText(timeString.getCurrentMillis());

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

        enableStartRiderButton();

        // set the selected listview position to the top
        ttAdapter.setSelectedPosition(0);
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
        TextView txtRiderNum = (TextView)findViewById(R.id.rider_number);
        TextView txtRiderName = (TextView)findViewById(R.id.txtRider);
        TextView txtRiderTime = (TextView)findViewById(R.id.txtRiderTime);

        // Get the selected rider info
        int selectedPos = ttAdapter.getSelectedPosition();

        Cursor cursor = (Cursor)ttAdapter.getItem(selectedPos);


        String riderNum = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
        String riderName = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));
        float riderTime = elapsedTime;

        // convert the elapsed milliseconds time into a string we can
        // set into the textview.
        TimeString timeString = Utils.floatToTimeString(riderTime);
        String strRiderTime = timeString.getCurrentElapsedTime() + timeString.getCurrentMillis();

        // write the rider info to the Last Seen Rider views
        txtRiderNum.setText(riderNum);
        txtRiderName.setText(riderName);
        txtRiderTime.setText(strRiderTime);


        // write the rider info to the splits table
        db.addRiderSplit(riderNum, riderTime);
        db.updateRiderLastSeen(riderNum, riderTime);
        updateDataChanged();

        // set the selected position back to the top
        ttAdapter.setSelectedPosition(0);

        // In a background tasks, Run through all the laps for this rider
        // and update the avg lap and std-dev values in the table
        new TTStdDevAsyncTask(getApplicationContext()).execute(riderNum);

    }

    public void updateDataChanged(){
        ttAdapter.changeCursor(db.getAllMainScreenRiderData());
    }

    private void enableStartRiderButton(){
        int stopButtonVisibility = (findViewById(R.id.btnStop)).getVisibility();
        if(stopButtonVisibility == View.VISIBLE) {
            Button riderBtnStart = (Button)(findViewById(R.id.btnStartRider));
            riderBtnStart.setEnabled(true);

            int selectedPos = ttAdapter.getSelectedPosition();
            Cursor cursor = (Cursor)ttAdapter.getItem(selectedPos);

            String riderLast = "0";
            if(selectedPos >= 0) {
                // If something is selected, check the last seen time so
                // we can adjust the button text for Start vs. Checkin
                riderLast = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2)));
                Log.d("TimeTrialActivity: enableStartRiderButton: riderLastSeen:", ""+riderLast);
            }

            if(riderLast.compareTo("0") == 0){
                riderBtnStart.setText(getResources().getString(R.string.riderBtnStart));
            }
            else{
                riderBtnStart.setText(getResources().getString(R.string.riderBtnCheckin));
            }
        }
    }

    private void showStopButton(){
        (findViewById(R.id.btnStart)).setVisibility(View.GONE);
        (findViewById(R.id.btnRest)).setVisibility(View.GONE);
        (findViewById(R.id.btnStop)).setVisibility(View.VISIBLE);
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
