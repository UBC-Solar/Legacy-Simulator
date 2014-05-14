package com.ubcsolar.map;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;  
import javax.xml.parsers.SAXParserFactory;  

import org.xml.sax.Attributes;  
import org.xml.sax.SAXException;  
import org.xml.sax.helpers.DefaultHandler;  
  
public class SaxKmlParser extends DefaultHandler{
	
   
public <InputClass extends List<Point>> InputClass parseToPoints(InputClass toFill, String file) throws SAXException, IOException, ParserConfigurationException{  
  
   // obtain and configure a SAX based parser  
   SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();  
  
   // obtain object for SAX parser  
   SAXParser saxParser = saxParserFactory.newSAXParser();  
  
   // default handler for SAX handler class  
   // all three methods are written in handler's body  
   DefaultHandler defaultHandler = new DefaultHandler(){  
    String nameTag="close";  
    String coordinatesTag="close";  
    String placemarkTag = "open";
    String pointName = "";
    String coordinates = "";
      
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
    	 System.out.println("Name: " + pointName);
    	 nameTag = "close";  
     }  
     if (qName.equalsIgnoreCase("coordinates")) {  
    	 coordinatesTag = "close";  
     }  
     if (qName.equalsIgnoreCase("point")) {
    	 System.out.println(coordinates);
    	 coordinates = "";
    	 System.out.println("I would make a point here");
    	 
     }  
     if (qName.equalsIgnoreCase("linestring")) {  
    	 System.out.println(coordinates);
    	 
         coordinates = "";
    	 System.out.println("I would make a list of points here"); 
     }  
    }  
   };  
     
   // parse the XML specified in the given path and uses supplied  
   // handler to parse the document  
   // this calls startElement(), endElement() and character() methods  
   // accordingly  
   saxParser.parse(file, defaultHandler);  
   
  return toFill;
}

}  