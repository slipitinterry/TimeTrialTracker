//
//  ACTCTTEditInfoViewController.h
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/8/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ACTCTTEditInfoViewControllerDelegate

-(void)editingInfoWasFinished;

@end

@interface ACTCTTEditInfoViewController : UIViewController <UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UITextField *riderNameText;
@property (nonatomic, strong) id<ACTCTTEditInfoViewControllerDelegate> delegate;
@property (nonatomic) int recordIDToEdit;

- (IBAction)saveRider:(id)sender;

@end
