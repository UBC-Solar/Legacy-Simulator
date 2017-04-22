#currently, script finds info of most recent sim, parses the data and stores it in a list as a tuple
#TODO: find a way to poll for data from the car and compare

#Things to think about:
#getting the data directly through XBee library vs waiting for data from the CSV database
#speed up data retriving time (list should be O(1) access if using index, but searching is still O(n), and theres
#around 900 entries for short routes. Current ideas include using the last retreived index as the starting index
#for next search since car moves forward.) EDIT: searching is not that slow if you do not print everything
#find an integer to use as key, which makes searching/comparing much faster (might be very difficult,
#need to find something in common between sim data and car. Currently, we are using location (lat, long),
#which are both doubles. Searching would be required instead of O(1) index access,
#margin of errors between sim location and car location are needed, which makes searching even slower, and errors
#may occur (wrong sim frame fetched due to double rounding error)) EDIT: its actually not that slow if you don't
#print everything but the errors might still be an issue
import os
import sys

for i in range(4):
    os.chdir("..");

#csv indices 
TIME_STAMP = 6;
STATE_OF_CHARGE = 9;
LATITUDE = 62;
LONGITUDE= 63;
#error allowed when comparing doubles
MARGIN_OF_ERROR = 0.0000001;

#list to hold all the sim data
sim_data = [];

#get the file name of most recent sim
dir = "Output/" + str(max(os.listdir("Output/"))) + "/simulations/";
file = dir + str(max(os.listdir(dir)));

#fill list with sim data
for line in open(file, "r"):
    if len(line.split(',')) > 1:
        #testing
        #print(line.split(',')[TIME_STAMP], end = "\t");
        #print(line.split(',')[STATE_OF_CHARGE], end = "\t");
        #print(line.split(',')[LATITUDE], end = "\t");
        #print(line.split(',')[LONGITUDE]);
        sim_data.append((line.split(',')[TIME_STAMP], line.split(',')[STATE_OF_CHARGE],\
                         line.split(',')[LATITUDE], line.split(',')[LONGITUDE],));

print("FINISH READING FILE");
print(sim_data);

#testing how fast searching is
def search(lat, long):
    for (a,b,c,d,) in sim_data:
        try:    
            if abs(float(lat) - float(c)) < MARGIN_OF_ERROR and abs(float(long) - float(d)) < MARGIN_OF_ERROR:
                return (a,b,c,d,);
                
        except ValueError:
                continue;

while (True):
    lat = input();
    long = input();
    print(search(lat, long));


#testing
'''
print(sim_data);

for (a, b, c, d,) in sim_data:
    print(a + '\t' + b + '\t' + c + '\t' + d);
'''
