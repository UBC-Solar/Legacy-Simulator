/**
 * 
 */
package com.ubcsolar.notification;

import java.util.ArrayList;
import java.util.List;

import com.ubcsolar.weather.METAR;
import com.ubcsolar.weather.Taf;

/**
 * @author Noah
 *
 */
public class NewTafReportLoadedNotification extends Notification {

	private ArrayList<Taf> listOfTafs;
	
	
	/* (non-Javadoc)
	 * @see com.ubcsolar.notification.Notification#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO write a mesage for this
		return null;
	}

	public NewTafReportLoadedNotification(List<Taf> listOfMetars){
		listOfMetars = new ArrayList<Taf>(listOfMetars);
	}
	
	public List<Taf> getListOfMetars(){
		return listOfTafs;
	}
	
}
