package com.ubcsolar.map.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.map.MapController;

public class MapControllerTests {
	static MapController toTest;
	static String fileNameToLoad;
	
	@BeforeClass
	public static void setup() throws IOException, SAXException, ParserConfigurationException, JDOMException{
		GlobalController newSession = new GlobalController(false);
		toTest = new MapController(newSession);
		
		//tests written to be dynamic, but assumes a west->east route.
		fileNameToLoad = "res\\Actual EDC to Calgary.kml";
		toTest.load(new File(fileNameToLoad));
	}
	
	@Test
	public void closestPointShouldBeItself(){
		List<GeoCoord> breadCrumbs = toTest.getAllPoints().getTrailMarkers();
		GeoCoord pointToTest = breadCrumbs.get(breadCrumbs.size() - 5);//5 chosen arbitrarily
		assertEquals(pointToTest, toTest.findClosestPointOnRoute(pointToTest));
	}
	
	@Test
	public void findClosestPointBeforeRouteShouldReturnFirstPoint() throws IOException, SAXException, ParserConfigurationException, JDOMException{
		List<GeoCoord> breadCrumbs = toTest.getAllPoints().getTrailMarkers();
		GeoCoord start = breadCrumbs.get(0);//start point
		GeoCoord pointToTest = new GeoCoord(start.getLat()+0.0000001,start.getLon(), start.getElevation());
		assertEquals(start, toTest.findClosestPointOnRoute(pointToTest));
	}
	
	@Test
	public void findClosestPointAfterRouteShouldReturnLastPoint() throws IOException, SAXException, ParserConfigurationException, JDOMException{
		List<GeoCoord> breadCrumbs = toTest.getAllPoints().getTrailMarkers();
		GeoCoord start = breadCrumbs.get(breadCrumbs.size()-1);//end point
		GeoCoord pointToTest = new GeoCoord(start.getLat()-0.0000001,start.getLon(), start.getElevation());
		assertEquals(start, toTest.findClosestPointOnRoute(pointToTest));
	}
	
	
	
}
