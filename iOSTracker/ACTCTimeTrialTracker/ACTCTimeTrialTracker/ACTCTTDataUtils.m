//
//  ACTCTTDataUtils.m
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/19/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import "ACTCTTDataUtils.h"\

@implementation ACTCTTDataUtils



+ (void)clearRiderTable:(ACTCTTDBManager *)database
{
    //delete it
    NSLog(@"Deleting All of the Rider Data...");
    
    // Setup the delete table query string
    NSString *dropTable = @"drop table if exists riders";
    
    // Execute the drop table
    [database executeQuery:dropTable];
    
    NSString *createTable = @"CREATE TABLE riders(riderID integer primary key, riderName text, laps number, last_seen number, eta number, avg_lap number, mean_diff number, std_dev number)";
    
    // Execute the create table
    [database executeQuery:createTable];

}

+ (void)clearLapsTable:(ACTCTTDBManager *)database
{
    //delete it
    NSLog(@"Deleting All of the Lap Data...");
    
    // Setup the delete table query string
    NSString *dropTable = @"drop table if exists laps";
    
    // Execute the drop table
    [database executeQuery:dropTable];
    
    NSString *createTable = @"CREATE TABLE laps(lapID integer primary key, riderID integer, lap_split number)";
    
    // Execute the create table
    [database executeQuery:createTable];

}


@end
