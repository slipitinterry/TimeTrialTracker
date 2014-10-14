//
//  ACTCTTViewController.h
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 9/18/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ACTCTTEditInfoViewController.h"

@interface ACTCTTViewController : UIViewController <UITableViewDelegate, UITableViewDataSource, ACTCTTEditInfoViewControllerDelegate>

- (IBAction)addRider:(id)sender;

@property (weak, nonatomic) IBOutlet UITableView *tableRiders;

@end
