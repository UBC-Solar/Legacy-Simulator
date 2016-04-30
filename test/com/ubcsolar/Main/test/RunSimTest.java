package com.ubcsolar.Main.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class RunSimTest {
	private static GlobalController theProgram;
	
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, JDOMException, NoForecastReportException, NoLoadedRouteException, NoLocationReportedException, NoCarStatusException{
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Application started");
		theProgram = new GlobalController(true);
		//will first commit test
		theProgram.getMapController().load(new File("res\\UBC_to_Coquitlam.kml"));
		theProgram.getMyWeatherController().downloadNewForecastsForRoute(50);
		
		//HashMap<String, > temperatures = new HashMap
		TelemDataPacket fakeReport = generateNewTelemDataPack();
		theProgram.getMyCarController().adviseOfNewCarReport(fakeReport);
		LocationReport carLocationReported = new LocationReport(new GeoCoord(49.26068,-123.24576,97.41090393066406), "raven", "generated");
		theProgram.getMapController().recordNewCarLocation(carLocationReported);
		theProgram.getMySimController().runSimulation(new HashMap<GeoCoord, Double>());
		
	}
	
	private static TelemDataPacket generateNewTelemDataPack(){
		Random rng = new Random();
		int speed = 30;
		int stateOfCharge = 85;
		double totalV = rng.nextFloat()*0.2 - 0.1;
		totalV = totalV > 50 ? 50 : totalV < 40 ? 40 : totalV;
		double temps[] = new double[6];
		for(int i=0; i<6; i++)
			temps[i] += rng.nextFloat() - 0.5;
		
		
		HashMap<String,Integer> temperatures = new HashMap<String,Integer>();
		temperatures.put("bms", (int) temps[0]);
		temperatures.put("motor", (int) temps[1]);
		temperatures.put("pack0", (int) temps[2]);
		temperatures.put("pack1", (int) temps[3]);
		temperatures.put("pack2", (int) temps[4]);
		temperatures.put("pack3", (int) temps[5]);
		HashMap<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		for(int i = 0; i<4; i++){ //Current number of cells coming in pack is 4. Will probably have to adjust that.
			cellVoltages.put(i, generatePackVoltages(totalV));
		}
		TelemDataPacket tempPacket = new TelemDataPacket(speed, (int)totalV, temperatures, cellVoltages, stateOfCharge);
		return tempPacket;
	}
	
	private static ArrayList<Float> generatePackVoltages(double totalV){
		ArrayList<Float> cell1 = new ArrayList<Float>();
		Random rng = new Random();
		
		putPackVoltages(10, totalV, cell1);
		return cell1;
	}
	
	private static void putPackVoltages(int nCells, double totalV, ArrayList<Float> voltages){
		if(nCells == 0){
			return;
		}else if(nCells == 1){
			voltages.add((float)totalV);
			return;
		}
		
		Random rng = new Random();
		int half1 = nCells / 2;
		double middleV = totalV * half1 / nCells;
		middleV = middleV * (1.0 + 0.2 * rng.nextFloat() + 0.1);
		putPackVoltages(half1, middleV, voltages);
		putPackVoltages(nCells-half1, totalV-middleV, voltages);
	}
}
