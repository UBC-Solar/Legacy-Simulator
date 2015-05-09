package com.ubcsolar.weather;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ubcsolar.notification.NewMetarReportLoadedNotification;

public class ReadMETAR {
	
	static ArrayList<METAR> listOfMetars = new ArrayList<METAR>();
	public static void ReadMETAR(String filename, WeatherController mySession) {
		
		try{
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
				METAR current;
				
				boolean brawText= false;
				boolean bstationID= false;
				boolean bobsvTime= false;
				boolean blatitude= false;
				boolean blongitude= false;
				boolean btempC= false;
				boolean bdewPointC= false;
				boolean bwindDirection= false;
				boolean bwindSpeed= false;
				boolean bvisibility= false;
				boolean baltim= false;
				boolean bseaPressure= false;
				boolean flightCategory1 = false;
				boolean bpressureTendency= false;
				boolean bmetarType= false;
				boolean belevation= false;
				String rawText;
				String stationID;
				String observationTime;
				String latitude;
				String longitude;
				String tempC;
				String dewPointC;
				String windDirection;
				String windSpeed;
				String visibilityStatute;
				String altim;
				String seaLevelPressure;
				String flightCategory;
				String pressureTendency;
				String metarType;
				String elevation;
				
				public void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {
		 			
					if (qName.equalsIgnoreCase("raw_text")) {
						brawText = true;
					}
					
					
					if (qName.equalsIgnoreCase("station_id")) {
						bstationID = true;
					}
					
					if (qName.equalsIgnoreCase("observation_time")) {
						bobsvTime = true;
					}
					
					if (qName.equalsIgnoreCase("latitude")) {
						blatitude = true;
					}
					
					if (qName.equalsIgnoreCase("longitude")) {
						blongitude = true;
					}
					
					if (qName.equalsIgnoreCase("temp_c")) {
						btempC = true;
					}
					
					if (qName.equalsIgnoreCase("dewpoint_c")) {
						bdewPointC = true;
					}
					
					if (qName.equalsIgnoreCase("wind_dir_degrees")) {
						bstationID = true;
					}
					
					if (qName.equalsIgnoreCase("wind_speed_kt")) {
						bstationID = true;
					}
				
					if (qName.equalsIgnoreCase("visibility_statute_mi")) {
						bvisibility = true;
					}
				
					if (qName.equalsIgnoreCase("altim_in_hg")) {
						baltim = true;
					}
			
					if (qName.equalsIgnoreCase("sea_level_pressure_mb")) {
						bseaPressure = true;
					}
				
					if (qName.equalsIgnoreCase("flight_category")) {
						flightCategory1 = true;
					}
					
					if (qName.equalsIgnoreCase("three_hr_pressure_tendency_mb")) {
						bpressureTendency = true;
					}
				
					if (qName.equalsIgnoreCase("metar_type")) {
						bmetarType = true;
					}
					
					if (qName.equalsIgnoreCase("elevation_m")) {
						belevation = true;
					}
					
				}
				
				
			
			public void characters(char ch[], int start, int length) throws SAXException {
				 
				if (brawText) {
					System.out.println("RAW TEXT: " + new String(ch, start, length));
					rawText = new String(ch, start, length);
					brawText = false;
				}
				
				if (bstationID) {
					System.out.println("STATION ID: " + new String(ch, start, length));
					stationID= new String(ch, start, length);
					bstationID = false;
				}
				
				if (bobsvTime) {
					System.out.println("OBSERVATION TIME: " + new String(ch, start, length));
					observationTime = new String(ch, start, length);
					bobsvTime = false;
				}
				
				if (blatitude) {
					System.out.println("LATITUDE: " + new String(ch, start, length));
					latitude = new String(ch, start, length);
					blatitude = false;
				}
				
				if (blongitude) {
					System.out.println("LONGITUDE: " + new String(ch, start, length));
					longitude = new String(ch, start, length);
					blongitude = false;
				}
				
				if (btempC) {
					System.out.println("TEMPERATURE (deg C): " + new String(ch, start, length));
					tempC = new String(ch, start, length);
					btempC = false;
				}
				
				if (bdewPointC) {
					System.out.println("DEW POINT (deg C): " + new String(ch, start, length));
					dewPointC = new String(ch, start, length);
					bdewPointC = false;
				}
				
				if (bwindDirection) {
					System.out.println("WIND DIRECTION (deg): " + new String(ch, start, length));
					windDirection = new String(ch, start, length);
					bwindDirection = false;
				}
				
				if (bwindSpeed) {
					System.out.println("WIND SPEED (kt): " + new String(ch, start, length));
					windSpeed = new String(ch, start, length);
					bwindSpeed = false;
				}

				if (bvisibility) {
					System.out.println("VISIBILTIY (mi): " + new String(ch, start, length));
					visibilityStatute = new String(ch, start, length);
					bvisibility = false;
				}
				
				if (baltim) {
					System.out.println("ALTIM (hg): " + new String(ch, start, length));
					altim = new String(ch, start, length);
					baltim = false;
				}
				
				if (bseaPressure) {
					System.out.println("SEA LEVEL PRESSURE (mb): " + new String(ch, start, length));
					seaLevelPressure = new String(ch, start, length);
					bseaPressure = false;
				}
				
				if (flightCategory1) {
					System.out.println("FLIGHT CATEGORY: " + new String(ch, start, length));
					flightCategory = new String(ch, start, length);
					flightCategory1 = false;
				}

				if (bpressureTendency) {
					System.out.println("PRESSURE TENDENCY (mb): " + new String(ch, start, length));
					pressureTendency = new String(ch, start, length);
					bpressureTendency = false;
				}
				
				if (bmetarType) {
					System.out.println("METAR TYPE: " + new String(ch, start, length));
					metarType = new String(ch, start, length);
					bmetarType = false;
				}
				
				if (belevation) {
					System.out.println("ELEVATION (m): " + new String(ch, start, length));
					elevation = new String(ch, start, length);
					belevation = false;
				}
				
				
			}
			
			
			public void endElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
				
				try{
				current = new METAR(Long.parseLong(this.rawText), this.stationID, Long.parseLong(this.observationTime), 
						Double.parseDouble(this.latitude), Double.parseDouble(this.longitude), Double.parseDouble(this.tempC),
						Double.parseDouble(this.dewPointC), Integer.parseInt(this.windDirection), Integer.parseInt(this.windSpeed),
						Double.parseDouble(this.visibilityStatute), Double.parseDouble(this.altim), Double.parseDouble(this.seaLevelPressure),
						this.flightCategory, Double.parseDouble(this.pressureTendency), this.metarType, Double.parseDouble(this.elevation));
				
				listOfMetars.add(current);
				}
				
	
				catch(IllegalArgumentException e){
					return;
				}
			
			}
		};
		
		saxParser.parse(filename, handler);
			
		mySession.sendNotification(new NewMetarReportLoadedNotification(listOfMetars));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	
	
}
