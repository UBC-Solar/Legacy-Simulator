package com.ubcsolar.sim;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.Values;

import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;

public class SimEngine {

	public SimEngine() {
		
	}

	
	public List<SimFrame> runSimulation(Route toTraverse, LocationReport startLocation, ForecastReport weatherReports, TelemDataPacket carStartingCondition, Map<GeoCoord,Double> requestedSpeeds){
	
	SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "simulation starting");
	List<SimFrame> listOfFrames = new ArrayList<SimFrame>(toTraverse.getTrailMarkers().size());
	
	int startPos = getStartPos(toTraverse.getTrailMarkers(), startLocation.getLocation());
	ForecastIO weather = weatherReports.getForecasts().get(startPos); //assumes that the number of forecasts in weatherReports = number in Route.
	GeoCoord start = toTraverse.getTrailMarkers().get(startPos);
	GeoCoord next = toTraverse.getTrailMarkers().get(startPos + 1);
	Double reqSpeed = requestedSpeeds.get(start);
	TelemDataPacket startCondition = carStartingCondition;
	FIODataPoint startWeather = new FIODataBlock(weather.getHourly()).datapoint(0);
	
	SimFrame startFrame = new SimFrame(startWeather, startCondition, startLocation, System.currentTimeMillis()); //starting frame is current.
	listOfFrames.add(startFrame);
	
	SimFrame lastFrame = startFrame;
	for(int i = startPos; i<toTraverse.getTrailMarkers().size(); i++){ 
		/*
		 * By starting at startPos, we calculate the jump from car's current location to the next breadcrumb, rather
		 * than just assuming that it's actually at the last breadcrumb. 
		 * This way may produce errors if the car is actually far off the trail, but the alternative is to advance the car
		 * magically to next breadcrumb, and if the gap between breadcrumbs is big, it may produce an error.  
		 */
		ForecastIO nextWeather = weatherReports.getForecasts().get(i);
		GeoCoord nextPoint = toTraverse.getTrailMarkers().get(i);
		SimFrame nextFrame = this.generateNextFrame(lastFrame, nextPoint, nextWeather, requestedSpeeds.get(nextPoint));
		lastFrame = nextFrame;
		listOfFrames.add(nextFrame);
	}	
	
	return listOfFrames;
	}




	private SimFrame generateNextFrame(SimFrame lastFrame, GeoCoord nextPoint, ForecastIO nextWeather, Double requestedSpeed) {
		TelemDataPacket lastCarStatus = lastFrame.getCarStatus();
		GeoCoord lastPosition = lastFrame.getGPSReport().getLocation();
		double lastSpeed = lastCarStatus.getSpeed();
		long lastTimeStamp = lastFrame.getRepresentedTime();
		
		
		double speedToDrive;
		if(requestedSpeed == null){
			if(lastCarStatus.getSpeed() > 1){
				speedToDrive = lastCarStatus.getSpeed(); //'always drive the same speed' is simple, but dumb. 
			}
			else{
				speedToDrive = calculateBestSpeed(); //stubMethod. Also this is a greedy algo. 
			}
		}
		else{
			speedToDrive = requestedSpeed;
		}
		
		//not sure if calculateDistance() takes elevation into account....
		double distanceCovered = lastPosition.calculateDistance(nextPoint, DistanceUnit.METERS); 
		double elevationChange = nextPoint.getElevation() - lastPosition.getElevation();
		
		
		double tempTime = (distanceCovered/(speedToDrive * 1000))*60*1000*60; //double check units. km/h and m??
		long timeSinceLastFrame = (long) tempTime;		
		long nextSimFrameTime = lastTimeStamp + timeSinceLastFrame;
		FIODataPoint forecastForPoint = chooseReport(nextWeather, nextSimFrameTime);
		double squareMetersOfPanel = 10; //total random guess. TODO: get actual measurement. 
		double sunPowerInWatts = calculateSunPower(nextPoint, forecastForPoint, (lastTimeStamp + (timeSinceLastFrame/2)), squareMetersOfPanel);
		
		TelemDataPacket newCarStatus;
		newCarStatus = calculateNewCarStatus(lastCarStatus, distanceCovered, elevationChange, forecastForPoint, speedToDrive, sunPowerInWatts);
		LocationReport nextLocationReport = generateLocationReport(lastFrame.getGPSReport(), nextPoint);
		
		SimFrame toReturn = new SimFrame(forecastForPoint, newCarStatus, nextLocationReport, nextSimFrameTime);

		try {
			Date fcParseTime = GlobalValues.forecastIODateParser.parse(toReturn.getForecast().time());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return toReturn; 
	}

	
	private LocationReport generateLocationReport(LocationReport oldReport, GeoCoord nextPoint) {
		// TODO Auto-generated method stub
		LocationReport toReturn = new LocationReport(nextPoint, oldReport.getCarName(), "Simulated");
		return toReturn;
	}


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
			double elevationChange, FIODataPoint forecastForPoint, double speedToDrive, double sunPowerInWatts2) {
		//TODO actually calculate the car...
		//TODO review the state of charge
		TelemDataPacket toReturn = new TelemDataPacket(speedToDrive,
				lastCarStatus.getTotalVoltage(), 
				lastCarStatus.getTemperatures(), 
				lastCarStatus.getCellVoltages(), lastCarStatus.getStateOfCharge(), 
				(distanceCovered/(speedToDrive*1000)*60*60*1000));
		
		return toReturn;
	}


	/**
	 * Helper function; calculate the amount of solar power falling on the car during the frame. 
	 * @param nextPoint - to get the lon/lat for sun elevation in degrees
	 * @param forecastForPoint - the weather forecast for the point
	 * @param squareMetersOfPanel - the total collection area of solar panels
	 * @return
	 */
   private double calculateSunPower(GeoCoord nextPoint, FIODataPoint forecastForPoint, double timeOfDay, double squareMetersOfPanel) {
		// TODO Auto-generated method stub
	   
	   //Get the sun elevation given the time of day and the latitude and longitude. 
	   
	   //assume 100 watts per square foot. (thanks random forum guy)
	   //10.7639 sq feet per sq. meter. 
	   
	   double watts = 100*10.7639 * squareMetersOfPanel;
	   
	   //calculate how much sun there is given the weather (cloudy? Probably not much). 
	   //I think there's actually a parameter in FIODataPoint for sun exposure. If not, use the cloudyness measurement. 
	   
	   //assuming no weather at all right now. 
	   
	   //because watts, don't need to include time. 
		return watts; 
	}


/**
    * Helper function, picks the right report from a list of hourly reports and a time. 
    * @param weather
    * @param timeFrame ms since jan1 1970 (see System.currentTimeMillis)
    * @return
    */
	private FIODataPoint chooseReport(ForecastIO weather, double timeFrame) {
		//TODO actually make this choose something. 
		FIODataPoint toReturn  = new FIODataBlock(weather.getHourly()).datapoint(0);
		toReturn.setTimezone("PST");
		return toReturn;
	}


	private double calculateBestSpeed() {
		return 22.0; // Chosen by fair dice roll, guaranteed to be random. https://xkcd.com/221/ 
		//obviously need to put a real algo here... 
		//TODO put real algo here. 
	}


	private int getStartPos(ArrayList<GeoCoord> trailMarkers, GeoCoord location) {
		// find the closest point and return the position number. 
		//TODO actually calculate start position. 
		return 0;
	}
}
