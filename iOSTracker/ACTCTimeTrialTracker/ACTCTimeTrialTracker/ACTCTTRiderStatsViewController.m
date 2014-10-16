//
//  ACTCTTRiderStatsViewController.m
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/16/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import "ACTCTTRiderStatsViewController.h"
#import "ACTCTTDBManager.h"
#import "ACTCTTTimerUtils.h"

@interface ACTCTTRiderStatsViewController ()

@property (nonatomic, strong) ACTCTTDBManager *dbManager;
@property (nonatomic, strong) NSArray *arrRiderStatsInfo;

@end

@implementation ACTCTTRiderStatsViewController

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

    // Make self the delegate and datasource of the table view.
    self.riderStatsTableView.delegate = self;
    self.riderStatsTableView.dataSource = self;
    
    // Initialize the dbManager property.
    self.dbManager = [[ACTCTTDBManager alloc] initWithDatabaseFilename:@"timetrial.db"];
    
}

- (void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    [self loadData];
    
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void)loadData{
    // Form the query.
    NSString *query = @"select * from riders order by riderID";
    
    NSLog(@"Loading data for RiderStats List View...");
    
    // Get the results.
    if (self.arrRiderStatsInfo != nil) {
        self.arrRiderStatsInfo = nil;
    }
    self.arrRiderStatsInfo = [[NSArray alloc] initWithArray:[self.dbManager loadDataFromDB:query]];
    
    // Reload the table view.
    [self.riderStatsTableView reloadData];
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.arrRiderStatsInfo.count;
}


-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 100.0;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    NSLog(@"Entering ACTCTTRiderStatsViewController.m cellForRowAtIndexPath");

    // Dequeue the cell.
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"statsCell" forIndexPath:indexPath];

    NSInteger indexOfRidername = [self.dbManager.arrColumnNames indexOfObject:@"riderName"];
    NSInteger indexOfRiderID = [self.dbManager.arrColumnNames indexOfObject:@"riderID"];
    NSInteger indexOfRiderAvg = [self.dbManager.arrColumnNames indexOfObject:@"avg_lap"];
    NSInteger indexOfRiderMeanDiff = [self.dbManager.arrColumnNames indexOfObject:@"mean_diff"];
    NSInteger indexOfRiderStdDev = [self.dbManager.arrColumnNames indexOfObject:@"std_dev"];

    // Set the loaded data to the appropriate cell labels.
    UILabel *riderNum = (UILabel*)[cell viewWithTag:1];
    riderNum.text = [NSString stringWithFormat:@"%@", [[self.arrRiderStatsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderID]];
    NSLog(@"Rider Number Set into Cell...");
    
    UILabel *riderName = (UILabel*)[cell viewWithTag:2];
    riderName.text = [NSString stringWithFormat:@"%@", [[self.arrRiderStatsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRidername]];
    NSLog(@"Rider Name Set into Cell...");


    UILabel *riderAvgLap = (UILabel*)[cell viewWithTag:3];
    NSString *strAvg =  [[self.arrRiderStatsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderAvg];
    float fAvg = [strAvg floatValue];
    NSString *avgTimeString = [ACTCTTTimerUtils floatToTimeString:fAvg];
    riderAvgLap.text = [NSString stringWithFormat:@"Avg Lap: %@", avgTimeString];
    NSLog(@"Rider Average Lap Set into Cell...");

    UILabel *riderMeanDiff = (UILabel*)[cell viewWithTag:4];
    NSString *strMeanDiff =  [[self.arrRiderStatsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderMeanDiff];
    riderMeanDiff.text = [NSString stringWithFormat:@"Mean Diff: %@", strMeanDiff];
    NSLog(@"Rider Mean Difference Set into Cell...");

    UILabel *riderStdDev = (UILabel*)[cell viewWithTag:5];
    NSString *strStdDev =  [[self.arrRiderStatsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderStdDev];
    riderStdDev.text = [NSString stringWithFormat:@"Std Dev: %@", strStdDev];
    NSLog(@"Rider Std Deviation Set into Cell...");

    
    return cell;
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
