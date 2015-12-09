package com.ubcsolar.map;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.PointOfInterest;
import com.ubcsolar.common.Route;

public class JdomkmlInterface {

	private Document myDoc;
	private String loadedFileName;
	private Route chachedRoute;
	
	public JdomkmlInterface(String filename) throws IOException, JDOMException {
		dropCurrentAndLoad(filename);
	}
	
	
	/**
	 * Note that this won't save any changes that have been made. 
	 * @param filename
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void dropCurrentAndLoad(String filename) throws IOException, JDOMException{
		//disconnect? 
		//TODO check for and drop extra stuff if it's an absolute file path. 
		this.loadedFileName = filename;
		try {
	         File inputFile = new File(filename);

	         SAXBuilder saxBuilder = new SAXBuilder();

	         myDoc = saxBuilder.build(inputFile);
	         
		}
		catch(IOException e){
			throw e;
		} catch (JDOMException e) {
			throw e;
		}
		
		this.chachedRoute = turnInToRoute(this.myDoc);
	}
	
	
	private Route turnInToRoute(Document myDoc2) throws JDOMException {
		//Documentation: https://developers.google.com/kml/documentation/kmlreference
		//TODO download just the route and then the entire map
		//(two different options on Google Maps) to make sure it works for both
		Element rootElement = myDoc2.getRootElement();
		Route toReturn = null;
		Element documentNode;
		Namespace theNameSpace = rootElement.getNamespace();
		if(rootElement.getName() == "Document"){
			documentNode = rootElement;
		}else{
			documentNode = rootElement.getChild("Document", theNameSpace);
		}
		
		
		String nameOfDocument = documentNode.getChildText("name",theNameSpace);
		List<Element> placemarks = documentNode.getChildren("Placemark",theNameSpace);
		ArrayList<GeoCoord> track = new ArrayList<GeoCoord>();
		ArrayList<PointOfInterest> pois = new ArrayList<PointOfInterest>();
		/*
		 * <Point>
<LineString>
<LinearRing>
<Polygon>
<MultiGeometry>
<gx:MultiTrack>
<Model>
<gx:Track>
		 */
		for(Element placeMarkToCheck : placemarks){
		//Note: element names are case-sensitive. 
			if(placeMarkToCheck.getChild("Point",theNameSpace) != null){
				String name = placeMarkToCheck.getChildText("name",theNameSpace);
				System.out.println("name: " + name);
				GeoCoord location = parseString(placeMarkToCheck.getChild("Point",theNameSpace).getChildText("coordinates",theNameSpace));
				String description = "";
				//TODO add in support for description (it's a 'cdata' tag, so I'm not sure)
				pois.add(new PointOfInterest(location, name, description));
			}
			if(placeMarkToCheck.getChild("LineString",theNameSpace) != null){
				ArrayList<GeoCoord> theTrack = parseTrack(placeMarkToCheck.getChild("LineString",theNameSpace).getChildText("coordinates",theNameSpace));
				if(theTrack != null){
				track.addAll(theTrack);
				}
			}
			//TODO add in support for the others. 
		}
		
		return new Route(nameOfDocument, new ArrayList<GeoCoord>(), new ArrayList<PointOfInterest>());
		
	}


	private ArrayList<GeoCoord> parseTrack(String childText) {
		// TODO Auto-generated method stub
		return null;
	}


	private GeoCoord parseString(String childText) throws JDOMException {
		System.out.println("coordinate: " + childText);
		childText = childText.replaceAll("\\s", ""); //to get rid of any tabs or spaces. 
		String[] coordinatePieces = childText.split("[,\\s]+");
		if(coordinatePieces.length<3){ //i.e not a valid coordinate. (even altitude 0 would be ok)
			throw new JDOMException("Not three parts to this coordinate. "
					+ "May have had whitespace or newlines between lat, long, and elevation");
		}
		GeoCoord toReturn;
		try{
		toReturn = new GeoCoord(Double.parseDouble(coordinatePieces[0]),
								Double.parseDouble(coordinatePieces[1]),
								Double.parseDouble(coordinatePieces[2]));
		}
		catch(IllegalArgumentException e){ //if we can't parse into a Double
			throw new JDOMException("Error converting from String to coordinate."
					+ " Invalid Character?");
		}
		
		return toReturn;
	}


	public Route getRoute(){
		return null;
	}
	
	
	/**
	 * Uses Google's Elevations API to get elevations for each coordinate
	 * @param resolution - how many Coordinates to send each URL request.
	 * Note that more coordinates reduces accuracy as per their API documentation
	 * Also note that we only have a fixed number of calls per 24 hour period.
	 */
	public void getElevationsFromGoogle(int resolution){
		
	}
	
	public void printToFile(String filename) throws IOException{
		FileWriter fileToPrintTo = new FileWriter(new File(filename));
		fileToPrintTo.write(new XMLOutputter().outputString(this.myDoc));
		fileToPrintTo.close();
	}
	
	
	
	
}
