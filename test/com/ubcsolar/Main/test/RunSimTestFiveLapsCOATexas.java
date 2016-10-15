package com.ubcsolar.Main.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.exception.NoCarStatusException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;
import com.ubcsolar.testAssistanceFiles.RandomObjectGenerator;

public class RunSimTestFiveLapsCOATexas {
	private static GlobalController theProgram;
	
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, JDOMException, NoForecastReportException, NoLoadedRouteException, NoLocationReportedException, NoCarStatusException{
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Application started");
		theProgram = new GlobalController(true);
		
		theProgram.getMapController().load(new File("res\\Circuit_Of_Americas_labled.kml"));
		theProgram.getMyWeatherController().downloadNewForecastsForRoute(10);
		theProgram.getMyCarController().adviseOfNewCarReport(RandomObjectGenerator.generateNewTelemDataPack());
		LocationReport carLocationReported = new LocationReport(new GeoCoord(30.131831,-97.6398,155.8408355712891), "raven", "generated");
		theProgram.getMapController().recordNewCarLocation(carLocationReported);
		theProgram.getMySimController().runSimulation(new HashMap<GeoCoord, Map<Integer, Double>>(),5);
	}
	
	
}
