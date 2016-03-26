package com.ubcsolar.notification;

import java.text.SimpleDateFormat;

import org.jfree.data.Values;

import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.DataUnit;
import com.ubcsolar.common.SimulationReport;

public class NewSimulationReportNotification extends NewDataUnitNotification {
	private final SimulationReport theReport;
	
	public NewSimulationReportNotification(SimulationReport theReport){
		super();
		this.theReport = theReport;
	}

	@Override
	public DataUnit getDataUnit() {
		return theReport;
	}

	@Override
	public String getMessage() {
		return "new simulation run at: " + GlobalValues.hourMinSec.format(super.getTimeCreated());
	}

}
