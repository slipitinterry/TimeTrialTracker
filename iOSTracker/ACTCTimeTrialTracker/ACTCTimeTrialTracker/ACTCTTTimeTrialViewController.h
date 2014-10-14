//
//  ACTCTTTimeTrialViewController.h
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/12/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ACTCTTTimeTrialViewController : UIViewController <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UILabel *timerLabel;
@property (weak, nonatomic) IBOutlet UILabel *riderNumberLabel;
@property (weak, nonatomic) IBOutlet UILabel *riderNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *riderLastSeenLabel;


@property (weak, nonatomic) IBOutlet UIButton *timerStartButton;
@property (weak, nonatomic) IBOutlet UIButton *timerResetButton;
@property (weak, nonatomic) IBOutlet UIButton *timerStopButton;


- (IBAction)timerStartAction:(UIButton *)sender;
- (IBAction)timerResetAction:(UIButton *)sender;
- (IBAction)timerStopAction:(UIButton *)sender;

@property (weak, nonatomic) IBOutlet UIButton *startRiderButton;
- (IBAction)startRiderAction:(UIButton *)sender;

@property (weak, nonatomic) IBOutlet UITableView *tableRiderInfo;
@end
