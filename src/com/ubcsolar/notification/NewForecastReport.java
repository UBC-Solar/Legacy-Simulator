package com.ubcsolar.notification;

import com.ubcsolar.common.DataUnit;
import com.ubcsolar.common.ForecastReport;

public class NewForecastReport extends NewDataUnitNotification {

	private final ForecastReport theReport;
	public NewForecastReport(ForecastReport theReport) {
		this.theReport = theReport;
	}

	@Override
	public String getMessage() {
		return "Set of forecasts gathered for " + theReport.getRouteNameForecastsWereCreatedFor();
	}

	@Override
	public DataUnit getDataUnit() {
		return theReport;
	}

}
