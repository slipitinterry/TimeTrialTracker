//
//  ACTCTTEditInfoViewController.m
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/8/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import "ACTCTTEditInfoViewController.h"
#import "ACTCTTDBManager.h"


@interface ACTCTTEditInfoViewController ()

@property (nonatomic, strong) ACTCTTDBManager *dbManager;

-(void)loadInfoToEdit;

@end



@implementation ACTCTTEditInfoViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // Set the navigation bar tint color.
    self.navigationController.navigationBar.tintColor = self.navigationItem.rightBarButtonItem.tintColor;

    // Make self the delegate of the textfields.
    self.riderNameText.delegate = self;
    
    // Initialize the dbManager object.
    self.dbManager = [[ACTCTTDBManager alloc] initWithDatabaseFilename:@"timetrial.db"];

    // Check if should load specific record for editing.
    if (self.recordIDToEdit != -1) {
        // Load the record with the specific ID from the database.
        [self loadInfoToEdit];
    }

}

-(BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    return YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)saveRider:(id)sender {
    // Prepare the query string.
    // If the recordIDToEdit property has value other than -1, then create an update query. Otherwise create an insert query.
    NSString *query;
    if (self.recordIDToEdit == -1) {
        query = [NSString stringWithFormat:@"insert into riders values(null, '%@', 0, 0, 0, 0, 0, 0)", self.riderNameText.text];
    }
    else{
        query = [NSString stringWithFormat:@"update riders set riderName='%@' where riderID=%d",
                 self.riderNameText.text, self.recordIDToEdit];
    }
    
    // Execute the query.
    [self.dbManager executeQuery:query];
    
    // If the query was successfully executed then pop the view controller.
    if (self.dbManager.affectedRows != 0) {
        NSLog(@"Query was executed successfully. Affected rows = %d", self.dbManager.affectedRows);

        // Inform the delegate that the editing was finished.
        [self.delegate editingInfoWasFinished];

        // Pop the view controller.
        [self.navigationController popViewControllerAnimated:YES];
    }
    else{
        NSLog(@"Could not execute the query.");
    }
}

-(void)loadInfoToEdit{
    // Create the query.
    NSString *query = [NSString stringWithFormat:@"select * from riders where riderID=%d", self.recordIDToEdit];
    
    // Load the relevant data.
    NSArray *results = [[NSArray alloc] initWithArray:[self.dbManager loadDataFromDB:query]];
    
    // Set the loaded data to the textfields.
    self.riderNameText.text = [[results objectAtIndex:0] objectAtIndex:[self.dbManager.arrColumnNames indexOfObject:@"riderName"]];

}

@end
