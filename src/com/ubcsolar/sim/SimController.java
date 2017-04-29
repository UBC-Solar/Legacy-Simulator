/**
 * the interface for the UI and the sim. 
 * Can change settings and run new sims, and see the result of the past 
 * ones. 
 */

package com.ubcsolar.sim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;

import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
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

	private static double minFinalCharge = 5.0;

	private ForecastReport currentForecastReport;

	public SimController(GlobalController toAdd) {
		super(toAdd);
	}

	/**
	 *
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
	public void runSimulation(int laps, long startTime)
			throws NoForecastReportException, NoLoadedRouteException, NoLocationReportedException,
			NoCarStatusException {
		if (laps <= 0) {
			throw new IllegalArgumentException("Number of Laps too low, must go at least 1 lap");
		}
		// Compile all the information we need.

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

		TelemDataPacket prevCarStatus = lastCarReported;
		double startingVelocity = 1;
		int numSubchunksPerForecast = 5;
		SpeedReport results;
		Map<GeoCoord, Map<Integer,Double>> speedProfile = new LinkedHashMap<GeoCoord, Map<Integer,Double>>();
		List<SimFrame> resultFrames = new ArrayList<SimFrame>();

		for (int i = 1; i <= laps; i++) {
		    results = getSpeedReport(startTime, i, startingVelocity, numSubchunksPerForecast, laps, prevCarStatus);
            for(GeoCoord k : results.getSpeedProfile().keySet()){
                Map<Integer,Double> lapSpeed;
                if(i == 1) {
                    lapSpeed = new LinkedHashMap<Integer,Double>();
                } else {
                    lapSpeed = speedProfile.get(k);
                }
                lapSpeed.put(i, results.getSpeedProfile().get(k));
                speedProfile.put(k, lapSpeed);
            }
            resultFrames.addAll(results.getSpeedResult().getListOfFrames());
            startingVelocity = results.getSpeed();
            startTime += results.getSpeedResult().getTravelTime();
            prevCarStatus = results.getSpeedResult().getFinalTelemData();
        }
		SimulationReport toSend = new SimulationReport(resultFrames, speedProfile, "some info");

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
	public SpeedReport getSpeedReport(long startTime, int lapNum, double startingVelocity, int numSubchunksPerForecast,
                                      int totalNumLaps, TelemDataPacket prevCarStatus)
			throws NoForecastReportException, NoLoadedRouteException,
			NoLocationReportedException, NoCarStatusException {
		// things needed for simV2
		ForecastReport simmedForecastReport = this.mySession.getMyWeatherController()
				.getSimmedForecastForEveryPointForLoadedRoute();
		Route routeToTraverse = this.mySession.getMapController().getAllPoints();

		long nextStartTime = startTime;

		List<GeoCoord> points = routeToTraverse.getTrailMarkers(); // the GeoCoords of the route
		Map<GeoCoord, Double> testSpeedProfile = new LinkedHashMap<GeoCoord, Double>(); // map to store speed profile
		ArrayList<SimFrame> frames = new ArrayList<SimFrame>(); // list to store the frames from the sim results of each chunk
		SpeedReport report = new SpeedReport(new LinkedHashMap<GeoCoord,Double>(),
				new SimResult(new ArrayList<SimFrame>(), 0,
						prevCarStatus, new LinkedHashMap<GeoCoord, Double>(), false),
				0);
		double currentSpeed = startingVelocity;
		int subchunksPerForecast = numSubchunksPerForecast;
		int chunkStart = 1; //set to 1 so it doesn't override point 0 with 0 velocity
		int currentSubChunk = 1;


		TreeMap<Integer, ForecastIO> inflectionPoints = mySession.getMyWeatherController()
				.findInflectionPoints(routeToTraverse, currentForecastReport.getForecasts());

		int numSubchunksPerLap = subchunksPerForecast * inflectionPoints.keySet().size();

		if (lapNum == 1) {
            testSpeedProfile.put(points.get(0), 0.0);
        } else {
		    testSpeedProfile.put(points.get(0), startingVelocity);
        }
		long totalTime = 0;
		
	try{
		double minCharge;
		for (int chunkEnd : inflectionPoints.keySet()) {
			int pointsPerSubChunk = (chunkEnd - chunkStart + 1) / subchunksPerForecast;
			int remainder = (chunkEnd - chunkStart + 1) % subchunksPerForecast;
			
			for (int i = chunkStart; i < chunkEnd; i += pointsPerSubChunk ) {
				minCharge = getMinCharge(numSubchunksPerLap * totalNumLaps,
                        (currentSubChunk + (lapNum - 1) * numSubchunksPerLap));
				if (remainder > 0) {
					report = getSpeedProfileForChunk(routeToTraverse, points.subList(i, i + pointsPerSubChunk + 1), simmedForecastReport,
							prevCarStatus, nextStartTime, lapNum, minCharge, inflectionPoints.get(chunkEnd), currentSpeed);
					i++;
					remainder--;
				}
				else {
					report = getSpeedProfileForChunk(routeToTraverse, points.subList(i, i + pointsPerSubChunk), simmedForecastReport,
							prevCarStatus, nextStartTime, lapNum, minCharge, inflectionPoints.get(chunkEnd), currentSpeed);
				}
				currentSpeed = report.getSpeed(); // change current speed to the speed of the car at the end of the chunk
				prevCarStatus = report.getSpeedResult().getFinalTelemData();
				testSpeedProfile.putAll(report.getSpeedProfile()); // add new speed profiles to map
				frames.addAll(report.getSpeedResult().getListOfFrames()); // add sim frames to list
                nextStartTime += (report.getSpeedResult().getTravelTime());
                totalTime += report.getSpeedResult().getTravelTime();
    			currentSubChunk++;
			}
			chunkStart = ++chunkEnd;
		}
		minCharge = 100 - ((100 - minFinalCharge) / totalNumLaps * lapNum);
		if (inflectionPoints.size() != 1) {
			report = getSpeedProfileForChunk(routeToTraverse, points.subList(chunkStart, points.size() - 1), simmedForecastReport,
					prevCarStatus, nextStartTime, lapNum, minCharge, currentForecastReport.getForecasts().get(inflectionPoints.size()), currentSpeed);
			prevCarStatus = report.getSpeedResult().getFinalTelemData();
			testSpeedProfile.putAll(report.getSpeedProfile()); // add new speed profiles to map
			frames.addAll(report.getSpeedResult().getListOfFrames()); // add sim frames to list
		}
	}
		catch(NotEnoughChargeException e) {
			System.err.println("Starting speed cannot make it through route");
		}
	
		// return Speed Report with all the speed profiles and sim result with

		return new SpeedReport(
				testSpeedProfile,
				new SimResult(frames, totalTime, prevCarStatus, report.getSpeedProfile(), true),
				currentSpeed);
	}
	
	double getMinCharge(int numSubChunks, int currentSubChunk)
	{
		double chargePerChunk = (100 - minFinalCharge) / numSubChunks;
		double minCharge = 100 - chargePerChunk * currentSubChunk;
		if (minCharge < 0)
		{
			return 0;
		}
		return minCharge;
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

		SimResult results = new SimResult(new ArrayList<SimFrame>(), 0,
				lastCarReported, new LinkedHashMap<GeoCoord, Double>(), false);
		double finalSpeed = startingSpeed;
		double currSpeed = finalSpeed;
		
		// initialize every geo coord of the chunk with the same speed
		boolean validprofile = false; // flag to check if the car will run out
										// of charge

		// if car does run out of charge, speeds are lowered for this chunk until it works
		int numRetries = 0;
		while (!validprofile) {

			// run sim from start of chunk to end of chunk
			results = new SimEngine().runSimV2(routeToTraverse, chunk.get(0), chunk.get(chunk.size() - 1),
					lastCarReported, startingSpeed, finalSpeed, startTime, lapNum, minCharge, inflectionPoint);
			validprofile = results.wasRunSuccessful();

			if(!validprofile) {
				finalSpeed -= .5;
				if (finalSpeed <= 0) {
					throw new NotEnoughChargeException(0,0, "speed cannot make it through the whole route");
				}
			}
			if(results.getFinalTelemData().getStateOfCharge() > (minCharge + 2) && numRetries < 10
					&& finalSpeed < GlobalValues.MAX_SPEED && validprofile) {
				finalSpeed += 2;
				if(finalSpeed > GlobalValues.MAX_SPEED)
					finalSpeed = GlobalValues.MAX_SPEED;
				validprofile = false;
				numRetries++;
			}
		}

		SpeedReport report = new SpeedReport(results.getSpeedProfile(), results, results.getFinalTelemData().getSpeed());
		return report;
	}
}
