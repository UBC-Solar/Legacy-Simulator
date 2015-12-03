/**
 * This class adds the points contained in a KML file to a given list of points 
 * using the SAX parser class.
 */
package com.ubcsolar.map;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;  
import javax.xml.parsers.SAXParserFactory;  

import org.xml.sax.Attributes;  
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;  
import org.xml.sax.helpers.LocatorImpl;

import com.ubcsolar.common.GeoCoord;
  
public class SaxKmlParser extends DefaultHandler{
	  
	String nameTag="close";  
	String coordinatesTag="close";  
	String placemarkTag = "open";
	String pointName = "";
	String coordinates = "";
	List<GeoCoord> toFill;
	// parse the XML specified in the given path and uses supplied  
	// handler to parse the document  
	// this calls startElement(), endElement() and character() methods  
	// accordingly  
   
	
	/**
	 * Entry point for the class, fills the List from the file. 
	 * NOTE: Adds points in order (which means that if Google Maps
	 * lists the route, then the cities, it will assume you do the route, then go between the 
	 * cities again. 
	 * @param toFill - the List to fillwith points
	 * @param file - the valid KML file to read from
	 * @return - a List with all points
	 * @throws SAXException - if something was wrong with the parser
	 * @throws IOException - if something is wrong with the file (not found etc)
	 * @throws ParserConfigurationException 
	 */
	public <InputClass extends List<GeoCoord>> InputClass parseToPoints(InputClass toFill, String file) throws SAXException, IOException, ParserConfigurationException{  
	this.toFill = toFill;

	   
   // obtain and configure a SAX based parser  
   SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();  
  
   // obtain object for SAX parser  
   SAXParser saxParser = saxParserFactory.newSAXParser();
   
	saxParser.parse(file, this);  
	return toFill;
}
   
   // this method is called every time the parser gets an open tag '<'  
   // identifies which tag is being open at time by assigning an open flag  
   public void startElement(String uri, String localName, String qName,  
     Attributes attributes) throws SAXException {  
    
 
    if (qName.equalsIgnoreCase("NAME")) {  
   	 nameTag = "open";  
    }  

    if (qName.equalsIgnoreCase("Coordinates")) {  
   	 coordinatesTag = "open";  
   	 coordinates = "";
    }  
   }  


   // prints data stored in between '<' and '>' tags  
   public void characters(char ch[], int start, int length)  
     throws SAXException {  
      
    if (nameTag.equals("open")) {
   	 String tempPointName = new String(ch, start, length);
   	 pointName = tempPointName;
    }  
    if (coordinatesTag.equals("open")) {  
     coordinates += new String(ch, start, length);  
    }  
   }

   // calls by the parser whenever '>' end tag is found in xml   
   // makes tags flag to 'close'  
   public void endElement(String uri, String localName, String qName)  
     throws SAXException {  
      
    if (qName.equalsIgnoreCase("name")) {  
   	// System.out.println("Name: " + pointName);
   	 nameTag = "close";  
    }  
    if (qName.equalsIgnoreCase("coordinates")) {  
   	 coordinatesTag = "close";  
    }  
    if (qName.equalsIgnoreCase("point")) {
   	 //System.out.println(coordinates);
   	 
   	 parsePoints(coordinates, pointName);
   	 coordinates = "";
   	 
   	 
   	 //System.out.println("I would make a point here");
   	 
    }  
    if (qName.equalsIgnoreCase("linestring")) {  
   	// System.out.println(coordinates);
    	parsePoints(coordinates, pointName);
        coordinates = "";
   	 //System.out.println("I would make a list of points here"); 
    }  
   }
   

   /**
    * Parse a list of points in String form from the xml document into Points in the system 
    * @param aBigStringOfCoordinates - everything between the <coordinates> tags in the kml. 
    * @param pointName2 - the name of the point/route (to include in the info on the first point)
    * @throws SAXParseException - if anything goes wrong (i.e converting to doubles, or not having 3 parts to a coordinate)
    */
	private void parsePoints(String aBigStringOfCoordinates, String pointName2) throws SAXParseException {
		
		String delim = "[\n\\s]+"; //use whitespace or newlines as the delim. 
		String[] allPoints = aBigStringOfCoordinates.split(delim);
		int pointCount = 0; //to count the number of points added
							//(may not be equal to length of allPoints, due to extra '\n's or whitepace)
		for(int i = 0; i<allPoints.length; i++){
			if(allPoints[i].length()>2){ //eliminates blank rows. Must have at least 2 commas to be a valid coordinate. 
				String[] coordinatePieces = allPoints[i].split("[,\\s]+");
				if(coordinatePieces.length<3){ //i.e not a valid coordinate. (even altitude 0 would be ok)
					throw new SAXParseException("Not three parts to this coordinate. "
							+ "May have had whitespace or newlines between lat, long, and elevation",
							new LocatorImpl());
				}
				
				GeoCoord temp;
				try{
				temp = new GeoCoord(Double.parseDouble(coordinatePieces[0]),
										Double.parseDouble(coordinatePieces[1]),
										Double.parseDouble(coordinatePieces[2]));
				}
				catch(IllegalArgumentException e){ //if we can't parse into a Double
					throw new SAXParseException("Error converting from String to coordinate."
							+ " Invalid Character?"
							, new LocatorImpl());
				}
			/*	if(pointCount == 0){
					temp.setInformation(pointName2); //if it's the first one, set the name as the info. 
													//first one in a route will be 'directions from __ to__'
													//first (and only) in a point will be the city name.
				}*/
				toFill.add(temp); //add to end of the toFill list. 
				pointCount++; //because the number of points may be different than array position (blanks, etc). 
			
				
				//for testing: print out the point I just added.
				//System.out.println(temp.getInformation() + " " + temp.getLat() + "," + temp.getLon() + "," + temp.getElevationInMeters());
				
			}
			
		}
		
		
	}
   
   

   


}  