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
  
public class SaxKmlParser extends DefaultHandler{
	  
	String nameTag="close";  
	String coordinatesTag="close";  
	String placemarkTag = "open";
	String pointName = "";
	String coordinates = "";
	List<Point> toFill;
	// parse the XML specified in the given path and uses supplied  
	// handler to parse the document  
	// this calls startElement(), endElement() and character() methods  
	// accordingly  
   
	
	/**
	 * Entry point for the class, fills the List from the file.  
	 * @param toFill - the List to fillwith points
	 * @param file - the valid KML file to read from
	 * @return - a List with all points
	 * @throws SAXException - if something was wrong with the parser
	 * @throws IOException - if something is wrong with the file (not found etc)
	 * @throws ParserConfigurationException 
	 */
	public <InputClass extends List<Point>> InputClass parseToPoints(InputClass toFill, String file) throws SAXException, IOException, ParserConfigurationException{  
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
		
		String delim = "[\n\\s]+";
		String[] allPoints = aBigStringOfCoordinates.split(delim);
		int pointCount = 0; //to count the number of points added
							//(may not be equal to length of allPoints, due to extra '\n's or whitepace)
		for(int i = 0; i<allPoints.length; i++){
			if(allPoints[i].length()>2){ //eliminates blank rows. Must have at least 2 commas. 
				String[] coordinatePieces = allPoints[i].split("[,\\s]+");
				if(coordinatePieces.length<3){
					throw new SAXParseException("Not three parts to this coordinate. "
							+ "May have had whitespace or newlines between lat, long, and elevation",
							new LocatorImpl());
				}
				
				Point temp;
				try{
				temp = new Point(Double.parseDouble(coordinatePieces[0]),
										Double.parseDouble(coordinatePieces[1]),
										Double.parseDouble(coordinatePieces[2]));
				}
				catch(IllegalArgumentException e){
					throw new SAXParseException("Error converting from String to coordinate."
							+ " Invalid Character?"
							, new LocatorImpl());
				}
				if(pointCount == 0){
					temp.setInformation(pointName2);
				}
				toFill.add(temp);
				pointCount++; 
				//for testing: print out the point I just added.
				//System.out.println(temp.getInformation() + " " + temp.getLat() + "," + temp.getLon() + "," + temp.getElevationInMeters());
				
			}
			
		}
		
		
	}
   
   

   


}  