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

public class RunSimTest {
	private static GlobalController theProgram;
	private static String file = "res\\HopeToMerritt.kml";
	
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, JDOMException, NoForecastReportException, NoLoadedRouteException, NoLocationReportedException, NoCarStatusException{
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Application started");
		
		theProgram = new GlobalController(true);
		
		theProgram.getMapController().load(new File(file));
		theProgram.getMyWeatherController().downloadNewForecastsForRoute(100);
		
		
		theProgram.getMyCarController().adviseOfNewCarReport(RandomObjectGenerator.generateNewTelemDataPack());
		LocationReport carLocationReported = new LocationReport(/*new GeoCoord(49.26068,-123.24576,97.41090393066406)*/theProgram.getMapController().getAllPoints().getTrailMarkers().get(0), "raven", "generated");
		theProgram.getMapController().recordNewCarLocation(carLocationReported);
		//theProgram.getMySimController().runSimulation(new HashMap<GeoCoord, Map<Integer, Double>>(),1);
		
	}
	
	
}
