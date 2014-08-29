package com.ridgway.timetrialtracker;

/**
 * Created by ridgway on 8/29/14.
 */
public class Utils {

    public static TimeString floatToTimeString(float time){
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
        String currentElapsedTime = hours + ":" + minutes + ":" + seconds;
        String currentMillis = "."+milliseconds;

        TimeString timeString = new TimeString(currentElapsedTime, currentMillis);
        return timeString;

    }
}
