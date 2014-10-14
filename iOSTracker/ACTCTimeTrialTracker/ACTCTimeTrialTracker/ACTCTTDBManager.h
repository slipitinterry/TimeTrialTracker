//
//  ACTCTTDBManager.h
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/8/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ACTCTTDBManager : NSObject

@property (nonatomic, strong) NSMutableArray *arrColumnNames;

@property (nonatomic) int affectedRows;

@property (nonatomic) long long lastInsertedRowID;


-(instancetype)initWithDatabaseFilename:(NSString *)dbFilename;

-(NSArray *)loadDataFromDB:(NSString *)query;

-(void)executeQuery:(NSString *)query;

@end
