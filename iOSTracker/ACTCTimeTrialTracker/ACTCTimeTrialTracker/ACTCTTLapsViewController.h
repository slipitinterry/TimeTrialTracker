//
//  ACTCTTLapsViewController.h
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/16/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ACTCTTLapsViewController : UIViewController <UITableViewDelegate, UITableViewDataSource,
                                UITabBarControllerDelegate>

@property (weak, nonatomic) IBOutlet UITableView *lapsTableView;


@property (weak, nonatomic) IBOutlet UIBarButtonItem *shareDataButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *deleteDataButton;

@end
