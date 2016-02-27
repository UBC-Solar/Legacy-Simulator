/**
 * the interface for the UI and the sim. 
 * Can change settings and run new sims, and see the result of the past 
 * ones. 
 */

package com.ubcsolar.sim;
import java.util.Map;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.Notification;

public class SimController extends ModuleController {

	public SimController(GlobalController toAdd) {
		super(toAdd);
	}
	
	public void runSimulation(Map<GeoCoord, Double> requestedSpeeds) throws NoForecastReportException, NoLoadedRouteException, NoLocationReportedException{
		ForecastReport simmedForecastReport;
		
		simmedForecastReport = this.mySession.getMyWeatherController().getSimmedForecastForEveryPointfForLoadedRoute();
		LocationReport lastReported = this.mySession.getMapController().getLastReportedLocation();
		if(lastReported == null){
			throw new NoLocationReportedException();
		}
		
		//SimEngine test = new SimEngine();
		
		
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
