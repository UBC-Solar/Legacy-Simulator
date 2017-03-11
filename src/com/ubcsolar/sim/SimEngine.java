package com.ubcsolar.sim;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.data.Values;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.exception.NotEnoughChargeException;

public class SimEngine {
	private final double RECHARGE_TIME_MS= 3*1000;
	private final int EFF_SOLAR_CONSTANT = 990;
	protected int totalCharge;

	public SimEngine() {
		inUseCarModel = new DefaultCarModel();
	}

	private CarModel inUseCarModel;
	
	
	/**
	 * Runs a simulation of the car's performance on the given portion of the provided route,
	 * 	assuming that the car follows the speed profile provided. The method will return a
	 * 	SimResult object (containing the time taken, the final TelemDataPacket, and a list of the
	 * 	SimFrames used to do the simulation)
	 * LIMITATION: Currently can only do one lap at a time. To do multiple laps, call this method
	 * 	multiple times from the SimController (generally, all sims should be done in chunks anyway)
	 * @param toTraverse: The complete route that the simulation is run on
	 * @param startLoc: The starting location for the route chunk to be simulated. startLoc must
	 * 	be part of toTraverse
	 * @param endLoc: The ending location for the route chunk to be simulated. endLoc must be part
	 * 	of toTraverse
	 * @param report: The ForecastReport containing the forecasts for toTraverse. The report must
	 * 	contain a ForecastIO for every GeoCoord in the route chunk that is being simulated. (Use
	 * 	methods in WeatherController to interpolate forecasts if forecast density is less than
	 * 	GeoCoord density)
	 * @param carStartState: the car's telemetry data at the start of the simulated route chunk
	 * @param speedProfile: A map that matches each GeoCoord between startLoc and endLoc with the
	 * 	speed (in km/h) to be simulated during that interval. Currently, this is the limitation that prevents
	 * 	simulating multiple laps (to avoid double mapping GeoCoords)
	 * @param startTime: The time at which the race will begin (in Unix format, i.e. ms from 1/1/70)
	 * @param lapNum: The lap that the simulation is simulating
	 * @param minCharge: The minimum percentage of charge that is acceptable at the end of this segment
	 * 	of the race
	 * @return a SimResult object, containing the simulated travel time, the final TelemDataPacket, 
	 * 	and a list of the SimFrames used to do the simulation
	 * @throws NotEnoughChargeException if the end charge is less than minCharge
	 */
	public SimResult runSimV2(Route toTraverse, GeoCoord startLoc, GeoCoord endLoc,
			ForecastReport report, TelemDataPacket carStartState,
			Map<GeoCoord,Double> speedProfile, long startTime, int lapNum, double minCharge,
			ForecastIO inflectionPoint) throws NotEnoughChargeException{
		
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Sim v2 starting");
		
		//SimResult result = new SimResult(carStartState);
		
		int startingIndex = toTraverse.getIndexOfClosestPoint(startLoc);
		int endingIndex = toTraverse.getIndexOfClosestPoint(endLoc);
		
		GeoCoord currPoint = toTraverse.getClosestPointOnRoute(startLoc);
		
		if(endingIndex < startingIndex){
			throw new IllegalArgumentException("ending location must be after starting location");
		}
		
		List<ForecastIO> forecastList = report.getForecasts();
		/*
		NavigableSet<Integer> inflectionIndices = inflectionPoints.navigableKeySet();
		Integer inflectionIndex = inflectionIndices.first();
		ForecastIO currWeather = inflectionPoints.get(inflectionIndex);
		boolean checkNextForecast = true;
		
		inflectionIndex = inflectionIndices.higher(inflectionIndex);
		if(inflectionIndex == null)
			checkNextForecast = false;
		*/
		//ForecastIO startWeather = forecastList.get(startingIndex);
		FIODataPoint startWeatherPoint = chooseReport(inflectionPoint,startTime);
		LocationReport startLocationReport = new LocationReport(currPoint, "Raven", "Simmed");
		totalCharge = carStartState.getTotalVoltage();
		SimFrame startSimFrame = new SimFrame(startWeatherPoint,carStartState,startLocationReport,startTime,lapNum);
		
		List<SimFrame> listOfFrames = new ArrayList<SimFrame>();
		listOfFrames.add(startSimFrame);
		GeoCoord prevPoint = currPoint;
		long currTime = startTime;
		TelemDataPacket prevStatus = carStartState;
		TelemDataPacket currStatus = prevStatus;
		List<SimEngineHelper> threadList = new ArrayList<SimEngineHelper>();
		for(int i = startingIndex+1; i <= endingIndex; i++){
			currPoint = toTraverse.getTrailMarkers().get(i);
			
			double speed = speedProfile.get(currPoint);
			double distance = prevPoint.calculateDistance(currPoint);
			double timeIncHr = distance/speed;
			double timeIncMS = timeIncHr * 3600000;
			currTime += timeIncMS; 
			/*
			//ForecastIO currWeather = forecastList.get(i);
			if(checkNextForecast && i >= inflectionIndex){
				currWeather = inflectionPoints.get(inflectionIndex);
				inflectionIndex = inflectionIndices.higher(inflectionIndex);
				if(inflectionIndex == null)
					checkNextForecast = false;
			}*/
			
			
			SimEngineHelper currThread = new SimEngineHelper(speed,prevPoint,currPoint,
					currTime,inflectionPoint,this,timeIncHr);
			currThread.run();
			threadList.add(currThread);
//			FIODataPoint currWeatherPoint = chooseReport(currWeather,currTime);
//			currStatus = this.calculateNewTelemPacket(prevStatus, prevPoint, 
//					currPoint, currWeatherPoint, speed, timeIncHr);
//
//			if(currStatus.getStateOfCharge()<minCharge){
//				String message = "Speed profile uses too much charge. Desired end charge is " + minCharge + ", actual end charge is" +  currStatus.getStateOfCharge(); 
//				throw new NotEnoughChargeException(currStatus.getStateOfCharge(), minCharge, message);
//			}
			
//			LocationReport currLocReport = new LocationReport(currPoint, "Raven", "Simmed");
//			
//			SimFrame currSimFrame = new SimFrame(currWeatherPoint,currStatus,currLocReport,currTime,lapNum);
//			listOfFrames.add(currSimFrame);
			
			prevPoint = currPoint;
//			prevStatus = currStatus;
		}
		double currCharge = carStartState.getStateOfCharge();
		for(int i = 0; i < threadList.size(); i++){
			SimEngineHelper currThread = threadList.get(i);
			currCharge += currThread.getChargeDiff();
			if(currCharge > 100)
				currCharge = 100;
			if(currCharge < 0)
				currCharge = 0;
			if(currCharge <= minCharge){
				String message = "Speed profile uses too much charge. Drops below minimum charge of " + minCharge; 
				throw new NotEnoughChargeException(currCharge, minCharge, message);				
			}
			if(i % 20 == 0 || i == threadList.size() - 1){
				currStatus = new TelemDataPacket(currThread.getSpeed(), carStartState.getTotalVoltage(),
						carStartState.getTemperatures(), carStartState.getCellVoltages(), currCharge);
				LocationReport currLocReport = new LocationReport(currThread.getCurrPoint(), "Raven", "Simmed");
				SimFrame currSimFrame = new SimFrame(currThread.getWeatherPoint(),currStatus,currLocReport,
						currThread.getNewTime(),lapNum);
				listOfFrames.add(currSimFrame);
			}
			try {
				currThread.join();
			} catch (InterruptedException e) {
				System.err.println("there was a thread problem in RunSimV2 (something got interrupted by something)");
				e.printStackTrace();
			}
		}
		
		SimResult result = new SimResult(listOfFrames,currTime,currStatus);
		
		return result;
		
	}
	
	/*
	 * Did RequestedSpeeds as a Map<CeoCoord point, Map<lap number, requested speed>>, to be able to 
	 * request different speeds for different laps.  
	 */
	public List<SimFrame> runSimulation(Route toTraverse, int startLocationIndex, ForecastReport weatherReports, TelemDataPacket carStartingCondition, Map<GeoCoord,Map<Integer,Double>> requestedSpeeds, int laps){
		if(laps <= 0){
			throw new IllegalArgumentException("Must go at least one lap");
		}

		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "simulation starting");
		List<SimFrame> listOfFrames = new ArrayList<SimFrame>(toTraverse.getTrailMarkers().size());
		if(startLocationIndex == toTraverse.getTrailMarkers().size()-1){
			return new ArrayList<SimFrame>(); //can't simulate if at end of race.
		}
		
		ForecastIO weather = weatherReports.getForecasts().get(startLocationIndex); //assumes that the number of forecasts in weatherReports = number in Route.
		GeoCoord start = toTraverse.getTrailMarkers().get(startLocationIndex);
		GeoCoord next = toTraverse.getTrailMarkers().get(startLocationIndex + 1); //won't index out of range because of check above. 
		if(requestedSpeeds.get(start) != null && requestedSpeeds.get(start).get(1) != null){
			Double reqSpeed = requestedSpeeds.get(start).get(1);//first lap
		}
		TelemDataPacket startCondition = carStartingCondition;
		FIODataPoint startWeather = new FIODataBlock(weather.getHourly()).datapoint(0);
		LocationReport simmedStartPoint = new LocationReport(toTraverse.getTrailMarkers().get(startLocationIndex), "Raven", "Simmed");
		SimFrame startFrame = new SimFrame(startWeather, startCondition, simmedStartPoint, System.currentTimeMillis(), 1); //starting frame is current.
		listOfFrames.add(startFrame);

		SimFrame lastFrame = startFrame;
		int numOfPoints = toTraverse.getTrailMarkers().size();
		int currentLap = 1; 
		for(int i = startLocationIndex+1; i<(numOfPoints*laps); i++){ 

			/*
			 * By starting at startPos, we calculate the jump from car's current location to the next breadcrumb, rather
			 * than just assuming that it's actually at the last breadcrumb. 
			 * This way may produce errors if the car is actually far off the trail, but the alternative is to advance the car
			 * magically to next breadcrumb, and if the gap between breadcrumbs is big, it may produce an error.  
			 */

			ForecastIO nextWeather;
			GeoCoord nextPoint;
			if (lastFrame.getCarStatus().getSpeed()<=0){
				i--; //if the speed is zero then we need to redo the frame because the car is not moving, and thus is in the same place
				if(i%numOfPoints == 0){
					currentLap--;//was adjusted earlier above, shouldn't have been. 
				}
				if(i<0){
					nextWeather = weatherReports.getForecasts().get(startLocationIndex);
				}
				else{
					nextWeather = weatherReports.getForecasts().get(i%numOfPoints);
				}

				nextPoint = lastFrame.getGPSReport().getLocation();
			}
			else{
				nextWeather = weatherReports.getForecasts().get(i%numOfPoints);
				nextPoint = toTraverse.getTrailMarkers().get(i%numOfPoints);
			}
			Double requestedSpeedTemp = null;
			if(requestedSpeeds.get(nextPoint) != null){
				requestedSpeedTemp = requestedSpeeds.get(nextPoint).get(currentLap);
			}
			SimFrame nextFrame = this.generateNextFrame(lastFrame, nextPoint, nextWeather, requestedSpeedTemp,currentLap);
		
			lastFrame = nextFrame;
			listOfFrames.add(nextFrame);
			if(i%numOfPoints == (numOfPoints-1)){ //if it's the last point in the lap
				currentLap++; //say we're going to the next lap! 
			}
		}	

		return listOfFrames;
	}




	private SimFrame generateNextFrame(SimFrame lastFrame, GeoCoord nextPoint, ForecastIO nextWeather, Double requestedSpeed,int currentLap) {
		TelemDataPacket lastCarStatus = lastFrame.getCarStatus();
		GeoCoord lastPosition = lastFrame.getGPSReport().getLocation();
		double lastSpeed = lastCarStatus.getSpeed();
		long lastTimeStamp = lastFrame.getRepresentedTime();

		double elevationChange = nextPoint.getElevation() - lastPosition.getElevation();

		double speedToDrive;
		if(requestedSpeed == null){
			speedToDrive = calculateBestSpeed(lastCarStatus.getSpeed(), elevationChange, lastCarStatus.getStateOfCharge()); //stubMethod. Also this is a greedy algo.
			//obviously need to add more arguments ^^
		}
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^KEEP ALL ABOVE^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		else{
			speedToDrive = requestedSpeed;
		}
//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

		//not sure if calculateDistance() takes elevation into account....
		double distanceCovered = lastPosition.calculateDistance(nextPoint)*1000; 
		long nextSimFrameTime;
		long timeSinceLastFrame;
		double timeSinceLastFrameInHr;

		if (speedToDrive >= .001){
			double tempTime = (distanceCovered/(speedToDrive * 1000.0))*60.0*1000.0*60.0; //double check units. km/h and m?? distanceCovered is in meters
			timeSinceLastFrame = (long) tempTime;		
			nextSimFrameTime = lastTimeStamp + timeSinceLastFrame;
			timeSinceLastFrameInHr = timeSinceLastFrame/(60.0*1000.0*60.0);
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ENCLOSED IS GOOD STUFF^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                      STUFF BELOW HERE TO THE NEXT LINE I AM NOT TOO FAMILIAR WITH
			//System.out.println(distanceCovered);
		}
		else{
			double tempTime = RECHARGE_TIME_MS; //double check units. km/h and m?? distanceCovered is in meters
			timeSinceLastFrame = (long) tempTime;		
			nextSimFrameTime = lastTimeStamp + timeSinceLastFrame;
			timeSinceLastFrameInHr = tempTime/(1000*60*60);
			//System.out.println("AGAHAHAHAHAHAHAH RAN");
			//System.out.println(timeSinceLastFrameInHr);
			//System.out.println(distanceCovered);
		}



		FIODataPoint forecastForPoint = chooseReport(nextWeather, nextSimFrameTime);
		double sunPowerInWatts = calculateSunPower(forecastForPoint);


		double SunCharge = (sunPowerInWatts*timeSinceLastFrameInHr)/(inUseCarModel.getMaxBatteryCap()); //divide watt hrs from the sun by max watt hrs to get the percentage of charge from the sun
		if(SunCharge>2000000){
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "" + distanceCovered + " " +  timeSinceLastFrameInHr + " Speed: " + speedToDrive);
		}

		TelemDataPacket newCarStatus;
		newCarStatus = calculateNewCarStatus(lastCarStatus, distanceCovered, elevationChange, forecastForPoint, speedToDrive, SunCharge);
		LocationReport nextLocationReport = generateLocationReport(lastFrame.getGPSReport(), nextPoint);

		SimFrame toReturn = new SimFrame(forecastForPoint, newCarStatus, nextLocationReport, nextSimFrameTime,currentLap);

		try {
			Date fcParseTime = GlobalValues.forecastIODateParser.parse(toReturn.getForecast().time());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return toReturn; 
	}


	private LocationReport generateLocationReport(LocationReport oldReport, GeoCoord nextPoint) {
		LocationReport toReturn = new LocationReport(nextPoint, oldReport.getCarName(), "Simulated");
		return toReturn;
	}

	
	public double calculateChargeDiff(GeoCoord startLoc, GeoCoord endLoc, FIODataPoint forecastForPoint,
			double speed, double timeTaken){
		double resistivePower = calculateResistivePower(forecastForPoint, endLoc, startLoc, speed);
		
		double sunPower = calculateSunPower(forecastForPoint);
		//System.out.println("Resistive power: " + resistivePower + " sunPower is : " + sunPower);
		double netPower = sunPower - resistivePower;//in Watts
		
		double changeInCharge = netPower/totalCharge*timeTaken;//in amp-hours
		double changeInChargePerCent = changeInCharge/GlobalValues.BATTERY_MAX_CHARGE;
		return changeInChargePerCent*100;
	}
	
	//timeTaken is in hours
	private TelemDataPacket calculateNewTelemPacket(TelemDataPacket prevStatus, GeoCoord startLoc,
			GeoCoord endLoc, FIODataPoint forecastForPoint, double speed, double timeTaken){
		double changeInChargePerCent = calculateChargeDiff(startLoc,endLoc,forecastForPoint,speed,timeTaken);
		double newCharge = prevStatus.getStateOfCharge()+changeInChargePerCent;
		if(newCharge > 100)
			newCharge = 100;
		if(newCharge < 0)
			newCharge = 0;
		
		TelemDataPacket newStatus = new TelemDataPacket(speed, prevStatus.getTotalVoltage(), 
				prevStatus.getTemperatures(), prevStatus.getCellVoltages(), newCharge);
		
		return newStatus;
		
	}
	
//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	/**
	 * Important helper method, calculates the state of the car after traverssing the last gap. 
	 * @param lastCarStatus - th
	 * @param distanceCovered
	 * @param elevationChange
	 * @param forecastForPoint
	 * @param speedToDrive
	 * @param sunPowerInWatts
	 * @return
	 */
	private TelemDataPacket calculateNewCarStatus(TelemDataPacket lastCarStatus, double distanceCovered,
			double elevationChange, FIODataPoint forecastForPoint, double speedToDrive, double SunCharge) {
		//TODO actually calculate the car...
		//TODO review the state of charge

		double generateSoC = generateSoC(lastCarStatus.getStateOfCharge(), elevationChange, speedToDrive, SunCharge);
		TelemDataPacket toReturn = new TelemDataPacket(speedToDrive,
				lastCarStatus.getTotalVoltage(), 
				lastCarStatus.getTemperatures(), 
				lastCarStatus.getCellVoltages(), 
				generateSoC, 
				(distanceCovered/(speedToDrive*1000)*60*60*1000));

		return toReturn;
	}
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^KEEP ENCLOSED ABOVE^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

	/**
	 * May generate a value more than 100 or less than 0, but keeps it somewhere within that.
	 * Max change is +/- 2% from last.  
	 * @param lastSoC
	 * @return
	 */
	
	//TODO: change this method
	private double generateSoC(double lastSoC, double elevationChange, double speed, double SoCFromSun) {
		if (elevationChange < 0){
			if (lastSoC+2+SoCFromSun>=100){
				lastSoC=100;
				return lastSoC;
			}
			else{
				return lastSoC+2+SoCFromSun;
			}
		}
		else if(elevationChange >0){
			if(lastSoC-speed/50.0+SoCFromSun<=0){
				lastSoC=0;
				return lastSoC;
			}
			else{
				return lastSoC-speed/50.0+SoCFromSun;
			}
		}
		else if(speed == 0){
			if (lastSoC+1.5+SoCFromSun>=100){
				lastSoC=100;
				return lastSoC;
			}
			else{
				return lastSoC+1.5+SoCFromSun;
			}
		}
		else{
			if(lastSoC-speed/100.0+SoCFromSun<=0){
				lastSoC=0;
				return lastSoC;
			}
			else{
				return lastSoC-speed/100.0+SoCFromSun;
			}
		}
	}



	/*
	private double generateRandomSoC(double lastSoC) {
		Random rng = new Random();
		int change = rng.nextInt(5); //up or down max 2% in a frame.
		if(lastSoC<=0){
			return lastSoC + change;
		}
		if(lastSoC>=100){
			return lastSoC-change;
		}
		else{
			return lastSoC + change - 2; 
		}
	}
	 */

	
//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	/**
	 * Calculates power gain from solar panels on the car, assuming it is experiencing
	 * the weather given in forecastForPoint, according to formulas given at 
	 * http://scool.larc.nasa.gov/lesson_plans/CloudCoverSolarRadiation.pdf
	 * and at http://photovoltaic-software.com/PV-solar-energy-calculation.php
	 * @param forecastForPoint: the forecast for the point you're trying to predict
	 * 		power output at. 
	 * @return the amount of power (in Watts) that the panels will produce in the given
	 * 		situation
	 */
	
	//TODO: more sophisticated calculations, involving time of day/year, angle of incidence
		// of sun, etc.
	//TODO: make this private after JUnit testing
	public double calculateSunPower(FIODataPoint forecastForPoint) {
		double cloudCover = forecastForPoint.cloudCover();
		double cloudCoverFactor = 990.0*(1-0.75*cloudCover*cloudCover*cloudCover);
		double panelArea = inUseCarModel.getSolarPanelArea();
		double sunPower = panelArea * GlobalValues.PANEL_EFFICIENCY * cloudCoverFactor;
		
		return sunPower;
		
		//		Calendar rightNow = Calendar.getInstance();
//		rightNow.setTimeInMillis((long) timeOfDay);
//
//		int hour= rightNow.HOUR_OF_DAY;
//		// TODO Auto-generated method stub
//
//		//Get the sun elevation given the time of day and the latitude and longitude. 
//
//		double cloudCover = lastFrame.getForecast().cloudCover();
//
//
//
//		//assume 100 watts per square foot. (thanks random forum guy)
//		//10.7639 sq feet per sq. meter. 
//		double timeFactor=1;
//
//		//replace with sunrise equation later
//		if (hour<12 && hour>6){
//			timeFactor=(4-24/hour)/2; //calculations just meant to get a 0-1 scale
//		}
//		if (hour>12 && hour<21){
//			timeFactor=(1-hour/21)/.4286; //.4286 is a conversion factor to get on a scale of 0-1
//		}
//
//		double watts = 100*10.7639 * squareMetersOfPanel* timeFactor* cloudCover;
//
//		//calculate how much sun there is given the weather (cloudy? Probably not much). 
//		//I think there's actually a parameter in FIODataPoint for sun exposure. If not, use the cloudyness measurement. 
//
//		//assuming no weather at all right now. 
//
//		//because watts, don't need to include time. 
//		return watts; 
	}
//^^^^^^^^^^^^^^^^^SOME GOOD IDEAS IN HERE, LIKE CHANGING SUNLIGHT WITH TIME OF DAY AND GETTING CLOUD COVER^^^^^^^^^^^^^^^^^^^^^^^^^
//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	
	/**
	 * Helper function, picks the hourly report that is closest in time to timeFrame
	 * @param ForecastIO with full selection of weather data, and a set of hourly forecasts that
	 * 		is sorted by time
	 * @param timeFrame: time in ms since jan1 1970 (see System.currentTimeMillis)
	 * @return an FIODataPoint containing the hourly forecast for the hour closest to timeFrame
	 */

	public FIODataPoint chooseReport(ForecastIO weather, double timeFrame) {
		JsonObject hourly = weather.getHourly();
		JsonArray hourlyData = (JsonArray)hourly.get("data");
		int currTime = Integer.parseInt(((JsonObject)hourlyData.get(0)).get("time").toString());
		int bestIndex = 0;
		double smallestDiff = Math.abs(timeFrame-currTime);
		double prevDiff = smallestDiff;
		for(int i = 1; i < hourlyData.size(); i++){
			currTime = Integer.parseInt(((JsonObject)hourlyData.get(i)).get("time").toString());
			double currDiff = Math.abs(timeFrame-currTime);
			if(currDiff < smallestDiff){
				smallestDiff = currDiff;
				bestIndex = i;
			}
			if(currDiff > prevDiff)
				break;
			prevDiff = currDiff;
		}
		FIODataPoint toReturn  = new FIODataBlock(weather.getHourly()).datapoint(bestIndex);
		toReturn.setTimezone("PST");
		return toReturn;
	}
//^^^^^^^^^^^^^^^^^^^^^^^^^^THIS HAS BEEN UPDATED^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	/**
	 * Calculates the estimated resistive force due to air drag acting on the car in the
	 *  interval from fromLoc to toLoc 
	 * @param toLoc: breadcrumb that simulated car is traveling to
	 * @param fromLoc: breadcrumb that simulated car is traveling from
	 * @param carSpeed: the simulated car's current speed (from the previous breadcrumb) in km/h
	 * @param toForecast: the weather forecast at the current time at the destination location,
	 * 			used to find the headwind
	 * @return the estimated resistive force acting on the car during the interval (fromLoc-toLoc)
	 * 			This value will be positive if the drag is resisting the car's motion (i.e.
	 * 			there is a headwind) or negative if it's assisting (tailwind) 
	 */
	//TODO: change to private after JUnit testing
	public double calculateDrag(GeoCoord toLoc, GeoCoord fromLoc, double carSpeed, 
			FIODataPoint toForecast){
		double latDiff = toLoc.getLat()-fromLoc.getLat();
		double lonDiff = toLoc.getLon()-fromLoc.getLon();
		double carBearing;//measured as angle in degrees, with 0 at north and measured clockwise
		double alpha = Math.atan(latDiff/lonDiff);
		//double alphaDegrees = alpha * 180.0 / Math.PI;
		if(lonDiff>=0){
			carBearing = Math.PI / 2.0 - alpha;
		}else{
			carBearing = 3.0 * Math.PI / 2.0 - alpha;
		}
		
		double windSpeed = toForecast.windSpeed() * GlobalValues.KMH_TO_MS_FACTOR;
		double carSpeedInMS = carSpeed * GlobalValues.KMH_TO_MS_FACTOR;
		double relativeVelocity = carSpeedInMS;
		if(windSpeed != 0){
			//this if statement is necessary because windBearing() will be undefined if
			//windSpeed() is 0
			double windBearing = toForecast.windBearing() * Math.PI / 180;
			relativeVelocity = carSpeedInMS + windSpeed*Math.cos((windBearing-carBearing));
		}
		boolean isTailwind = false;
		if(Math.abs(relativeVelocity) < Math.abs(carSpeedInMS/**Math.sin(carBearing)*/))
			isTailwind = true;
		double dragMag = 0.5 * GlobalValues.CAR_CROSS_SECTIONAL_AREA * GlobalValues.DRAG_COEFF * 
				relativeVelocity * relativeVelocity;
		if(isTailwind)
			return -1*dragMag;
		else
			return dragMag;
	}
	
	//TODO: change this method
	private double calculateBestSpeed(double lastCarSpeed, double elevationChange, double SoC) {
		double SpeedReturn;
		double MaxCarSpeed=110;

		if (SoC<=0){
			if(lastCarSpeed-2<0){
				return 0;
			}
			else{
				SpeedReturn=lastCarSpeed-2;
				return SpeedReturn;
			}
		}
		else if(elevationChange<0 || elevationChange>0){
			SpeedReturn=lastCarSpeed;
			return SpeedReturn;
		}
		else{
			if (lastCarSpeed+3>MaxCarSpeed){
				SpeedReturn=MaxCarSpeed;
				return SpeedReturn;
			}
			else{
				SpeedReturn=lastCarSpeed+3;
				return SpeedReturn;
			}
		}

	}


//not sure if this does anything
	private int getStartPos(ArrayList<GeoCoord> trailMarkers, GeoCoord location) {
		// find the closest point and return the position number. 
		
		return 0;
	}
	
	public double getInclinationAngle(GeoCoord startPoint, GeoCoord endPoint) {
		double inclinationAngle = 0;
		double distance = startPoint.calculateDistance(endPoint)*1000;
		double heightDifference = endPoint.getElevation() - startPoint.getElevation();
		/*
		 * If heightDifference returns a positive number, this means that we are elevating from a lower starting point.
		 * Thus, the inclinationAngle should be positive.
		 */
		
		inclinationAngle = Math.atan(heightDifference/distance);
		return inclinationAngle;
		
	}
	
	public double getGradientResistanceForce(double angle) {
		// F = mgsin(theta)
		double force = GlobalValues.CAR_MASS * 9.8 * Math.sin(angle); //force is positive if it opposes the direction of travel
		return force;
	}
	
	public double getRollingResistanceForce(double angle, double tirePressure, double velocity) {
		double rollingCoefficient = 0.005 + (1/tirePressure)*(0.01+0.0095*Math.pow(velocity/100, 2));
		// Normal Force = mgcos(theta)
		double normalForce = GlobalValues.CAR_MASS * 9.8 * Math.cos(angle);
		double force = rollingCoefficient * normalForce; //force is positive if it opposes the direction of travel
		return force;
	}
	
	/**
	 * Calculates total power coming from various resistive forces
	 * @param currForecast: the FIODataPoint corresponding to the forecast at the destination location
	 * 			at the current time for whichever simFrame is calling this method
	 * @param toLoc: the end location of the simFrame calling this method
	 * @param fromLoc: the start location of the car in the simFrame calling this method
	 * @param carSpeed: the car's predicted speed in the simFrame calling this method
	 * @return power loss/gain due to resistive forces during the given interval
	 */
	
	public double calculateResistivePower(FIODataPoint currForecast, GeoCoord toLoc, GeoCoord fromLoc, 
			double carSpeed){
		double inclinationAngle = getInclinationAngle(fromLoc, toLoc);
		double gradientForce = getGradientResistanceForce(inclinationAngle);
		double frictionForce = getRollingResistanceForce(inclinationAngle, GlobalValues.TIRE_PRESSURE, carSpeed);
		double dragForce = calculateDrag(toLoc, fromLoc, carSpeed, currForecast);
		double resistivePower = (gradientForce + frictionForce + dragForce)*carSpeed
				*GlobalValues.KMH_TO_MS_FACTOR/GlobalValues.ENGINE_EFF;
		return resistivePower;
	}
}
