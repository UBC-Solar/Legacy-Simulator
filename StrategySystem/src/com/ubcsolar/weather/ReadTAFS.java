package com.ubcsolar.weather;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ReadTAFS {
public static void ReadTAFS(String argv[]) {
		
		try{
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
			
				boolean bstation_id = false;
				boolean bissue_time = false;
				boolean blatitude = false;
				boolean blongitude = false;
				
				boolean bfcst_time_from = false;
				boolean bfcst_time_to = false;
				
				boolean bwind_dir = false;
				boolean bwind_speed = false;
				boolean bvisibility = false;				
				boolean bskycond = false;

				public void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {
		 
				
				if (qName.equalsIgnoreCase("station_id")) {
					bstation_id = true;
				}
				
				if (qName.equalsIgnoreCase("issue_time")) {
					bissue_time = true;
				}
				
				if (qName.equalsIgnoreCase("latitude")) {
					blatitude = true;
				}
				
				if (qName.equalsIgnoreCase("longitude")) {
					blongitude = true;
				}
				
				if (qName.equalsIgnoreCase("fcst_time_from")) {
					bfcst_time_from = true;
				}
		 
				if (qName.equalsIgnoreCase("fcst_time_to")) {
					bfcst_time_to = true;
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
					System.out.println("STATION ID: " + new String(ch, start, length));
					bstation_id = false;
				}
				
				if (bissue_time) {
					System.out.println("ISSUE TIME : " + new String(ch, start, length));
					bissue_time = false;
				}
				
				
				if (blatitude) {
					System.out.println("LATITUDE : " + new String(ch, start, length));
					blatitude = false;
				}
				
				if (blongitude) {
					System.out.println("LONGITUDE : " + new String(ch, start, length));
					blongitude = false;
				}
				
				if (bfcst_time_from) {
					System.out.println("FOREAST TIME FROM : " + new String(ch, start, length));
					bfcst_time_from = false;
				}
		 
				if (bfcst_time_to) {
					System.out.println("FORECAST TIME TO : " + new String(ch, start, length));
					bfcst_time_to = false;
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
			
			
		};
		
		saxParser.parse("res/test_TAFS_xml", handler);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	
	
}


