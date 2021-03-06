//
//  ACTCTTTimeTrialViewController.m
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/12/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import "ACTCTTTimeTrialViewController.h"
#import "ACTCTTTimerUtils.h"
#import "ACTCTTDBManager.h"


@interface ACTCTTTimeTrialViewController ()

@property BOOL bStopped;
@property (getter = isRunning) BOOL bRunning;

@property BOOL disableScreenSleep;
@property BOOL hideRidersAfterLastLap;
@property NSInteger maxLaps;

@property NSTimeInterval elapsedTime;
@property NSTimeInterval currentInterval;

@property (nonatomic, strong) ACTCTTDBManager *dbManager;
@property (nonatomic, strong) ACTCTTDBManager *dbManagerForLapQuery;
@property (nonatomic, strong) NSArray *arrRiderInfo;
@property (nonatomic) int recordSelected;

@property (nonatomic) NSString *selectedRiderID;
@property (nonatomic) NSString *selectedRiderName;
@property (nonatomic, getter = isRiderSelected) BOOL riderSelected;

@property (weak) NSTimer *repeatingTimer;

- (NSDictionary *)userInfo;
- (void)targetMethod:(NSTimer*)theTimer;
- (NSInteger)queryNumberOfLapsForRider:(NSString*)riderID;
- (void)updateAverageLapAndETAForRider:(NSString*)riderID;


@end

@implementation ACTCTTTimeTrialViewController

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

    // Get our user preference values
    NSUserDefaults *defaultPrefs = [NSUserDefaults standardUserDefaults];
    self.disableScreenSleep = [defaultPrefs boolForKey:@"disableScreenSleep"];
    self.hideRidersAfterLastLap = [defaultPrefs boolForKey:@"hideRidersAfterLastLap"];
    self.maxLaps = [defaultPrefs integerForKey:@"numLaps"];

    
    // Make self the delegate and datasource of the table view.
    self.tableRiderInfo.delegate = self;
    self.tableRiderInfo.dataSource = self;
    id tabController = [self parentViewController];
    if( [tabController isKindOfClass:[UITabBarController class]] )
    {
        UITabBarController *tab = tabController;
        tab.delegate = self;
    }

    // Initialize the dbManager property.
    self.dbManager = [[ACTCTTDBManager alloc] initWithDatabaseFilename:@"timetrial.db"];
    self.dbManagerForLapQuery = [[ACTCTTDBManager alloc] initWithDatabaseFilename:@"timetrial.db"];
    
    //Setup the Buttons
    [self enableButtons:NO];
    self.bStopped = NO;

}

- (void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    [self loadData];
    
    
}

-(void)loadData{
    // Form the query.
    NSString *select = @"select * from riders";
    
    NSString *where = @"";
    if(self.hideRidersAfterLastLap){
        where = [NSString stringWithFormat:@" where laps < '%ld'", (long)self.maxLaps];
    }
    
    NSString *query = [NSString stringWithFormat:@"%@ %@ order by eta, last_seen, riderID", select, where];
    
    NSLog(@"Loading data for TimeTrial List View...");
    NSLog(@"Using Query: %@", query);
    
    // Get the results.
    if (self.arrRiderInfo != nil) {
        self.arrRiderInfo = nil;
    }
    self.arrRiderInfo = [[NSArray alloc] initWithArray:[self.dbManager loadDataFromDB:query]];
    
    // Reload the table view.
    [self.tableRiderInfo reloadData];
    
    self.riderSelected = NO;
}

- (BOOL)tabBarController:(UITabBarController *)aTabBar
                    shouldSelectViewController:(UIViewController *)viewController
{
    NSLog(@"TabBarController:shouldSelectViewController");
    if ([self isRunning] && (viewController != [aTabBar.viewControllers objectAtIndex:0]) )
    {
        // Disable all but the first tab.
        NSLog(@"Disable TabBar Tabs.");
        return NO;
    }
    
    return YES;
}


-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.arrRiderInfo.count;
}


-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 90.0;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{

    NSLog(@"Entering ACTCTTTimeTrialViewController.m cellForRowAtIndexPath");
    [self logRiderInfo];
    
    // Dequeue the cell.
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"riderCell" forIndexPath:indexPath];
    
    NSInteger indexOfRidername = [self.dbManager.arrColumnNames indexOfObject:@"riderName"];
    NSInteger indexOfRiderID = [self.dbManager.arrColumnNames indexOfObject:@"riderID"];
    NSInteger indexOfRiderLaps = [self.dbManager.arrColumnNames indexOfObject:@"laps"];
    NSInteger indexOfRiderLastSeen = [self.dbManager.arrColumnNames indexOfObject:@"last_seen"];
    NSInteger indexOfRiderETA = [self.dbManager.arrColumnNames indexOfObject:@"eta"];
    
    NSLog(@"Column Indexes Retrieved...");
    
    // Set the loaded data to the appropriate cell labels.
    UILabel *riderNum = (UILabel*)[cell viewWithTag:1];
    riderNum.text = [NSString stringWithFormat:@"%@", [[self.arrRiderInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderID]];
    NSLog(@"Rider Number Set into Cell...");

    UILabel *riderName = (UILabel*)[cell viewWithTag:2];
    riderName.text = [NSString stringWithFormat:@"%@", [[self.arrRiderInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRidername]];
    NSLog(@"Rider Name Set into Cell...");

    UILabel *riderLaps = (UILabel*)[cell viewWithTag:3];
    riderLaps.text = [NSString stringWithFormat:@"Laps: %@", [[self.arrRiderInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderLaps]];
    NSLog(@"Rider Laps Set into Cell...");

    UILabel *riderLastSeen = (UILabel*)[cell viewWithTag:4];
    NSString *seen = [[self.arrRiderInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderLastSeen];
    float fSeen = [seen floatValue];
    NSString *seenTimeString = [ACTCTTTimerUtils floatToTimeString:fSeen];
    riderLastSeen.text = [NSString stringWithFormat:@"Last Seen: %@", seenTimeString];
    NSLog(@"Rider LastSeen Set into Cell...");


    UILabel *riderETA = (UILabel*)[cell viewWithTag:5];
    NSString *strETA =  [[self.arrRiderInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderETA];
    float fETA = [strETA floatValue];
    NSString *etaTimeString = [ACTCTTTimerUtils floatToTimeString:fETA];
    riderETA.text = [NSString stringWithFormat:@"ETA: %@", etaTimeString];
    NSLog(@"Rider ETA Set into Cell...");

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSLog(@"TableIndexPath: %@", indexPath);
    NSInteger indexOfRidername = [self.dbManager.arrColumnNames indexOfObject:@"riderName"];
    NSLog(@"RiderNameFieldIndex: %ld", (long)indexOfRidername);
    self.selectedRiderName = [[self.arrRiderInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRidername];

    NSInteger indexOfRiderID = [self.dbManager.arrColumnNames indexOfObject:@"riderID"];
    NSLog(@"RiderIDFieldIndex: %ld", (long)indexOfRiderID);
    self.selectedRiderID = [[self.arrRiderInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderID];

    NSString *riderID = [NSString stringWithFormat:@"%@", self.selectedRiderID];
    NSNumber *selectedRiderLaps = [NSNumber numberWithLong:[self queryNumberOfLapsForRider:riderID]];
    NSLog(@"Rider Laps: %@", selectedRiderLaps);
    // If we have already started a rider, they will have a non-zero lap count
    // So, change the button text to Checkin.
    if([selectedRiderLaps intValue] < 1){
        [self.startRiderButton setTitle:@"Start Rider" forState:UIControlStateNormal];
    }
    else {
        [self.startRiderButton setTitle:@"Checkin Rider" forState:UIControlStateNormal];
    }
    
    NSLog (@"Rider selected: %@", self.selectedRiderName);
    NSLog (@"Rider laps: %@", selectedRiderLaps);
    
    self.riderSelected = YES;
    [self enableButtons:self.isRunning];
    
}

- (void)logRiderInfo
{
    for(NSArray *rider in self.arrRiderInfo){
        for(NSString *column in rider){
            NSLog(@"Rider Info: %@", column);
        }
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSDictionary *)userInfo {
    NSDictionary *userInfoDict;
    if (self.bStopped){
        NSDate *startDate = [NSDate date];
        NSLog(@"Previous StartTime: %@", startDate);
        NSLog(@"Elapsed Time: %f", self.elapsedTime);
        startDate = [startDate dateByAddingTimeInterval:self.elapsedTime];

        NSLog(@"New StartTime: %@", startDate);
        userInfoDict = @{ @"StartDate" : startDate };
    }
    else {
        userInfoDict = @{ @"StartDate" : [NSDate date] };
    }
    return userInfoDict;
}

- (void)targetMethod:(NSTimer*)theTimer {
    NSDate *startDate = [[theTimer userInfo] objectForKey:@"StartDate"];
    NSLog(@"Timer started on %@", startDate);
    
    NSDate *currentDate = [NSDate date];
    NSLog(@"Current Date %@", currentDate);
    
    NSTimeInterval dateTime = [startDate timeIntervalSinceNow];
    self.elapsedTime = dateTime;
    NSLog(@"Time Difference %f", dateTime);
    
    float absDateTime = fabsf(dateTime * 1000);
    NSString *currentTimeString = [ACTCTTTimerUtils floatToTimeString:absDateTime];

    NSLog(@"Timer %@", currentTimeString);

    self.timerLabel.text = currentTimeString;
    self.currentInterval = absDateTime;
    
}


- (IBAction)timerStartAction:(UIButton *)sender {
    
    
    // Create the timer and start it
    // Cancel a preexisting timer.
    [self.repeatingTimer invalidate];
    
    NSTimer *timer = [NSTimer
                      scheduledTimerWithTimeInterval:0.5
                      target:self
                      selector:@selector(targetMethod:)
                      userInfo:[self userInfo]
                      repeats:YES];
    self.repeatingTimer = timer;

    [self enableButtons:YES];
    
    if(self.disableScreenSleep){
        [UIApplication sharedApplication].idleTimerDisabled = YES;
    }

}

- (IBAction)timerResetAction:(UIButton *)sender {
    self.bStopped = NO;
    self.timerLabel.text = @"00:00:00";
    
}

- (IBAction)timerStopAction:(UIButton *)sender {
    
    [self.repeatingTimer invalidate];
    self.repeatingTimer = nil;
    
    self.bStopped = YES;
    [self enableButtons:NO];
    
    if(self.disableScreenSleep){
        [UIApplication sharedApplication].idleTimerDisabled = NO;
    }

}

- (void) enableButtons:(bool)start {
    
    // start is YES when coming from the Start Timer, and NO from the Stop Timer
    NSLog(@"Enable Buttons %d", start);
    
    // Set the Running flag, so we can disable
    // all of the other tabs and the user can't move away
    // from the main tab while the time trial is in progress.
    self.bRunning = start;
    
    // Disable/Enable Hide/Show the Start, Stop, & Reset Buttons
    self.timerStartButton.enabled = !start;
    self.timerStartButton.hidden = start;
    
    self.timerResetButton.enabled = !start;
    self.timerResetButton.hidden = start;
    
    self.timerStopButton.enabled = start;
    self.timerStopButton.hidden = !start;
    
    self.startRiderButton.enabled = start && self.isRiderSelected;

}


- (IBAction)startRiderAction:(UIButton *)sender {
    
    NSString *lastSeenTime = @"00:00:00";
    
    // Now get Values from Timer and List View Selected Cell
    lastSeenTime = self.timerLabel.text;
    
    
    self.riderNumberLabel.text = self.selectedRiderID;
    self.riderNameLabel.text = [NSString stringWithFormat:@"Last Rider: %@", self.selectedRiderName];
    self.riderLastSeenLabel.text = [NSString stringWithFormat:@"Last Seen @: %@", lastSeenTime ];
    
    [self saveLapInfo];
}

- (void)saveLapInfo {
    // Prepare the query string.
    // If the recordIDToEdit property has value other than -1, then create an update query. Otherwise create an insert query.
    NSLog(@"Save Lap Info...");
    
    // Save the current time, so we use the same value for all queries
    NSTimeInterval lapTime = self.currentInterval;
    
    NSString *query = [NSString stringWithFormat:@"insert into laps values(null, '%@', '%f')", self.riderNumberLabel.text, lapTime];
    
    // Execute the lap insert.
    [self.dbManager executeQuery:query];
    
    // If the query was successfully executed then pop the view controller.
    if (self.dbManager.affectedRows != 0) {
        NSLog(@"Insert Lap Query was executed successfully. Affected rows = %d", self.dbManager.affectedRows);
    }
    else{
        NSLog(@"Could not execute the Insert Lap query.");
    }
    
    // Now we've updated the laps for this rider, get the total laps
    // and use that to update the rider record, along with the current time.
    NSInteger numLaps = [self queryNumberOfLapsForRider:self.selectedRiderID] - 1; // lap data has start time, too.
    [self updateAverageLapAndETAForRider:self.selectedRiderID];
    NSString *updateRiderLastSeen = [NSString stringWithFormat:@"update riders set last_seen='%f', laps='%ld' where riderID=%@", lapTime, (long)numLaps, self.selectedRiderID];

    // Execute the rider update.
    [self.dbManager executeQuery:updateRiderLastSeen];
    
    // If the query was successfully executed then pop the view controller.
    if (self.dbManager.affectedRows != 0) {
        NSLog(@"Rider Last Seen was executed successfully. Affected rows = %d", self.dbManager.affectedRows);
        
        // reload the data for the list view
        [self loadData];
    }
    else{
        NSLog(@"Could not execute the Update Last Seen query.");
    }
}

- (NSInteger)queryNumberOfLapsForRider:(NSString*)riderID{
    // Form the query.
    NSString *query = [NSString stringWithFormat:@"select * from laps where riderID='%@'", riderID];
    
    NSLog(@"Loading Lap Count for Rider: %@", riderID);
    
    // Get the results.
    NSArray *arrLapsForRider = [[NSArray alloc] initWithArray:[self.dbManagerForLapQuery loadDataFromDB:query]];

    return [arrLapsForRider count];
    
}


- (void)updateAverageLapAndETAForRider:(NSString*)riderID{
    
    NSDecimalNumber *riderETA;
    NSDecimalNumber *riderAvgLap;
    
    // Form the query.
    NSString *query = [NSString stringWithFormat:@"select * from laps where riderID='%@'", riderID];
    
    NSLog(@"Loading Laps for Rider: %@", riderID);
    
    // Get the results.
    NSArray *arrLapsForRider = [[NSArray alloc] initWithArray:[self.dbManager loadDataFromDB:query]];
    NSInteger lapCount = [arrLapsForRider count];
    
    if( lapCount > 1 ){
        NSMutableArray *arrLapSplits = [[NSMutableArray alloc]init];

        // First calculate all the lap splits, but finding the difference between successive lap
        // entries in the lap table for this rider.
        NSInteger indexOfLapTime = [self.dbManager.arrColumnNames indexOfObject:@"lap_split"];
        NSNumber *fLap = (NSNumber *)[[arrLapsForRider firstObject] objectAtIndex:indexOfLapTime];
        NSDecimalNumber *previous = [NSDecimalNumber decimalNumberWithDecimal:[fLap decimalValue]];
        for( int lap = 1; lap < lapCount; lap++ ){

            NSNumber *fLapNext = (NSNumber *)[[arrLapsForRider objectAtIndex:lap] objectAtIndex:indexOfLapTime];
            NSDecimalNumber *next = [NSDecimalNumber decimalNumberWithDecimal:[fLapNext decimalValue]];
                                     
            NSDecimalNumber *lapSplit = [self abs:[next decimalNumberBySubtracting:previous]];
            
            [arrLapSplits addObject:lapSplit];
            NSLog(@"Lap Split: %@", lapSplit);
            previous = next;
        }
        
        NSNumber *lapSplitCount = [NSNumber numberWithInteger:[arrLapSplits count]];
        NSDecimalNumber *dnLapCount = [NSDecimalNumber decimalNumberWithDecimal:[lapSplitCount decimalValue]];

        NSNumber *fLapTime = (NSNumber *) [arrLapSplits firstObject];
        NSDecimalNumber *averageLapTime = [NSDecimalNumber decimalNumberWithDecimal:[fLapTime decimalValue]];
        for( int lapIndex = 1; lapIndex < [lapSplitCount intValue]; lapIndex++){
            NSNumber *fNextLapTime = (NSNumber *)[arrLapSplits  objectAtIndex:lapIndex];
            NSDecimalNumber *nextLapTime = [NSDecimalNumber decimalNumberWithDecimal:[fNextLapTime decimalValue]];
            averageLapTime = [averageLapTime decimalNumberByAdding:nextLapTime];
        }
        
        riderAvgLap = [averageLapTime decimalNumberByDividingBy:dnLapCount];
        NSLog(@"Total Laps: %@", lapSplitCount);
        NSLog(@"Average Lap Interval: %@", riderAvgLap);
        
        NSNumber *mostRecentLapTime = [[arrLapsForRider objectAtIndex:(lapCount - 1)] objectAtIndex:indexOfLapTime];
        NSDecimalNumber *mRLT = [NSDecimalNumber decimalNumberWithDecimal:[mostRecentLapTime decimalValue]];
        riderETA = [mRLT decimalNumberByAdding:riderAvgLap];
        NSLog(@"Rider ETA: %@", riderETA);
 
        
        // Now run the database update

        NSString *updateRiderLastSeen = [NSString stringWithFormat:@"update riders set eta='%@', avg_lap='%@' where riderID=%@", riderETA, riderAvgLap, self.selectedRiderID];
        
        // Execute the rider update.
        [self.dbManager executeQuery:updateRiderLastSeen];
        
        // If the query was successfully executed then pop the view controller.
        if (self.dbManager.affectedRows != 0) {
            NSLog(@"Rider ETA & Average Lap values were updated successfully. Affected rows = %d", self.dbManager.affectedRows);
        }
        else{
            NSLog(@"Could not execute the Update Rider ETA & Average Lap query.");
        }
    }
    else
    {
        NSLog(@"Not enough laps to calculate Average and ETA.");
    }

}

- (NSDecimalNumber *)abs:(NSDecimalNumber *)num {
    if ([num compare:[NSDecimalNumber zero]] == NSOrderedAscending) {
        // Number is negative. Multiply by -1
        NSDecimalNumber * negativeOne = [NSDecimalNumber decimalNumberWithMantissa:1
                                                                          exponent:0
                                                                        isNegative:YES];
        return [num decimalNumberByMultiplyingBy:negativeOne];
    } else {
        return num;
    }
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


@end
