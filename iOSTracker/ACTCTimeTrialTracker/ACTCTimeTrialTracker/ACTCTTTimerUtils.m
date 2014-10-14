//
//  ACTCTTTimerUtils.m
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/12/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import "ACTCTTTimerUtils.h"

@implementation ACTCTTTimerUtils

+ (NSString *) floatToTimeString:(float)time {
    
    // Time in milliseconds
    
    long secs = (long)(time/1000);
    long mins = (long)((time/1000)/60);
    long hrs = (long)(((time/1000)/60)/60);
    //long millis = (long)(time);
    
    /** Convert the seconds to String
     * and format to ensure it has
     * a leading zero when required
     */
    NSString *secondsString;
    secs = secs % 60;
    secondsString = [NSString stringWithFormat:@"%ld", secs];
    if(secs == 0){ secondsString = @"00"; }
    if(secs <10 && secs > 0){ secondsString = [NSString stringWithFormat:@"0%ld", secs ]; }
    
    /* Convert the minutes to String
     * and format the String
     */
    NSString *minutesString;
    mins = mins % 60;
    minutesString = [NSString stringWithFormat:@"%ld", mins];
    if(mins == 0){ minutesString = @"00"; }
    if(mins <10 && mins > 0){ minutesString = [NSString stringWithFormat:@"0%ld", mins ]; }
    
    /**
     * Convert the hours to String
     * and format the String
     */
    NSString *hoursString;
    hoursString = [NSString stringWithFormat:@"%ld", hrs];
    if(hrs == 0){ hoursString = @"00"; }
    if(hrs <10 && hrs > 0){ hoursString = [NSString stringWithFormat:@"0%ld", hrs]; }
    
    /* Although we are not using milliseconds
     * on the timer in this example
     * code included in the event that it's wanted
     */
    //millis = millis % 1000;
    //String milliseconds = String.valueOf(millis);
    //if(milliseconds.length()==2){ milliseconds = "0"+milliseconds; }
    //if(milliseconds.length()<=1){ milliseconds = "00"; }
    //milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);
    
    /**
     * Setting the timer text to the elapsed time
     */
    NSLog(@"Timer: %@:%@:%@", hoursString, minutesString, secondsString);
    
    NSString *currentElapsedTime = [NSString stringWithFormat:@"%@:%@:%@", hoursString, minutesString, secondsString];
    //NSString currentMillis = "."+milliseconds;
    
    
    return currentElapsedTime;
    
}

@end
