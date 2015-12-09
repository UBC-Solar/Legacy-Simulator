package com.ubcsolar.map;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

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
	
	
	private Route turnInToRoute(Document myDoc2) {
		//Documentation: https://developers.google.com/kml/documentation/kmlreference
		//TODO download just the route and then the entire map
		//(two different options on Google Maps) to make sure it works for both
		Element rootElement = myDoc2.getRootElement();
		Route toReturn = null;
		Element documentNode;
		if(rootElement.getName() == "Document"){
			documentNode = rootElement;
		}else{
			documentNode = rootElement.getChild("Document");
		}
		
		String nameOfDocument = documentNode.getChildText("name");
		List<Element> placemarks = documentNode.getChildren("Placemark");
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
			if(placeMarkToCheck.getChild("Point") != null){
				String name = placeMarkToCheck.getChildText("Name");
				GeoCoord location = parseString(placeMarkToCheck.getChild("Point").getChildText("Coordinates"));
				String description = "";
				//TODO add in support for description (it's a 'cdata' tag, so I'm not sure)
				pois.add(new PointOfInterest(location, name, description));
			}
			if(placeMarkToCheck.getChild("LineString") != null){
				ArrayList<GeoCoord> theTrack = parseTrack(placeMarkToCheck.getChild("LineString").getChildText("Coordinates"));
				track.addAll(theTrack);
			}
			//TODO add in support for the others. 
		}
		
		return new Route(nameOfDocument, new ArrayList<GeoCoord>(), new ArrayList<PointOfInterest>());
		
	}


	private ArrayList<GeoCoord> parseTrack(String childText) {
		// TODO Auto-generated method stub
		return null;
	}


	private GeoCoord parseString(String childText) {
		System.out.println(childText);
		return null;
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
