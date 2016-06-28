/**
 * the interface for the UI and the sim. 
 * Can change settings and run new sims, and see the result of the past 
 * ones. 
 */

package com.ubcsolar.sim;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.NewSimulationReportNotification;
import com.ubcsolar.notification.Notification;

public class SimController extends ModuleController {

	public SimController(GlobalController toAdd) {
		super(toAdd);
	}
	
	public void runSimulation(Map<GeoCoord, Double> requestedSpeeds) throws NoForecastReportException, NoLoadedRouteException, NoLocationReportedException, NoCarStatusException{
		//Compile all the information we need. 		
		ForecastReport simmedForecastReport = this.mySession.getMyWeatherController().getSimmedForecastForEveryPointfForLoadedRoute();
		LocationReport lastReported = this.mySession.getMapController().getLastReportedLocation();
		if(lastReported == null){
			throw new NoLocationReportedException();
		}
		Route routeToTraverse = this.mySession.getMapController().getAllPoints();
		
		if (routeToTraverse == null){
			throw new NoLoadedRouteException();
		}
		TelemDataPacket lastCarReported = this.mySession.getMyCarController().getLastTelemDataPacket();
		if(lastCarReported == null){
			throw new NoCarStatusException();
		}
		
		double startTimeNanos = System.nanoTime();
		//run the sim! 
		GeoCoord startPoint = this.mySession.getMapController().findClosestPointOnRoute(lastReported.getLocation());
		
		/*int targetIndex = -1;
		for(int i=0; i<routeToTraverse.getTrailMarkers().size(); i++){ //TODO , in the findClosestPointOnRoute, u find the index on route. can't u use it here as well? instead of finding it again!!!!!
			if(routeToTraverse.getTrailMarkers().get(i).equals(startPoint)){
				targetIndex = i;
			}
		}*/
		int targetIndex= this.mySession.getMapController().getClosestPointIndex();
		
		if(targetIndex == -1){
			throw new IllegalArgumentException();
		}
		if(targetIndex >= routeToTraverse.getTrailMarkers().size()-1){
			this.mySession.sendNotification(new ExceptionNotification(new IndexOutOfBoundsException(), "ERROR: Simulation started at last point"));
			//can still let it go through and calculate an empty simulation.
		}
		List<SimFrame> simFrames = new SimEngine().runSimulation(routeToTraverse, targetIndex, simmedForecastReport, lastCarReported, requestedSpeeds);
		
		double endTimeNanos = System.nanoTime();
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Sim completed in " + ((endTimeNanos -startTimeNanos)/1000000) + "ms");
		SimulationReport toSend = new SimulationReport(simFrames,requestedSpeeds, "some info");
		this.mySession.sendNotification(new NewSimulationReportNotification(toSend));
	}
	
	/**
	 * this is where the class receives any notifications it registered for. 
	 * the "shoulder tap" 
	 */
	@Override
	public void notify(Notification n) {
		//handle any notifications that were registered for here
		if(n.getClass() == NewMapLoadedNotification.class){
			SimulationReport toSend = new SimulationReport(new ArrayList<SimFrame>(),new HashMap<GeoCoord, Double>(), "Deleted");
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Deleted last run Sim because new route loaded");
			this.mySession.sendNotification(new NewSimulationReportNotification(toSend));
		}
	}

	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	public void register() {
		this.mySession.register(this, NewMapLoadedNotification.class);

	}
}
