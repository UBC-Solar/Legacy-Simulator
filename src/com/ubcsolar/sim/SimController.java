/**
 * the interface for the UI and the sim. 
 * Can change settings and run new sims, and see the result of the past 
 * ones. 
 */

package com.ubcsolar.sim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;

import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.SimulationReport;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.exception.NoCarStatusException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;
import com.ubcsolar.exception.NotEnoughChargeException;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.NewSimulationReportNotification;
import com.ubcsolar.notification.Notification;

public class SimController extends ModuleController {

	private ForecastReport currentForecastReport;

	public SimController(GlobalController toAdd) {
		super(toAdd);
	}

	/**
	 * 
	 * @param requestedSpeeds
	 *            a map of geoCoordinates and requested speeds for the frames
	 *            ending at those geocoordinates. Used to override the sim's
	 *            best-guess. NOTE: not guaranteed that the speed will be
	 *            achieved; may be limited due to acceleration, power, etc.
	 * @param laps
	 *            - number of laps for the car to complete. Must be >=1 (1
	 *            implies finishing the existing lap).
	 * @throws NoForecastReportException
	 * @throws NoLoadedRouteException
	 * @throws NoLocationReportedException
	 * @throws NoCarStatusException
	 * @throws IllegalArgumentException
	 *             - if laps <= 0.
	 */
	public void runSimulation(Map<GeoCoord, Map<Integer, Double>> requestedSpeeds, int laps)
			throws NoForecastReportException, NoLoadedRouteException, NoLocationReportedException,
			NoCarStatusException {
		if (laps <= 0) {
			throw new IllegalArgumentException("Number of Laps too low, must go at least 1 lap");
		}
		// Compile all the information we need.
		// ForecastReport simmedForecastReport =
		// this.mySession.getMyWeatherController().getSimmedForecastForEveryPointForLoadedRoute();
		LocationReport lastReported = this.mySession.getMapController().getLastReportedLocation();
		if (lastReported == null) {
			throw new NoLocationReportedException();
		}
		Route routeToTraverse = this.mySession.getMapController().getAllPoints();

		if (routeToTraverse == null) {
			throw new NoLoadedRouteException();
		}
		TelemDataPacket lastCarReported = this.mySession.getMyCarController().getLastTelemDataPacket();
		if (lastCarReported == null) {
			throw new NoCarStatusException();
		}
		
		//TODO: restore block comment below after testing
		/*double startTimeNanos = System.nanoTime();
		//run the sim! 
		GeoCoord startPoint = this.mySession.getMapController().findClosestPointOnRoute(lastReported.getLocation());
		
		int targetIndex = -1;
		for(int i=0; i<routeToTraverse.getTrailMarkers().size(); i++){ //TODO , in the findClosestPointOnRoute, u find the index on route. can't u use it here as well? instead of finding it again!!!!!
			if(routeToTraverse.getTrailMarkers().get(i).equals(startPoint)){
				targetIndex = i;
			}
		}
		int targetIndex= this.mySession.getMapController().getClosestPointIndex();
		
		if(targetIndex == -1){
			throw new IllegalArgumentException();
		}
		if(targetIndex >= routeToTraverse.getTrailMarkers().size()-1){
			this.mySession.sendNotification(new ExceptionNotification(new IndexOutOfBoundsException(), "ERROR: Simulation started at last point"));
			//can still let it go through and calculate an empty simulation.
		}
		List<SimFrame> simFrames = new SimEngine().runSimulation(routeToTraverse, targetIndex, simmedForecastReport, lastCarReported, requestedSpeeds,laps);
		
		double endTimeNanos = System.nanoTime();
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Sim completed in " + ((endTimeNanos -startTimeNanos)/1000000) + "ms");*/
		
		//TODO: comment out testing between arrows:
		//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
/*		List<GeoCoord> trailMarkers = routeToTraverse.getTrailMarkers();
		GeoCoord startLoc = trailMarkers.get(0);
		int numPoints = trailMarkers.size();
		GeoCoord endLoc = trailMarkers.get(numPoints - 1);

		// double testSpeed = 50.0;
		Map<GeoCoord, Double> speedProfile = getSpeedReport().getSpeedProfile();
		Map<GeoCoord, Map<Integer, Double>> testRequestedSpeeds = new HashMap<GeoCoord, Map<Integer, Double>>();
		Map<Integer, Double> testLap = new HashMap<Integer, Double>();
		// testLap.put(1, testSpeed);
		for (int i = 0; i < numPoints; i++) {
			// speedProfile.put(trailMarkers.get(i), testSpeed);
			testRequestedSpeeds.put(trailMarkers.get(i), testLap);
		}
		long startTime = System.currentTimeMillis();

		SimResult results = new SimResult(new ArrayList<SimFrame>(), 10, lastCarReported);
		TreeMap<Integer, ForecastIO> inflectionPoints = mySession.getMyWeatherController()
				.findInflectionPoints(routeToTraverse, currentForecastReport.getForecasts());
		try {
			results = new SimEngine().runSimV2(routeToTraverse, startLoc, endLoc, currentForecastReport,
					lastCarReported, speedProfile, startTime, 1, 10, inflectionPoints);
		} catch (NotEnoughChargeException e) {
			System.out.println("Too low charge");
			System.err.println(e.getMessage());
			// e.printStackTrace();
		}
		List<SimFrame> simFrames = results.getListOfFrames();*/
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		
		
		SpeedReport results = getSpeedReport();
		Map<GeoCoord, Map<Integer,Double>> speedProfile = new HashMap<GeoCoord, Map<Integer,Double>>();
		for(GeoCoord k : results.getSpeedProfile().keySet()){
			Map<Integer,Double> lapSpeed = new HashMap<Integer,Double>();
			lapSpeed.put(1, results.getSpeedProfile().get(k));
			speedProfile.put(k, lapSpeed);
		}
		SimulationReport toSend = new SimulationReport(results.getSpeedResult().getListOfFrames(),speedProfile, "some info");

		this.mySession.sendNotification(new NewSimulationReportNotification(toSend));
	}

	/**
	 * this is where the class receives any notifications it registered for. the
	 * "shoulder tap"
	 */
	@Override
	public void notify(Notification n) {
		// handle any notifications that were registered for here
		if (n.getClass() == NewMapLoadedNotification.class) {
			SimulationReport toSend = new SimulationReport(new ArrayList<SimFrame>(),
					new HashMap<GeoCoord, Map<Integer, Double>>(), "Deleted");
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
					"Deleted last run Sim because new route loaded");
			this.mySession.sendNotification(new NewSimulationReportNotification(toSend));
		}
		if (n.getClass() == NewForecastReport.class) {
			NewForecastReport n2 = (NewForecastReport) n;
			currentForecastReport = n2.getTheReport();
		}
	}

	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	public void register() {
		mySession.register(this, NewForecastReport.class);
		this.mySession.register(this, NewMapLoadedNotification.class);

	}

	/**
	 * Method runs the simulation on the whole route and returns the Speed
	 * Report of the whole run
	 * 
	 * @return: Speed Report of the whole run
	 * @throws NoForecastReportException
	 * @throws NoLoadedRouteException
	 * @throws NoLocationReportedException
	 * @throws NoCarStatusException
	 */
	public SpeedReport getSpeedReport() throws NoForecastReportException, NoLoadedRouteException,
			NoLocationReportedException, NoCarStatusException {
		// things needed for simV2
		ForecastReport simmedForecastReport = this.mySession.getMyWeatherController()
				.getSimmedForecastForEveryPointForLoadedRoute();
		Route routeToTraverse = this.mySession.getMapController().getAllPoints();
		TelemDataPacket lastCarReported = this.mySession.getMyCarController().getLastTelemDataPacket();

		Calendar currCalendar = Calendar.getInstance();
		currCalendar.set(currCalendar.get(Calendar.YEAR), currCalendar.get(Calendar.MONTH), currCalendar.get(Calendar.DAY_OF_MONTH)+1, 8, 0);
		long nextStartTime = currCalendar.getTimeInMillis();

		List<GeoCoord> points = routeToTraverse.getTrailMarkers(); // the GeoCoords of the route
		Map<GeoCoord, Double> testSpeedProfile = new HashMap<GeoCoord, Double>(); // map to store speed profile
		ArrayList<SimFrame> frames = new ArrayList<SimFrame>(); // list to store the frames from the sim results of each chunk
		SpeedReport report;
		double currentSpeed = 50.0; // may turn this into a parameter later so
									// we can set what the starting speed is
		
		int chunk_per_forecast = 50; 
		
		int chunkStart = 0;

		TreeMap<Integer, ForecastIO> inflectionPoints = mySession.getMyWeatherController()
				.findInflectionPoints(routeToTraverse, currentForecastReport.getForecasts());
		
		testSpeedProfile.put(points.get(0), 0.0);
		long totalTime = 0;
		
	try{
			for (int chunkEnd : inflectionPoints.keySet()) {
				int points_per_chunk = (chunkEnd - chunkStart + 1)/chunk_per_forecast;
				int remainder = (chunkEnd - chunkStart + 1)%points_per_chunk;
				
				for (int i = chunkStart; i < chunkEnd; i += points_per_chunk ) {
					if (remainder > 0) {
						report = getSpeedProfileForChunk(routeToTraverse, points.subList(i, i + points_per_chunk + 1), simmedForecastReport,
								lastCarReported, nextStartTime, 1, 10, inflectionPoints.get(chunkEnd), currentSpeed);
						i++;
						remainder--;
					}
					else {
						report = getSpeedProfileForChunk(routeToTraverse, points.subList(i, i + points_per_chunk), simmedForecastReport,
								lastCarReported, nextStartTime, 1, 10, inflectionPoints.get(chunkEnd), currentSpeed);
					}
					currentSpeed = report.getSpeed(); // change current speed to the speed of the car at the end of the chunk
					lastCarReported = report.getSpeedResult().getFinalTelemData();
					testSpeedProfile.putAll(report.getSpeedProfile()); // add new speed profiles to map
					frames.addAll(report.getSpeedResult().getListOfFrames()); // add sim frames to list
				}
				chunkStart = ++chunkEnd;	
			}
			
			if (inflectionPoints.size() != 1) {
			report = getSpeedProfileForChunk(routeToTraverse, points.subList(chunkStart, points.size()), simmedForecastReport,
					lastCarReported, nextStartTime, 1, 10, currentForecastReport.getForecasts().get(inflectionPoints.size()), currentSpeed);
			lastCarReported = report.getSpeedResult().getFinalTelemData();
			testSpeedProfile.putAll(report.getSpeedProfile()); // add new speed profiles to map
			frames.addAll(report.getSpeedResult().getListOfFrames()); // add sim frames to list
			}
			
	}
		catch(NotEnoughChargeException e) {
			System.err.println("Starting speed cannot make it through route");
		}
		
		// return Speed Report with all the speed profiles and sim result with
		// all the frames and total time from each chunk
		return new SpeedReport(testSpeedProfile, new SimResult(frames, totalTime, lastCarReported), currentSpeed);
	}

	/**
	 * Method runs the simulation on a small chunk of the route and returns the
	 * speed report for that simulation. Currently, it has the car travel at 50
	 * km/hr and if the car cannot finish the whole chunk at that speed, it will
	 * decelerate by 10 km/hr. If the car cannot make it through the chunk given
	 * the current charge, method throws an exception.
	 * 
	 * @param routeToTraverse:
	 *            The complete route the sim runs on
	 * @param chunk:
	 *            The list of GeoCoords that the method runs the sim on
	 * @param simmedForecastReport:
	 *            The Forecast Report containing forecasts for toTransverse
	 * @param lastCarReported:
	 *            the car's telemetry data at the start of the simulated route
	 *            chunk
	 * @param startTime:
	 *            The time at which the race will begin
	 * @param lapNum:
	 *            The lap that the simulation is simulating
	 * @param minCharge:
	 *            The minimum percentage of charge that is accpetable at the end
	 *            of this sim race
	 * @param inflectionPoint
	 * @param startingSpeed:
	 *            initial speed of the car
	 * @return SpeedReport that has speed profile and sim results
	 * @throws NotEnoughChargeException 
	 */
	private SpeedReport getSpeedProfileForChunk(Route routeToTraverse, List<GeoCoord> chunk,
		ForecastReport simmedForecastReport, TelemDataPacket lastCarReported, long startTime, int lapNum,
		double minCharge, ForecastIO inflectionPoint, double startingSpeed) throws NotEnoughChargeException {
		
		Map<GeoCoord, Double> speeds = new HashMap<GeoCoord, Double>();
		SimResult results = new SimResult(new ArrayList<SimFrame>(), 10, lastCarReported);
		double speed = startingSpeed;

		// initialize every geo coord of the chunk with the same speed
		for (GeoCoord g : chunk) {
			speeds.put(g, speed);
		}
		boolean validprofile = false; // flag to check if the car will run out
										// of charge

		// if car does run out of charge, speeds are lowered for this chunk until it works
		while (!validprofile) {
			// simV2 throws an exception if there is not enough charge, so use that to check
			try {
				// run sim from start of chunk to end of chunk
				results = new SimEngine().runSimV2(routeToTraverse, chunk.get(0), chunk.get(chunk.size() - 1),
						simmedForecastReport, lastCarReported, speeds, startTime, 1, 10, inflectionPoint);
				validprofile = true;
			}

			catch (NotEnoughChargeException e) {
				// go through map and change speeds if theres not enough charge
				speed -= 10;
				// if the speed is at 0 (there is no way for the car to make it through the chunk given the amount of charge), throw an exception
				if (speed <= 0) {
					throw new NotEnoughChargeException(0,0, "speed cannot make it through the whole route");
			 	}
				for (GeoCoord g : speeds.keySet()) {	
					speeds.replace(g, speed);
				}
				System.out.println("DECELERATING TO " + speed);
			}
		}
		SpeedReport report = new SpeedReport(speeds, results, speed);
		return report;
	}
}
