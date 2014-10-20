//
//  ACTCTTDataUtils.h
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/19/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ACTCTTDBManager.h"

@interface ACTCTTDataUtils : NSObject

+ (void)clearRiderTable:(ACTCTTDBManager *)database;
+ (void)clearLapsTable:(ACTCTTDBManager *)database;

@end
