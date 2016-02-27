/**
 * the interface for the UI and the sim. 
 * Can change settings and run new sims, and see the result of the past 
 * ones. 
 */

package com.ubcsolar.sim;
import java.util.List;
import java.util.Map;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SimulationReport;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.exception.NoCarStatusException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;
import com.ubcsolar.notification.ExceptionNotification;
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
		
		//run the sim! 
		List<SimFrame> simFrames = new SimEngine().runSimulation(routeToTraverse, lastReported, simmedForecastReport, lastCarReported, requestedSpeeds);
		
		SimulationReport toSend = new SimulationReport(simFrames, "some info");
		this.mySession.sendNotification(new NewSimulationReportNotification(toSend));
	}
	
	/**
	 * this is where the class receives any notifications it registered for. 
	 * the "shoulder tap" 
	 */
	@Override
	public void notify(Notification n) {
		//handle any notifications that were registered for here

	}

	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	public void register() {
		// Do stuff here

	}
}
