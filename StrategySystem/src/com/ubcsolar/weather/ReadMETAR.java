package com.ubcsolar.weather;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadMETAR {
	
	static ArrayList<METAR> listOfMetars = new ArrayList<METAR>();
	public static void ReadMETAR() {
		
		try{
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
				METAR current;
				boolean bstation_id = false;
				boolean bobservation_time = false;
				boolean blatitude = false;
				boolean blongitude = false;
				boolean btemp = false;
				boolean bdewpoint = false;
				boolean bwind_dir = false;
				boolean bwind_speed = false;
				boolean bvisibility = false;
				boolean bskycond = false;
				String stationID;
				long observationTime;

				public void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {
		 
				if (qName.equalsIgnoreCase("METAR")) {
						bstation_id = true;
					}
				if (qName.equalsIgnoreCase("station_id")) {
					bstation_id = true;
				}
				
				if (qName.equalsIgnoreCase("observation_time")) {
					bobservation_time = true;
				}
				
				if (qName.equalsIgnoreCase("latitude")) {
					blatitude = true;
				}
				
				if (qName.equalsIgnoreCase("longitude")) {
					blongitude = true;
				}
				
				if (qName.equalsIgnoreCase("temp_c")) {
					btemp = true;
				}
		 
				if (qName.equalsIgnoreCase("dewpoint_c")) {
					bdewpoint = true;
				}
		 
				if (qName.equalsIgnoreCase("wind_dir_degrees")) {
					bwind_dir = true;
				}
		 
				if (qName.equalsIgnoreCase("wind_speed_kt")) {
					bwind_speed = true;
				}
				
				if (qName.equalsIgnoreCase("visibility_statute_mi")) {
					bvisibility = true;
				}
				
				if (qName.equalsIgnoreCase("Sky Conditions")) {
					bskycond = true;
				}
			
			}
				
				
			
			public void characters(char ch[], int start, int length) throws SAXException {
				 
				if (bstation_id) {
					stationID= new String(ch, start, length);
					System.out.println("STATION ID: " + new String(ch, start, length));
					bstation_id = false;
				}
				
				if (bobservation_time) {
					System.out.println("OBSERVATION TIME : " + new String(ch, start, length));
					bobservation_time = false;
				}
				
				
				if (blatitude) {
					System.out.println("LATITUDE : " + new String(ch, start, length));
					blatitude = false;
				}
				
				if (blongitude) {
					System.out.println("LONGITUDE : " + new String(ch, start, length));
					blongitude = false;
				}
				
				if (btemp) {
					System.out.println("TEMPERATURE (deg C) : " + new String(ch, start, length));
					btemp = false;
				}
		 
				if (bdewpoint) {
					System.out.println("DEW POINT (deg C) : " + new String(ch, start, length));
					bdewpoint = false;
				}
		 
				if (bwind_dir) {
					System.out.println("WIND DIRECTION (deg) : " + new String(ch, start, length));
					bwind_dir = false;
				}
		 
				if (bwind_speed) {
					System.out.println("WIND SPEED (kt) : " + new String(ch, start, length));
					bwind_speed = false;
				}
		 
				if (bvisibility) {
					System.out.println("VISIBILITY (mi): " + new String(ch, start, length));
					bvisibility = false;
					System.out.println("");
				}
				
		
				if (bskycond) {
					System.out.println("SKY CONDITIONS : " + new String(ch, start, length));
					bskycond = false;
				}
				
			}
			
			public void endElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
				current = new METAR(this.observationTime, stationID, 5);
				listOfMetars.add(current);
			
			}
		};
		
		saxParser.parse("res/test_METAR_xml", handler);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	
	
}
