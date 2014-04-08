/**
 * 
 */
package com.ubcsolar.notification;

import java.util.ArrayList;
import java.util.List;

import com.ubcsolar.weather.METAR;

/**
 * @author Noah
 *
 */
public class NewMetarReportLoaded extends Notification {

	private ArrayList<METAR> listOfMetars;
	
	
	/* (non-Javadoc)
	 * @see com.ubcsolar.notification.Notification#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public NewMetarReportLoaded(List<METAR> listOfMetars){
		listOfMetars = new ArrayList<METAR>(listOfMetars);
	}
	
	public List<METAR> getListOfMetars(){
		return listOfMetars;
	}
	
}
