# Simulator
UBC Solar's race simulator. An in-progress attempt to simulate a race and predict the car's performance.
It was put on hold when Raven was retired, but could be revived for a new generation of the car.

How to use this thing:

Step 1: Running it

First, you will need to make sure you have Java installed on your computer (any edition of Jave 8 should probably work).

Next, it doesn't currently have an executable, so I would recommend running it from an IDE. Either Eclipse or IntelliJ will work.
Eclipse is free for everyone, IntelliJ is better, but you need to create a student account to get it for free.

Inside the IDE, navigate to src/com/ubcsolar/Main/Main.java. From here, run the program. (Click "Run", then "Run 'Main' " if you're using IntelliJ).

Step 2: Using it

(This is kind of convoluted.)

First, open the Map window (click the "Advanced" button in the bottom-left pane of the screen).

Next, click the "Load Map" tab in the top left of the window, then "Select File" (I think the other buttons are broken??).
There are a number of premade maps in the "res" folder that can be loaded (in the program's current state, I would recommend
using the Circuit of the Americas map, as most of the others are too long to be really useful).

The program can read any map in .kml format. You can create new maps for free using Google My Maps, 
although you will have to do some extra steps to add the elevation to the .kml file.

After the map is loaded, close the map window, and load the Weather window by clicking the "Advanced" button in the Weather pane in the top left.

In this window, click the "Forecasts" tab, then "Load forecasts for Route (48 hours)", which will load the weather forecasts automatically.
(On short routes, this will just be a flat line, but on longer routes, it will grab weather forecasts for various points along the route.)

Next, we get to the unfinished components. Currently, you must do the following:

In the main window, click the Debug tab, and click the "Add Car TelemPacket" and "Add Location Report" buttons.
The TelemPacket can be left as is, while I would recommend that the location report be changed so that the coordinates are near the start point of the route.

We want the telemetry data and location to be transmitted from the car, but that functionality was never finished (either in Raven's hardware or the Simulator software).

Finally, to actually run the simulation:

Click the "Advanced" button in the Simulation pane, choose the number of laps you want, and then click "Run Simulation".
If it's successful, it will display lines showing what speed it thinks the car should run at over the course, and will
also mark those speeds on the main map. If nothing shows up, check the IDE. There is probably an error saying that it can't make it
around the track at that speed. This means that the program can't calculate any speed for the car to get around the track without running out of charge.
If that's the case, try running it over fewer laps or a shorter route.


