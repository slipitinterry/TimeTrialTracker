package com.ridgway.timetrialtracker;

/**
 * Created by ridgway on 8/29/14.
 */
public class TimeString {

    private String currentElapsedTime;
    private String currentMillis;

    TimeString(String elapsed, String millis){
        currentElapsedTime = elapsed;
        currentMillis = millis;
    }

    TimeString(){ }

    public String getCurrentElapsedTime(){
        return currentElapsedTime;
    }

    public String getCurrentMillis(){
        return currentMillis;
    }

    public void setCurrentElapsedTime(String elapsedTime){
        currentElapsedTime = elapsedTime;
    }

    public void setCurrentMillis(String currentMillis) {
        this.currentMillis = currentMillis;
    }
}
