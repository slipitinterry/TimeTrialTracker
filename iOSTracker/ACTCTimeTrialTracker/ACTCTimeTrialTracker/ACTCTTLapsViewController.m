//
//  ACTCTTLapsViewController.m
//  ACTCTimeTrialTracker
//
//  Created by ridgway on 10/16/14.
//  Copyright (c) 2014 Ridgway Coders. All rights reserved.
//

#import "ACTCTTLapsViewController.h"
#import "ACTCTTDBManager.h"
#import "ACTCTTTimerUtils.h"

@interface ACTCTTLapsViewController ()

@property (nonatomic, strong) ACTCTTDBManager *dbManager;
@property (nonatomic, strong) NSArray *arrLapsInfo;

@end

@implementation ACTCTTLapsViewController

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
    self.lapsTableView.delegate = self;
    self.lapsTableView.dataSource = self;
    
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
    NSString *query = @"select riders.riderID, riders.riderName, laps.lap_split, laps.riderID from laps, riders where riders.riderID = laps.riderID order by riders.riderID";
    
    NSLog(@"Loading data for Lap Info List View...");
    
    // Get the results.
    if (self.arrLapsInfo != nil) {
        self.arrLapsInfo = nil;
    }
    self.arrLapsInfo = [[NSArray alloc] initWithArray:[self.dbManager loadDataFromDB:query]];
    
    // Reload the table view.
    [self.lapsTableView reloadData];
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.arrLapsInfo.count;
}


-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 35.0;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    NSLog(@"Entering ACTCTTLapsViewController.m cellForRowAtIndexPath");

    // Dequeue the cell.
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"lapCell" forIndexPath:indexPath];
    
    NSInteger indexOfRidername = [self.dbManager.arrColumnNames indexOfObject:@"riderName"];
    NSInteger indexOfRiderID = [self.dbManager.arrColumnNames indexOfObject:@"riderID"];
    NSInteger indexOfRiderLapSplit = [self.dbManager.arrColumnNames indexOfObject:@"lap_split"];

    // Set the loaded data to the appropriate cell labels.
    UILabel *riderNum = (UILabel*)[cell viewWithTag:1];
    riderNum.text = [NSString stringWithFormat:@"%@", [[self.arrLapsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderID]];
    NSLog(@"Rider Number Set into Cell...");
    
    UILabel *riderName = (UILabel*)[cell viewWithTag:2];
    riderName.text = [NSString stringWithFormat:@"%@", [[self.arrLapsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRidername]];
    NSLog(@"Rider Name Set into Cell...");
    
    UILabel *riderSplit = (UILabel*)[cell viewWithTag:3];
    NSString *strSplit =  [[self.arrLapsInfo objectAtIndex:indexPath.row] objectAtIndex:indexOfRiderLapSplit];
    float fSplit = [strSplit floatValue];
    NSString *splitTimeString = [ACTCTTTimerUtils floatToTimeString:fSplit];
    riderSplit.text = [NSString stringWithFormat:@"%@", splitTimeString];
    NSLog(@"Rider LapSplit Set into Cell...");
 
    
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
