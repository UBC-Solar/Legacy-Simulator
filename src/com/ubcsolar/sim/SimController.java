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
		    if (!results.getStatus()) {
		    	break;
		    }
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
	
	public void runSimulationWithManualSpeeds(int laps, long startTime, Map<GeoCoord, Map<Integer, Double>> manualSpeedProfile)
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

		SimResult result = null;
		List<SimFrame> resultFrames = new ArrayList<SimFrame>(); //list of sim frames
		//idea is to use runSimV2 for every part of the route where the car travels at constant speed until the speed change
		//the point with the different speed would be the last point, and the point after would be the start of the next simulation
		GeoCoord start = routeToTraverse.getTrailMarkers().get(1); //first point of the simulation (need to ask why its 1, running point 0 to 1 is always a fail)
		GeoCoord end; //last point of the simulation
		TreeMap<Integer, ForecastIO> inflectionPoints = mySession.getMyWeatherController()
				.findInflectionPoints(routeToTraverse, currentForecastReport.getForecasts());
		ForecastIO current_forecast = inflectionPoints.get(inflectionPoints.keySet().toArray()[0]);
		long nextStartTime = startTime;
		long totalTime = 0;
		
		for (int i = 1; i <= laps; i++) {
			//iterate through all the points except for the last point (perserve last point for one last runSimV2 call since car may be traveling at constant speeds
			//until the end
			for (int point_index = 1; point_index < routeToTraverse.getTrailMarkers().size(); point_index++) {
				//check to see if an inflection point has reached and a new forecast is used
				if (inflectionPoints.keySet().contains(i)) {
					current_forecast = inflectionPoints.get(i);
				}
				
				//check to see if there is a speed change
				//if there is, run a simulation and update list of sim frames, new start speed, new start point, start time and car status using results
				if (Math.abs(manualSpeedProfile.get(start).get(i) - manualSpeedProfile.get(routeToTraverse.getTrailMarkers().get(point_index)).get(i)) > 0.000001 ||
						point_index == routeToTraverse.getTrailMarkers().size()) {
					end = routeToTraverse.getTrailMarkers().get(point_index);
					//min charge is set to 0. Since speeds are decided by users and no adjustments will be made, we let the simulation use up all the charge
					//for each runSimV2 call. We assume that if the cannot make it through without conserving charge for the next runSimV2 call (giving a non-zero
					//value for min charge), it will not make it through if charge is conserved (may need to change for single lap routes, definitely needs tweaking for
					//for multiple laps)
					result = new SimEngine().runSimV2(routeToTraverse, start, end,
							lastCarReported, manualSpeedProfile.get(start).get(i), manualSpeedProfile.get(end).get(i),
							nextStartTime, i, 0, current_forecast);
					//if sim is not successful, remove all the points before the end of the sim (only want to graph to the point the car fails, so speeds after 
					//that needs to be removed in speed profile)
					if(!result.wasRunSuccessful()) {
						System.err.println("Car cannot make it through the route with selected speeds");
						for (int removed_point = routeToTraverse.getTrailMarkers().size() - 1; (!end.equals(removed_point) && removed_point >= 0);removed_point--) {
							manualSpeedProfile.remove(routeToTraverse.getTrailMarkers().get(removed_point));
						}
						break;
					}
					//if sim is successful, update parameters
					if (point_index < routeToTraverse.getTrailMarkers().size() - 1) {
						start = routeToTraverse.getTrailMarkers().get(++point_index);
					}
					lastCarReported = result.getFinalTelemData();
					resultFrames.addAll(result.getListOfFrames()); // add sim frames to list
	                nextStartTime += result.getTravelTime();
	                totalTime += result.getTravelTime();
				}
			}
			if (!result.wasRunSuccessful()) {
				break;
			}
		}
			/*
			//after simulating almost the whole route, check to see if simulations were successful
			//if not, print error message 
			if (!result.wasRunSuccessful()) {
				System.err.println("Car cannot make it through the route with selected speeds");
				break;
			}
			//if everything has been successful so far, run one last simulation to the end of the route
			//this cannot be done in the previous for loop since the loop only runs simulations if there is a speed change
			//most, if not all of the time, the speed would be constant from one point of the route to the end, and this 
			//will not trigger a runSimV2 call in the for loop
			else {
				result = new SimEngine().runSimV2(routeToTraverse, start, routeToTraverse.getTrailMarkers().get(routeToTraverse.getTrailMarkers().size() - 1),
						lastCarReported, manualSpeedProfile.get(start).get(i), 
						manualSpeedProfile.get(routeToTraverse.getTrailMarkers().get(routeToTraverse.getTrailMarkers().size()-1)).get(i),
						nextStartTime, i, 0, current_forecast);
				
				lastCarReported = result.getFinalTelemData();
				resultFrames.addAll(result.getListOfFrames()); // add sim frames to list
	            totalTime += result.getTravelTime();
			}*/
		
		SimulationReport toSend = new SimulationReport(resultFrames, manualSpeedProfile, "some info");

		this.mySession.sendNotification(new NewSimulationReportNotification(toSend)); //send new sim report to be graphed
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
		boolean completed = true;

		long nextStartTime = startTime;

		List<GeoCoord> points = routeToTraverse.getTrailMarkers(); // the GeoCoords of the route
		Map<GeoCoord, Double> testSpeedProfile = new LinkedHashMap<GeoCoord, Double>(); // map to store speed profile
		ArrayList<SimFrame> frames = new ArrayList<SimFrame>(); // list to store the frames from the sim results of each chunk
		SpeedReport report = new SpeedReport(new LinkedHashMap<GeoCoord,Double>(),
				new SimResult(new ArrayList<SimFrame>(), 0,
						prevCarStatus, new LinkedHashMap<GeoCoord, Double>(), false),
				0, false);
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
		
	
		double minCharge;
		for (int chunkEnd : inflectionPoints.keySet()) {
			int pointsPerSubChunk = (chunkEnd - chunkStart + 1) / subchunksPerForecast;
			int remainder = (chunkEnd - chunkStart + 1) % subchunksPerForecast;
			
			for (int i = chunkStart; i <= chunkEnd; i += pointsPerSubChunk ) {
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
				if (!report.getStatus()) {
					completed = false;
					break;
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
	
		// return Speed Report with all the speed profiles and sim result with

		return new SpeedReport(
				testSpeedProfile,
				new SimResult(frames, totalTime, prevCarStatus, report.getSpeedProfile(), true),
				currentSpeed, completed);
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
		double minCharge, ForecastIO inflectionPoint, double startingSpeed) {

		SimResult results = new SimResult(new ArrayList<SimFrame>(), 0,
				lastCarReported, new LinkedHashMap<GeoCoord, Double>(), false);
		double finalSpeed = startingSpeed;
		double currSpeed = finalSpeed;
		boolean completed = true;
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
					System.err.println("speed cannot make it through the whole route");
					completed = false;
					break;
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
		SpeedReport report = new SpeedReport(results.getSpeedProfile(), results, results.getFinalTelemData().getSpeed(), completed);
		return report;
	}
}
